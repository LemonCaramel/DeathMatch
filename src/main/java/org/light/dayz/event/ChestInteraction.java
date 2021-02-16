package org.light.dayz.event;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.dayz.util.Regen;
import org.light.source.Singleton.CrackShotApi;
import org.yaml.snakeyaml.Yaml;

import java.util.concurrent.ThreadLocalRandom;

public class ChestInteraction implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getWorld().getName().contains("dayz") && GameController.contains(p.getUniqueId()) && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.CHEST) {
            if (Regen.chestRegen.containsKey(event.getClickedBlock().getLocation())) {
                int amount = YamlConfig.instance.getRegen() - Regen.chestRegen.get(event.getClickedBlock().getLocation());
                p.sendMessage("§c[ §f! §c] §f이미 누군가가 털어간 상자입니다. §b" + amount / 2 + "분 " + (amount % 2 == 0 ? 0 : 30) + "초후 리젠됩니다.");
            }
            else {
                Regen.chestRegen.put(event.getClickedBlock().getLocation(), 0);
                ThreadLocalRandom random = ThreadLocalRandom.current();
                Chest chest = (Chest) event.getClickedBlock().getState();
                chest.getBlockInventory().clear();
                chest.getInventory().addItem(CrackShotApi.generateNotOPWeapon());
                if (random.nextInt(0, 101) <= 5)
                    chest.getInventory().addItem(CrackShotApi.generateNotOPWeapon());
                if (random.nextInt(0, 2) == 0)
                    chest.getInventory().addItem(Regen.getPotions().get(random.nextInt(0, Regen.getPotions().size())));
                else
                    chest.getInventory().addItem(Regen.items.get(random.nextInt(0, Regen.items.size())));
            }
        }
    }

    @EventHandler
    public void exit(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (p.getWorld().getName().contains("dayz") && GameController.contains(p.getUniqueId()) && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.BEACON) {
            GameController.removePlayer(p, true);
        }
    }
}
