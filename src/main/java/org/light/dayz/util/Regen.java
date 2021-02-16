package org.light.dayz.util;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Regen {

    public static ArrayList<UUID> kitMap = new ArrayList<>();
    public static HashMap<Location, Long> chestRegen = new HashMap<>();

    public static boolean isWeaponGet(UUID data) {
        return kitMap.contains(data);
    }

    public static void addPlayer(UUID data) {
        if (!kitMap.contains(data))
            kitMap.add(data);
    }

    public static void clear() {
        kitMap.clear();
    }
}
