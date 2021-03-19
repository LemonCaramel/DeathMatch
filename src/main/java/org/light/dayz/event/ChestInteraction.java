package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.dayz.runnable.ExitRunnable;
import org.light.dayz.util.Regen;
import org.light.source.DeathMatch;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ChestInteraction implements Listener {

    public static HashMap<UUID, Integer> exitTime;
    private DeathMatch Plugin;

    public ChestInteraction(DeathMatch Plugin) {
        exitTime = new HashMap<>();
        this.Plugin = Plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getWorld().getName().contains("dayz")
                && GameController.contains(p.getUniqueId()) && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
            if (Regen.chestRegen.containsKey(event.getClickedBlock().getLocation())) {
                int amount = YamlConfig.instance.getRegen() - Regen.chestRegen.get(event.getClickedBlock().getLocation());
                if (amount == 0)
                    p.sendMessage("§c[ §f! §c] §f이미 누군가가 털어간 상자입니다. §b곧 리젠됩니다.");
                else
                    p.sendMessage("§c[ §f! §c] §f이미 누군가가 털어간 상자입니다. §b" + amount / 2 + "분 " + (amount % 2 == 0 ? 0 : 30) + "초후 리젠됩니다.");
            }
            else {
                Regen.chestRegen.put(event.getClickedBlock().getLocation(), 0);
                ThreadLocalRandom random = ThreadLocalRandom.current();
                Chest chest = (Chest) event.getClickedBlock().getState();
                chest.getBlockInventory().clear();
                int rand = random.nextInt(0, 15);
                switch (rand) {
                    case 0:
                    case 6:
                        chest.getInventory().addItem(Regen.getPotions().get(random.nextInt(0, Regen.getPotions().size())));
                        break;
                    case 1:
                        chest.getInventory().addItem(Regen.getPotions().get(random.nextInt(0, Regen.getPotions().size())));
                        chest.getInventory().addItem(Regen.items.get(random.nextInt(0, Regen.items.size())));
                        break;
                    case 2:
                        chest.getInventory().addItem(Regen.calcArmor());
                        break;
                    case 3:
                        chest.getInventory().addItem(Regen.items.get(random.nextInt(0, Regen.items.size())));
                        break;
                    case 5:
                    case 11:
                    case 14:
                        if (ThreadLocalRandom.current().nextInt(0, 11) < 2)
                            chest.getInventory().addItem(CrackShotApi.generateNotOPWeapon());
                        else
                            chest.getInventory().addItem(CrackShotApi.generateDayZWeapon());
                        break;
                    case 8:
                        chest.getInventory().addItem(Regen.items.get(random.nextInt(0, Regen.items.size())));
                        chest.getInventory().addItem(Regen.items.get(random.nextInt(0, Regen.items.size())));
                        break;
                    case 9:
                        chest.getInventory().addItem(Regen.calcArmor());
                        chest.getInventory().addItem(Regen.getPotions().get(random.nextInt(0, Regen.getPotions().size())));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @EventHandler
    public void exit(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getWorld().getName().contains("dayz") && GameController.contains(p.getUniqueId()) && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BEACON) {
            event.setCancelled(true);
            if (!exitTime.containsKey(p.getUniqueId())) {
                exitTime.put(p.getUniqueId(), 0);
                new ExitRunnable(p.getLocation(), p.getUniqueId(), exitTime).runTaskTimer(Plugin, 0L, 20L);
            }
        }
    }
}
