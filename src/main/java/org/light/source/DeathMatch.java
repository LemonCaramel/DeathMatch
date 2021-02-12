package org.light.source;

import com.comphenix.protocol.PacketType;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.Command.CommandController;
import org.light.source.Command.JoinLeaveCommand;
import org.light.source.Command.KillDeathCommand;
import org.light.source.Game.AfkManager;
import org.light.source.Listener.EventManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Runnable.WeatherRunnable;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.FileManager;
import org.light.source.Singleton.KillDeathFileManager;
import com.comphenix.protocol.ProtocolLibrary;
import org.light.source.packet.SneakAdapter;

public class DeathMatch extends JavaPlugin {

    @Override
    public void onEnable(){
        getLogger().info("DeathMatch Plugin Enabled");
        loadCommand();
        getServer().getPluginManager().registerEvents(new EventManager(this), this);
        FileManager.getInstance().load();
        CrackShotApi.generateWeaponMap();
        MinimizeLogger.getInstance().logStart();
        KillDeathFileManager.getInstance().load();
        ProtocolLibrary.getProtocolManager().addPacketListener(new SneakAdapter(this, PacketType.Play.Server.ENTITY_METADATA));
        new AfkManager().runTaskTimer(this, 0L, 20L);
        new WeatherRunnable(this);


    }

    @Override
    public void onDisable(){
        getLogger().info("DeathMatch Plugin Disabled");
        FileManager.getInstance().save();
        MinimizeLogger.getInstance().forceSaveLog();
        KillDeathFileManager.getInstance().save();
    }

    public void loadCommand(){
        JoinLeaveCommand joinCommand = new JoinLeaveCommand();
        KillDeathCommand killDeathCommand = new KillDeathCommand();
        getCommand("데스매치").setExecutor(new CommandController(this));
        getCommand("참여").setExecutor(joinCommand);
        getCommand("나가기").setExecutor(joinCommand);
        getCommand("킬뎃").setExecutor(killDeathCommand);
        getCommand("랭크").setExecutor(killDeathCommand);
    }
}
