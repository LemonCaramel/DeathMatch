package org.light.dayz.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.light.source.DeathMatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class YamlConfig {

    private DeathMatch Plugin;
    private File file;
    private YamlConfiguration config;
    private ArrayList<Location> locations;
    private ArrayList<String> helpWeapon;
    private int zKill;
    private int hKill;
    private int regen;
    public static YamlConfig instance;

    public YamlConfig(DeathMatch Plugin) {
        this.Plugin = Plugin;
        locations = new ArrayList<>();
        helpWeapon = new ArrayList<>();
        instance = this;
        zKill = 0;
        hKill = 0;
        file =  new File("plugins/" + Plugin.getDescription().getName() + "/Dayz-Config.yml");
        checkFile();
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void checkFile() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        //location.1 ~ location.2 ~
        try {
            config.load(file);
        }
        catch (IOException | InvalidConfigurationException e) {
            Bukkit.broadcastMessage("Error Load File");
        }
        regen = config.getInt("chest-regen", 10); //단위 분
        zKill = config.getInt("zombie-kill", 1);
        hKill = config.getInt("human-kill", 10);
        helpWeapon.clear();
        locations.clear();
        helpWeapon.addAll(config.getStringList("first-weapon"));
        if (config.getConfigurationSection("location") != null)
            for (String key : config.getConfigurationSection("location").getKeys(false))
                locations.add(getLocation("location." + key));
    }

    public void save() {
        config.set("chest-regen", regen);
        config.set("zombie-kill", zKill);
        config.set("human-kill", hKill);
        config.set("first-weapon", helpWeapon);
        for (int i = 0; i < locations.size(); i++)
            setLocation("location." + i, locations.get(i));
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    public ArrayList<String> getHelpWeapon() {
        return helpWeapon;
    }

    public void setHelpWeapon(ArrayList<String> helpWeapon) {
        this.helpWeapon = helpWeapon;
    }

    private void setLocation(String path, Location value) {
        config.set(path + ".X", value.getX());
        config.set(path + ".Y", value.getY());
        config.set(path + ".Z", value.getZ());
        config.set(path + ".World", value.getWorld().getName());
        config.set(path + ".Pitch", value.getPitch());
        config.set(path + ".Yaw", value.getYaw());
    }

    private Location getLocation(String path) {
        double x = config.getDouble(path + ".X", -1);
        double y = config.getDouble(path + ".Y", -1);
        double z = config.getDouble(path + ".Z", -1);
        float pitch = (float) config.getDouble(path + ".Pitch", 0);
        float yaw = (float) config.getDouble(path + ".Yaw", 0);
        String world = config.getString(path + ".World", null);
        if (world == null || Bukkit.getWorld(world) == null)
            return null;
        else
            return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

    }


    public int getZKill() {
        return zKill;
    }

    public void setZKill(int zKill) {
        this.zKill = zKill;
    }

    public int getHKill() {
        return hKill;
    }

    public void setHKill(int hKill) {
        this.hKill = hKill;
    }

    public int getRegen() {
        return regen;
    }

    public void setRegen(int regen) {
        this.regen = regen;
    }
}