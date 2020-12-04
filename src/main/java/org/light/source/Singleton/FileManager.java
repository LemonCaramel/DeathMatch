package org.light.source.Singleton;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.DeathMatch;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private static FileManager manager;
    private File folder;
    private File file;
    private YamlConfiguration config;
    private DeathMatch Plugin;

    static {
        manager = new FileManager();
    }

    private FileManager(){
        Plugin = JavaPlugin.getPlugin(DeathMatch.class);
        folder = new File("plugins/" + Plugin.getDescription().getName());
        file = new File("plugins/" + Plugin.getDescription().getName() + "/config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        if (!folder.exists())
            folder.mkdir();
    }

    public static FileManager getInstance(){
        return manager;
    }

    public void save(){
        DataManager manager = DataManager.getInstance();
        config.set("Round", manager.getRounds());
        config.set("KillLevel", manager.getKilltolevel());
        config.set("Time", manager.getTime());
        config.set("RequireJoin", manager.getMinimumUser());
        config.set("JoinReward", manager.getJoinMoney());
        config.set("FirstReward", manager.getFirstReward());
        config.set("SecondReward", manager.getSecondReward());
        config.set("ThirdReward", manager.getThirdReward());
        config.set("LocationAmount", manager.getLocationAmount());
        config.set("KillMaintain", manager.getKillMaintain());
        config.set("MaxReRoll", manager.getMaxReroll());
        config.set("ReRollMoney", manager.getReRollMoney());
        config.set("WaitTime", manager.getWaitTime());
        if (manager.getLocations() != null){
            for (int i = 0; i < DataManager.getInstance().getLocationAmount(); i++){
                config.set("Location." + (i+1), manager.getLocations()[i]);
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        DataManager manager = DataManager.getInstance();
        manager.setRounds(config.getInt("Round"));
        manager.setTime(config.getInt("Time"));
        manager.setKilltolevel(config.getInt("KillLevel"));
        manager.setMinimumUser(config.getInt("RequireJoin"));
        manager.setJoinMoney(config.getInt("JoinReward", 30));
        manager.setFirstReward(config.getInt("FirstReward", 150));
        manager.setSecondReward(config.getInt("SecondReward", 100));
        manager.setThirdReward(config.getInt("ThirdReward", 50));
        manager.setKillMaintain(config.getInt("KillMaintain", 10));
        manager.setMaxReroll(config.getInt("MaxReRoll", 10));
        manager.setReRollMoney(config.getInt("ReRollMoney", 200));
        manager.setWaitTime(config.getInt("WaitTime", 60));
        if (config.get("Location.1") != null && config.getInt("LocationAmount") != 0){
            int amount = config.getInt("LocationAmount");
            for (int i = 0; i < amount; i++){
                DataManager.getInstance().setLocations((Location) config.get("Location." + (i+1)),i+1);
            }
        }
    }
}
