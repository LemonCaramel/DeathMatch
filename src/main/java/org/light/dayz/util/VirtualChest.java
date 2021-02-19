package org.light.dayz.util;

import me.DeeCaaD.CrackShotPlus.CSPapi;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class VirtualChest {


    public static HashMap<UUID, Inventory> chest = new HashMap<>();
    public static HashMap<UUID, Inventory> chest2 = new HashMap<>();

    public static void openChest(Player p, boolean first) {
        if (first) {
            if (chest.containsKey(p.getUniqueId()))
                p.openInventory(chest.get(p.getUniqueId()));
            else {
                Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 1");
                chest.put(p.getUniqueId(), inventory);
                p.openInventory(inventory);
            }
        }
        else {
            if (chest2.containsKey(p.getUniqueId()))
                p.openInventory(chest2.get(p.getUniqueId()));
            else {
                Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 2");
                chest2.put(p.getUniqueId(), inventory);
                p.openInventory(inventory);
            }
        }
    }

    public static ArrayList<String> toConfig(Inventory inventory) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null || stack.getType() == Material.AIR)
                list.add("AIR");
            else if (CrackShotApi.getCSID(stack) != null)
                list.add(CrackShotApi.getCSID(stack));
            else
                list.add(stack.getType().toString() + ";" + stack.getAmount());
        }
        return list;
    }

    public static void toInventory(UUID key, ArrayList<String> list, boolean first) {
        Inventory inv;
        if (first)
            inv = Bukkit.createInventory(null, 54, "§0창고 1");
        else
            inv = Bukkit.createInventory(null, 54, "§0창고 2");
        for (int i = 0; i < 54; i++) {
            String val = list.get(i);
            if (val.contains("AIR"))
                continue;
            if (val.contains(";")) {
                String[] array = val.split(";");
                String item = array[0];
                int amount = array.length == 2 ? toInt(array[1]) : 1;
                if (Material.getMaterial(item) != null) {
                    Material material = Material.getMaterial(item);
                    if (material == Material.PAPER || material == Material.MAGMA_CREAM || material == Material.SUGAR || material == Material.END_ROD) {
                        switch (material) {
                            case PAPER:
                                inv.setItem(i, Regen.createItemStack(Material.PAPER, "§c[ §f! §c] §f붕대", (short) 0, amount, " ", " §8-  §f사용시 체력을 일부 회복합니다. (사용시간 3초)", " "));
                                break;
                            case MAGMA_CREAM:
                                inv.setItem(i, Regen.createItemStack(Material.MAGMA_CREAM, "§c[ §f! §c] §f에너지 드링크", (short) 0, amount, " ", " §8-  §f사용시 이동속도가 증가합니다. (사용시간 3초)", " "));
                                break;
                            case SUGAR:
                                inv.setItem(i, Regen.createItemStack(Material.SUGAR, "§c[ §f! §c] §f구급상자", (short) 0, amount, " ", " §8-  §f사용시 체력을 모두 회복합니다. (사용시간 5초)", " "));
                                break;
                            default:
                            case END_ROD:
                                inv.setItem(i, Regen.createItemStack(Material.END_ROD, "§c[ §f! §c] §f치료제", (short) 0, amount, " ", " §8-  §f골절도 치료가능한 치료제", " §8-  §f사용시 상태이상을 모두 제거합니다. (사용시간 3초)", " "));
                                break;
                        }
                    }
                    else
                        inv.setItem(i, new ItemStack(material, amount));
                }
                else if (CrackShotApi.getCSWeapon(item) != null)
                    inv.setItem(i, CSPapi.updateItemStackFeaturesNonPlayer(val, CrackShotApi.getCSWeapon(val)));
            }
            else {
                if (Material.getMaterial(val) != null)
                    inv.setItem(i, new ItemStack(Material.getMaterial(val)));
                else if (CrackShotApi.getCSWeapon(val) != null)
                    inv.setItem(i, CSPapi.updateItemStackFeaturesNonPlayer(val, CrackShotApi.getCSWeapon(val)));
            }
        }
        if (first)
            chest.put(key, inv);
        else
            chest2.put(key, inv);

    }

    public static int toInt(String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (Exception e) {
            return 1;
        }
    }

}
