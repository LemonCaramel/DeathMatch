package org.light.dayz.runnable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.dayz.game.GameController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExitRunnable extends BukkitRunnable {

    private HashMap<UUID, Integer> exitMap;
    private UUID data;
    private Location start;

    public ExitRunnable(Location start, UUID data, HashMap<UUID, Integer> map) {
        this.exitMap = map;
        this.data = data;
        this.start = start;
    }

    @Override
    public void run() {
        Player p = Bukkit.getPlayer(data);
        int value = exitMap.get(data);
        if (p == null || !GameController.contains(p.getUniqueId()) || p.getLocation().distance(start) >= 5.0 || !p.getWorld().getName().equalsIgnoreCase("dayz")) {
            exitMap.remove(data);
            cancel();
            if (p != null && p.getLocation().distance(start) >= 5.0) {
                p.sendMessage("§c[ §f! §c] §c탈출 위치로부터 5m이상 떨어져 탈출이 취소되었습니다.");

            }
        }
        else {
            if (value >= 5) {
                GameController.removePlayer(p, true);
                exitMap.remove(p.getUniqueId());
                cancel();
            }
            else {
                p.sendTitle("§c[ §f! §c] §b탈출!", "§6" + (5 - value) + "§f초후 탈출합니다.", 0, 25, 0);
                exitMap.put(data, value + 1);
            }
        }

    }

}
