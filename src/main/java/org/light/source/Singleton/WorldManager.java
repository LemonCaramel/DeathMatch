package org.light.source.Singleton;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.ArrayList;

public class WorldManager {

    private static WorldManager instance;
    private ArrayList<String> worlds;

    static {
        instance = new WorldManager();
    }

    private WorldManager() {
        worlds = new ArrayList<>();
    }

    public static WorldManager getInstance() {
        return instance;
    }

    public void addWorld(String name) {
        if (!worlds.contains(name))
            worlds.add(name);
    }

    public boolean containWorld(String name) {
        return worlds.contains(name);
    }

    public void removeWorld(String name) {
        worlds.remove(name);
    }

    public void loadWorld() {
        for (String world : worlds) {
            WorldCreator worldCreator = new WorldCreator(world);
            worldCreator.type(WorldType.FLAT);
            worldCreator.generateStructures(false);
            worldCreator.environment(World.Environment.NORMAL);
            worldCreator.generatorSettings("0x0");
            worldCreator.createWorld();
        }
    }

    public ArrayList<String> getWorlds() {
        return worlds;
    }

    public void clear() {
        worlds.clear();
    }
}
