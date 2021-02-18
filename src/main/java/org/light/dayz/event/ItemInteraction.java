package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.game.GameController;
import org.light.source.DeathMatch;

import java.util.HashMap;
import java.util.UUID;

public class ItemInteraction implements Listener {

    private DeathMatch Plugin;

    public ItemInteraction(DeathMatch Plugin) {
        this.Plugin = Plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Location loc = p.getLocation().clone();
        ItemStack stack = p.getInventory().getItemInMainHand();
        if (stack != null && stack.getType() != Material.AIR && stack.getItemMeta().getDisplayName() != null) {
            event.setCancelled(true);
            String name = stack.getItemMeta().getDisplayName();
            if (name.contains("붕대") || name.contains("구급상자") || name.contains("에너지 드링크") || name.contains("치료제")) {
                if (p.hasPotionEffect(PotionEffectType.FAST_DIGGING) && p.hasPotionEffect(PotionEffectType.SLOW))
                    p.sendMessage("§c[ §f! §c] §b이미 아이템을 사용중입니다.");
                else {
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                        p.getInventory().setItemInMainHand(stack);
                    }
                    else
                        p.getInventory().setItemInMainHand(null);
                    if (name.contains("구급상자")) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100,1,true, false),false);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100,100,true, false),false);
                        p.sendMessage("§c[ §f! §c] §f구급상자를 사용중입니다.. (5초, 3칸이상 멀어질시 취소)");
                        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
                            if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) <= 3.0) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 5, true, true), true);
                            }
                            else if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) > 3.0) {
                                ItemStack clone = stack.clone();
                                clone.setAmount(1);
                                p.sendMessage("§c거리가 너무 멀어져 사용이 취소되었습니다.");
                                p.getInventory().addItem(clone);
                            }
                        }, 100L);
                    }
                    else {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60,1,true, false),false);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60,100,true, false),false);
                        p.sendMessage("§c[ §f! §c] §f아이템을 사용중입니다.. (5초, 3칸이상 멀어질시 취소)");
                        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
                            if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) <= 3.0) {
                                if (name.contains("붕대"))
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25, 7, true, true), true);
                                else if (name.contains("에너지 드링크"))
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 900, 2, true, true), true);
                                else {
                                    p.removePotionEffect(PotionEffectType.SLOW);
                                    p.removePotionEffect(PotionEffectType.LUCK);
                                }
                            }
                            else if (GameController.contains(p.getUniqueId()) && loc.distance(p.getLocation()) > 3.0) {
                                ItemStack clone = stack.clone();
                                clone.setAmount(1);
                                p.sendMessage("§c거리가 너무 멀어져 사용이 취소되었습니다.");
                                p.getInventory().addItem(clone);
                            }
                        }, 60L);
                    }
                }
            }
        }
    }

}
