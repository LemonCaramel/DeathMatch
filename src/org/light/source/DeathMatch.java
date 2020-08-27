package org.light.source;

import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.Command.CommandController;
import org.light.source.Listener.EventManager;

public class DeathMatch extends JavaPlugin {

    @Override
    public void onEnable(){
        getLogger().info("DeathMatch Plugin Enabled");
        getCommand("데스매치").setExecutor(new CommandController(this));
        getServer().getPluginManager().registerEvents(new EventManager(this), this);
    }

    @Override
    public void onDisable(){
        getLogger().info("DeathMatch Plugin Disabled");
    }
}
