package org.light.source;

import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.Command.CommandController;

public class DeathMatch extends JavaPlugin {

    @Override
    public void onEnable(){
        getLogger().info("DeathMatch Plugin Enabled");
        getCommand("데스매치").setExecutor(new CommandController(this));
    }

    @Override
    public void onDisable(){
        getLogger().info("DeathMatch Plugin Disabled");
    }
}
