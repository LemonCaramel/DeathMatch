package org.light.dayz.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.light.dayz.util.VirtualChest;
import org.light.source.DeathMatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class YamlConfig {

    private DeathMatch Plugin;
    private File file;
    private File chestFile;
    private YamlConfiguration config;
    private YamlConfiguration chestConfig;
    private ArrayList<Location> locations;
    private ArrayList<String> helpWeapon;
    private ArrayList<String> denyWeapon;
    private int zKill;
    private int hKill;
    private int regen;
    public static YamlConfig instance;

    public YamlConfig(DeathMatch Plugin) {
        this.Plugin = Plugin;
        locations = new ArrayList<>();
        helpWeapon = new ArrayList<>();
        denyWeapon = new ArrayList<>();
        instance = this;
        zKill = 0;
        hKill = 0;
        file =  new File("plugins/" + Plugin.getDescription().getName() + "/Dayz-Config.yml");
        chestFile = new File("plugins/" + Plugin.getDescription().getName() + "/Dayz-Chest.yml");
        checkFile();
        config = YamlConfiguration.loadConfiguration(file);
        chestConfig = YamlConfiguration.loadConfiguration(chestFile);
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
        if (!chestFile.exists()) {
            try {
                chestFile.createNewFile();
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
            chestConfig.load(chestFile);
        }
        catch (IOException | InvalidConfigurationException e) {
            Bukkit.broadcastMessage("Error Load File");
        }
        regen = config.getInt("chest-regen", 10); //단위 분
        zKill = config.getInt("zombie-kill", 1);
        hKill = config.getInt("human-kill", 10);
        helpWeapon.clear();
        denyWeapon.clear();
        locations.clear();
        VirtualChest.chest.clear();
        VirtualChest.chest2.clear();
        helpWeapon.addAll(config.getStringList("first-weapon"));
        denyWeapon.addAll(config.getStringList("non-drop-weapon"));
        if (chestConfig.getConfigurationSection("chest") != null) {
            for (String key : chestConfig.getConfigurationSection("chest").getKeys(false)) {
                UUID uid = UUID.fromString(key);
                ArrayList<String> strings = new ArrayList<>(chestConfig.getStringList("chest." + key));
                VirtualChest.toInventory(uid, strings, true);
            }
        }
        if (chestConfig.getConfigurationSection("chest2") != null) {
            for (String key : chestConfig.getConfigurationSection("chest2").getKeys(false)) {
                UUID uid = UUID.fromString(key);
                ArrayList<String> strings = new ArrayList<>(chestConfig.getStringList("chest2." + key));
                VirtualChest.toInventory(uid, strings, false);
            }
        }
        if (config.getConfigurationSection("location") != null)
            for (String key : config.getConfigurationSection("location").getKeys(false))
                locations.add(getLocation("location." + key));


    }

    public void save() {
        chestConfig.set("chest", null);
        chestConfig.set("chest2", null);
        config.set("chest-regen", regen);
        config.set("zombie-kill", zKill);
        config.set("human-kill", hKill);
        config.set("first-weapon", helpWeapon);
        config.set("non-drop-weapon", denyWeapon);
        for (int i = 0; i < locations.size(); i++)
            setLocation("location." + i, locations.get(i));
        for (UUID key : VirtualChest.chest.keySet()) {
            ArrayList<String> list = VirtualChest.toConfig(VirtualChest.chest.get(key));
            chestConfig.set("chest." + key.toString(), list);
        }
        for (UUID key : VirtualChest.chest2.keySet()) {
            ArrayList<String> list = VirtualChest.toConfig(VirtualChest.chest2.get(key));
            chestConfig.set("chest2." + key.toString(), list);
        }
        try {
            config.save(file);
            chestConfig.save(chestFile);
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

    public ArrayList<String> getDenyWeapon() {
        return denyWeapon;
    }

    public void setDenyWeapon(ArrayList<String> denyWeapon) {
        this.denyWeapon = denyWeapon;
    }
}