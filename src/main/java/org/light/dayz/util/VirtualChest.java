package org.light.dayz.util;

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

    public static void openChest(Player p) {
        if (chest.containsKey(p.getUniqueId()))
            p.openInventory(chest.get(p.getUniqueId()));
        else {
            Inventory inventory = Bukkit.createInventory(null, 54, "§0창고");
            chest.put(p.getUniqueId(), inventory);
            p.openInventory(inventory);
        }
    }

    public static ArrayList<String> toConfig(Inventory inventory) {
        ArrayList<String> list = new ArrayList<>();
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null)
                list.add("AIR");
            else if (CrackShotApi.getCSID(stack) != null)
                list.add(CrackShotApi.getCSID(stack));
            else
                list.add(stack.getType().toString());
        }
        return list;
    }

    public static void toInventory(UUID key, ArrayList<String> list) {
        Inventory inv = Bukkit.createInventory(null, 54, "§0창고");
        for (String val : list) {
            if (Material.getMaterial(val) != null)
                inv.addItem(new ItemStack(Material.getMaterial(val)));
            else if (CrackShotApi.getCSWeapon(val) != null)
                inv.addItem(CrackShotApi.getCSWeapon(val));
        }
        chest.put(key, inv);
    }
}
