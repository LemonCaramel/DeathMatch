package org.light.source.Singleton;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryFactory {

    public static Inventory createInventory(String name, int size){
        return Bukkit.createInventory(null, size, Component.text(name));
    }

    public static ItemStack createItemStack(Material data, String name, String[] lore, short color){
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof Damageable) ((Damageable) meta).setDamage(color);
        meta.displayName(Component.text(name));
        meta.lore(createLore(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static List<Component> createLore(String[] arr){
        if (arr == null) return null;
        else {
            List<Component> returnable = new ArrayList<>();
            for (String lore : arr) returnable.add(Component.text(lore));
            return returnable;
        }
    }
}
