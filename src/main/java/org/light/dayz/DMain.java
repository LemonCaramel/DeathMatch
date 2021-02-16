package org.light.dayz;

import org.bukkit.Bukkit;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.event.FoodLevel;
import org.light.dayz.event.PreventShoot;
import org.light.dayz.event.SpawnMob;
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
        Bukkit.getServer().getPluginManager().registerEvents(new FoodLevel(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PreventShoot(), Plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new SpawnMob(), Plugin);
    }

    public void makeDisable() {
        config.save();
    }


}
