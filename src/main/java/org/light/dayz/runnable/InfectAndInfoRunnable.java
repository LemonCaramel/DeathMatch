package org.light.dayz.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.dayz.game.GameController;

import java.util.HashMap;
import java.util.UUID;

public class InfectAndInfoRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (UUID data : GameController.gameData.keySet()) {
            Player p = Bukkit.getPlayer(data);
            if (p != null ) {
                if (p.hasPotionEffect(PotionEffectType.LUCK)){
                    p.damage(4.0);
                }
                if (p.isSneaking()) {
                    p.sendActionBar("§c[ §f! §c] §b" + p.getName() + " §c체력 §7: §f" + (int)p.getHealth() + " §4hp §8| §6배고픔 §7: §f" + p.getFoodLevel() + " §8| §f위치 §7: " + toLocation(p.getLocation()));
                }
            }
        }
    }

    public String toLocation(Location loc) {
        return "§7[ §fX : " + (int)loc.getX() + " §fY : " + (int)loc.getY() + " §fZ : " + (int)loc.getZ() + " §7]";
    }
}
