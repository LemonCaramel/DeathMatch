package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.util.Regen;
import org.light.dayz.util.VirtualChest;
import org.light.source.Game.GameManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.EconomyApi;

import java.util.concurrent.ThreadLocalRandom;

public class InventoryClick implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getRawSlot() == -999) {
            ItemStack trash = event.getCursor();
            if (!GameManager.getInstance().contains(p.getUniqueId()) && CrackShotApi.getCSID(trash) != null && !p.isOp()) {
                Item item = p.getWorld().dropItem(p.getLocation(), trash);
                item.setVelocity(p.getLocation().getDirection());
                p.setItemOnCursor(null);
            }
            return;
        }

        if (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40) {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().getItemMeta().getDisplayName() != null && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("."))
            event.setCurrentItem(null);
        if (event.getInventory().getTitle().contains("보급품")) {
            event.setCancelled(true);
            if (CrackShotApi.getCSID(event.getCurrentItem()) != null) {
                if (p.getInventory().addItem(event.getCurrentItem()).isEmpty()) {
                    p.sendMessage("§c[ §f! §c] §f보급품을 선택하였습니다.");
                    Regen.addPlayer(p.getUniqueId());
                    p.closeInventory();
                }
                else {
                    p.sendMessage("§c[ §f! §c] §f인벤토리가 꽉 차있어 아이템을 지급하지 못했습니다.");
                }
            }
        }

        else if (event.getInventory().getTitle().contains("창고 선택")) {
            ItemStack stack = event.getCurrentItem();
            event.setCancelled(true);
            if (event.getRawSlot() >= 0 && event.getRawSlot() <= 4) {
                if (stack.getType() == Material.CHEST)
                    VirtualChest.openChest(p, toNumber(event.getRawSlot()));
                else if (stack.getType() == Material.BARRIER) {
                    if (event.getRawSlot() == 2) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 2500) {
                            EconomyApi.getInstance().subtractMoney(p, 2500);
                            MinimizeLogger.getInstance().appendLog(p.getName() + "님이 3번 창고 구매");
                            p.sendMessage("§b3번 창고를 구매하셨습니다.");
                            Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 3");
                            VirtualChest.chest3.put(p.getUniqueId(), inventory);
                            VirtualChest.selectChest(p);
                        }
                        else
                            p.sendMessage("§c돈이 부족합니다.");
                    }
                    else if (event.getRawSlot() == 3) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 5000) {
                            EconomyApi.getInstance().subtractMoney(p, 5000);
                            MinimizeLogger.getInstance().appendLog(p.getName() + "님이 4번 창고 구매");
                            p.sendMessage("§b4번 창고를 구매하셨습니다.");
                            Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 4");
                            VirtualChest.chest4.put(p.getUniqueId(), inventory);
                            VirtualChest.selectChest(p);
                        }
                        else
                            p.sendMessage("§c돈이 부족합니다.");
                    }
                    else {
                        if (EconomyApi.getInstance().currentMoney(p) >= 7500) {
                            EconomyApi.getInstance().subtractMoney(p, 7500);
                            MinimizeLogger.getInstance().appendLog(p.getName() + "님이 5번 창고 구매");
                            p.sendMessage("§b5번 창고를 구매하셨습니다.");
                            Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 5");
                            VirtualChest.chest5.put(p.getUniqueId(), inventory);
                            VirtualChest.selectChest(p);
                        }
                        else
                            p.sendMessage("§c돈이 부족합니다.");
                    }
                }
            }
        }
        else if (event.getInventory().getTitle().contains("상점")) {
            ItemStack stack = event.getCurrentItem();
            event.setCancelled(true);
            if (event.getRawSlot() >= 0 && event.getRawSlot() <= 9) {
                if (stack.getType() == Material.ROTTEN_FLESH) {
                    int amount = getZombieAmount(p.getInventory());
                    if (event.getClick() == ClickType.LEFT) {
                        if (amount >= 1) {
                            removeZombieItem(p.getInventory(), 1);
                            EconomyApi.getInstance().giveMoney(p, 1);
                            p.sendMessage("§c[ §f! §c] §f썩은 고기 1개를 팔아 §61§f원을 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c썩은 고기가 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (amount >= 1) {
                            removeZombieItem(p.getInventory());
                            EconomyApi.getInstance().giveMoney(p, amount);
                            p.sendMessage("§c[ §f! §c] §f썩은 고기 " + amount + "개를 팔아 §6" + amount + "§f원을 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c썩은 고기가 모자랍니다.");

                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        if (amount >= 10) {
                            removeZombieItem(p.getInventory(), 10);
                            EconomyApi.getInstance().giveMoney(p, 10);
                            p.sendMessage("§c[ §f! §c] §f썩은 고기 10개를 팔아 §610§f원을 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c썩은 고기가 모자랍니다.");
                    }
                }
                else if (stack.getType() == Material.DIAMOND_SWORD) {
                    if (event.getClick() == ClickType.LEFT) {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if (item != null && item.getType() != Material.AIR && CrackShotApi.getCSID(item) != null) {
                            String id = CrackShotApi.getCSID(item);
                            int amount = getPrice(id);
                            for (int i = 0; i < amount; i++)
                                p.getInventory().addItem(new ItemStack(Material.ROTTEN_FLESH));
                            p.sendMessage("§c[ §f! §c] §f" + id + "를 팔아 §6" + amount + "§f개의 썩은 고기를 흭득하였습니다.");
                            p.getInventory().setItemInMainHand(null);
                        }
                        else
                            p.sendMessage("§c총을 손에 들고 사용해주세요.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (checkGun(p.getInventory())) {
                            int amount = sellAllWeapon(p.getInventory());
                            for (int i = 0; i < amount; i++)
                                p.getInventory().addItem(new ItemStack(Material.ROTTEN_FLESH));
                            p.sendMessage("§c[ §f! §c] §f총기 전체를 팔아 §6" + amount + "§f개의 썩은 고기를 흭득하였습니다.");
                        }
                        else
                            p.sendMessage("§c인벤토리에 총이 존재하지 않습니다.");
                    }
                }
                else if (stack.getType() == Material.BREAD) {
                    if (event.getClick() == ClickType.LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 5) {
                            p.getInventory().addItem(new ItemStack(Material.BREAD));
                            EconomyApi.getInstance().subtractMoney(p, 5);
                            p.sendMessage("§c[ §f! §c] §f빵 1개를 §65§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.SHIFT_LEFT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 320) {
                            p.getInventory().addItem(new ItemStack(Material.BREAD, 64));
                            EconomyApi.getInstance().subtractMoney(p, 320);
                            p.sendMessage("§c[ §f! §c] §f빵 64개를 §6320§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                    else if (event.getClick() == ClickType.RIGHT) {
                        if (EconomyApi.getInstance().currentMoney(p) >= 50) {
                            p.getInventory().addItem(new ItemStack(Material.BREAD, 10));
                            EconomyApi.getInstance().subtractMoney(p, 50);
                            p.sendMessage("§c[ §f! §c] §f빵 10개를 §650§f원에 구매하였습니다.");
                        }
                        else
                            p.sendMessage("§c돈이 모자랍니다.");
                    }
                }
            }
        }
        else if (!GameManager.getInstance().contains(event.getWhoClicked().getUniqueId())
                && event.getClickedInventory() != null
                && event.getClickedInventory().getTitle() != null
                && !event.getClickedInventory().getTitle().contains("채널")
                && !checkDeathMatchTitle(event.getClickedInventory().getTitle())) {
            event.setCancelled(false);
            if (p.getInventory().getItemInOffHand() != null || p.getInventory().getItemInOffHand().getType() != Material.AIR)
                p.getInventory().setItemInOffHand(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        // 이게 왜 CRAFTING으로 분류되는지는 모르겠음
        if (event.getInventory().getType() == InventoryType.CRAFTING && event.getRawSlots().contains(45))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        ItemStack stack = event.getItemDrop().getItemStack();
        event.setCancelled(GameManager.getInstance().contains(p.getUniqueId()) || stack.getType() == Material.SKULL_ITEM || checkDummy(stack));

    }

    public boolean checkDummy(ItemStack stack) {
        return stack.getItemMeta().getDisplayName() != null && stack.getItemMeta().getDisplayName().equalsIgnoreCase(".");
    }

    public boolean checkDeathMatchTitle(String title) {
        return title.contains("랭크");
    }

    public VirtualChest.Number toNumber(int value) {
        if (value == 0)
            return VirtualChest.Number.ONE;
        else if (value == 1)
            return VirtualChest.Number.TWO;
        else if (value == 2)
            return VirtualChest.Number.THREE;
        else if (value == 3)
            return VirtualChest.Number.FOUR;
        else if (value == 4)
            return VirtualChest.Number.FIVE;
        else
            return VirtualChest.Number.ONE;
    }

    public int getZombieAmount(Inventory inventory) {
        int amount = 0;
        for (ItemStack stack : inventory.getContents())
            if (stack != null && stack.getType() == Material.ROTTEN_FLESH)
                amount += stack.getAmount();
        return amount;
    }

    public void removeZombieItem(Inventory inventory) {
        for (ItemStack stack : inventory.getContents())
            if (stack != null && stack.getType() == Material.ROTTEN_FLESH)
                inventory.remove(stack);
    }

    public void removeZombieItem(Inventory inventory, int amount) {
        int count = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack != null && stack.getType() == Material.ROTTEN_FLESH) {
                if (count >= amount)
                    break;
                else if (amount - count < stack.getAmount()) {
                    ItemStack copyStack = stack.clone();
                    copyStack.setAmount(stack.getAmount() - (amount - count));
                    inventory.setItem(i, copyStack);
                    count = amount;
                }
                else {
                    count += stack.getAmount();
                    inventory.remove(stack);
                }
            }
        }
    }

    public boolean checkGun(Inventory inventory) {
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() != Material.AIR && CrackShotApi.getCSID(stack) != null)
                return true;
        }
        return false;
    }

    public int sellAllWeapon(Inventory inventory) {
        int price = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() != Material.AIR && CrackShotApi.getCSID(stack) != null) {
                price += getPrice(CrackShotApi.getCSID(stack));
                inventory.remove(stack);
            }
        }
        return price;
    }
    public int getPrice(String value) {
        if (value.contains("_"))
            return ThreadLocalRandom.current().nextInt(5, 16);
        else
            return ThreadLocalRandom.current().nextInt(1, 6);
    }
}
