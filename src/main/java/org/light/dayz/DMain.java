package org.light.dayz;

import org.bukkit.Bukkit;
import org.light.dayz.command.ChestCommand;
import org.light.dayz.command.DropCommand;
import org.light.dayz.command.GameCommand;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.event.*;
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
        Plugin.getCommand("드랍").setExecutor(new DropCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new FoodLevel(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PreventShoot(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new SpawnMob(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new WeaponDamage(config, Plugin), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ChestInteraction(), Plugin);
        Regen.startTask();
    }

    public void makeDisable() {
        config.save();
    }


}
