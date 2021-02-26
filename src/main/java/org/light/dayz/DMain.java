package org.light.dayz;

import org.bukkit.Bukkit;
import org.light.dayz.command.*;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.event.*;
import org.light.dayz.runnable.InfectAndInfoRunnable;
import org.light.dayz.util.Regen;
import org.light.source.DeathMatch;

public class DMain {

    private DeathMatch Plugin;
    private YamlConfig config;

    public DMain(DeathMatch Plugin) {
        this.Plugin = Plugin;
        config = new YamlConfig(Plugin);
    }

    public void makeEnable() {
        config.load();
        Plugin.getCommand("dayz").setExecutor(new GameCommand(config));
        Plugin.getCommand("창고").setExecutor(new ChestCommand());
        Plugin.getCommand("상점").setExecutor(new SellCommand());
        Plugin.getCommand("쓰레기통").setExecutor(new TrashCommand());
        Plugin.getCommand("버그제거").setExecutor(new BugCommand(Plugin));
        Plugin.getCommand("비상탈출").setExecutor(new EmergencyExit(Plugin));
        Bukkit.getServer().getPluginManager().registerEvents(new FoodLevel(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PreventShoot(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new SpawnMob(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new WeaponDamage(config, Plugin), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ChestInteraction(Plugin), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ItemInteraction(Plugin), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ArmorStandInteraction(), Plugin);
        new InfectAndInfoRunnable().runTaskTimer(Plugin, 0L, 40L);
        Regen.startTask();
    }

    public void makeDisable() {
        config.save();
    }


}
