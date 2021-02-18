package org.light.dayz.util;

import me.DeeCaaD.CrackShotPlus.CSPapi;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class VirtualChest {


    public static HashMap<UUID, Inventory> chest = new HashMap<>();
    public static HashMap<UUID, Inventory> chest2 = new HashMap<>();

    public static void openChest(Player p, boolean first) {
        if (first) {
            if (chest.containsKey(p.getUniqueId()))
                p.openInventory(chest.get(p.getUniqueId()));
            else {
                Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 1");
                chest.put(p.getUniqueId(), inventory);
                p.openInventory(inventory);
            }
        }
        else {
            if (chest2.containsKey(p.getUniqueId()))
                p.openInventory(chest2.get(p.getUniqueId()));
            else {
                Inventory inventory = Bukkit.createInventory(null, 54, "§0창고 2");
                chest2.put(p.getUniqueId(), inventory);
                p.openInventory(inventory);
            }
        }
    }

    public static ArrayList<String> toConfig(Inventory inventory) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null || stack.getType() == Material.AIR)
                list.add("AIR");
            else if (CrackShotApi.getCSID(stack) != null)
                list.add(CrackShotApi.getCSID(stack));
            else
                list.add(stack.getType().toString());
        }
        return list;
    }

    public static void toInventory(UUID key, ArrayList<String> list, boolean first) {
        if (first) {
            Inventory inv = Bukkit.createInventory(null, 54, "§0창고 1");
            for (int i = 0; i < 54; i++) {
                String val = list.get(i);
                if (val.equalsIgnoreCase("AIR"))
                    continue;
                if (Material.getMaterial(val) != null && !val.equalsIgnoreCase("AIR"))
                    inv.setItem(i, new ItemStack(Material.getMaterial(val)));
                else if (CrackShotApi.getCSWeapon(val) != null)
                    inv.setItem(i, CSPapi.updateItemStackFeaturesNonPlayer(val,CrackShotApi.getCSWeapon(val)));
            }
            chest.put(key, inv);
        }
        else {
            Inventory inv = Bukkit.createInventory(null, 54, "§0창고 2");
            for (int i = 0; i < 54; i++) {
                String val = list.get(i);
                if (val.equalsIgnoreCase("AIR"))
                    continue;
                if (Material.getMaterial(val) != null)
                    inv.setItem(i, new ItemStack(Material.getMaterial(val)));
                else if (CrackShotApi.getCSWeapon(val) != null)
                    inv.setItem(i, CSPapi.updateItemStackFeaturesNonPlayer(val,CrackShotApi.getCSWeapon(val)));
            }
            chest2.put(key, inv);
        }
    }
}
