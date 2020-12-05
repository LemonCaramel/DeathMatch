package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class InventoryFactory {

    public static Inventory createInventory(String name, int size){
        return Bukkit.createInventory(null, size, name);
    }

    public static ItemStack createItemStack(Material data, String name, String[] lore, short color){
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        stack.setDurability(color);
        meta.setDisplayName(name);
        meta.setLore(createLore(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static List<String> createLore(String[] arr){
        if (arr == null)
            return null;
        else
            return Arrays.asList(arr);
    }
}
