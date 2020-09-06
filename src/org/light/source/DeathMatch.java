package org.light.source;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.Command.CommandController;
import org.light.source.Listener.EventManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Singleton.FileManager;

public class DeathMatch extends JavaPlugin {

    @Override
    public void onEnable(){
        getLogger().info("DeathMatch Plugin Enabled");
        getCommand("데스매치").setExecutor(new CommandController(this));
        getServer().getPluginManager().registerEvents(new EventManager(this), this);
        FileManager.getInstance().load();
        MinimizeLogger.getInstance().logStart();
    }

    @Override
    public void onDisable(){
        getLogger().info("DeathMatch Plugin Disabled");
        FileManager.getInstance().save();
        MinimizeLogger.getInstance().forceSaveLog();
    }
}
