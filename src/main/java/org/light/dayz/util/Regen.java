package org.light.dayz.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.source.DeathMatch;
import org.light.source.Singleton.CrackShotApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Regen {

    public static int count = 0;
    public static DeathMatch Plugin = JavaPlugin.getPlugin(DeathMatch.class);
    public static YamlConfig config = YamlConfig.instance;
    public static HashMap<UUID, Integer> kitMap = new HashMap<>();
    public static HashMap<Location, Integer> chestRegen = new HashMap<>();
    public static ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(new ItemStack(Material.BREAD, 5), new ItemStack(Material.COOKED_BEEF, 2), new ItemStack(Material.APPLE, 10), new ItemStack(Material.GRILLED_PORK, 3), new ItemStack(Material.BEETROOT, 10), new ItemStack(Material.COOKED_CHICKEN, 3)));
    public static boolean isWeaponGet(UUID data) {
        return kitMap.containsKey(data);
    }

    public static void addPlayer(UUID data) {
        if (!kitMap.containsKey(data))
            kitMap.put(data, 0);
    }

    public static void clear() {
        kitMap.clear();
    }

    public static void startTask() {
        Bukkit.getScheduler().runTaskTimer(Plugin, () -> {
            count++;
            if (count == 18) {
                for (UUID data : GameController.gameData.keySet()) {
                    Player p = Bukkit.getPlayer(data);
                    p.sendMessage("§c[ §f! §c] §f1분뒤 모든 엔티티가 제거됩니다.");
                }
            }
            else if (count == 19) {
                for (UUID data : GameController.gameData.keySet()) {
                    Player p = Bukkit.getPlayer(data);
                    p.sendMessage("§c[ §f! §c] §f30초뒤 모든 엔티티가 제거됩니다.");
                }
            }
            else if (count == 20) {
                World world = Bukkit.getWorld("dayz");
                if (world != null) {
                    for (Entity entity : world.getEntities()) {
                        if (!(entity instanceof Player) && !(entity instanceof Painting))
                            entity.remove();
                    }
                }
                for (UUID data : GameController.gameData.keySet()) {
                    Player p = Bukkit.getPlayer(data);
                    p.sendMessage("§c[ §f! §c] §f모든 엔티티가 제거되었습니다.");
                }
                count = 0;
            }
            for (UUID data : GameController.gameData.keySet()) {
                Player p = Bukkit.getPlayer(data);
                Location min = p.getLocation().clone().subtract(new Vector(30, 1, 30));
                Location max = p.getLocation().clone().add(new Vector(30, 1, 30));
                for (int i = 0; i < 2; i++) {
                    if (p.getWorld().getName().contains("dayz")) {
                        Zombie zombie = (Zombie) p.getWorld().spawnEntity(getSpawnLocation(min, max), EntityType.ZOMBIE);
                        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(65.0);
                        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50.0);
                        zombie.setHealth(50.0);
                        zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                        zombie.setSilent(true);
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2, true, false), false);
                    }
                }
            }
            ArrayList<Location> delLocations = new ArrayList<>();
            for (Location location : chestRegen.keySet()) {
                if (location.getBlock().getType() != Material.CHEST || chestRegen.get(location) >= config.getRegen())
                    delLocations.add(location);
                else
                    chestRegen.put(location, chestRegen.get(location) + 1);
            }
            for (Location location : delLocations)
                chestRegen.remove(location);
            delLocations.clear();
            ArrayList<UUID> delUIDs = new ArrayList<>();
            for (UUID uuid : kitMap.keySet()) {
                kitMap.put(uuid, kitMap.get(uuid) + 1);
                if (kitMap.get(uuid) >= 360)
                    delUIDs.add(uuid);
            }
            for (UUID id : delUIDs)
                kitMap.remove(id);
        }, 0L, 600L);
    }

    private static Location getSpawnLocation(Location first, Location second) {
        int tryCount = 0;
        double x, xx, y, yy, z, zz;
        int gapX, gapY, gapZ;
        if (first.getX() < second.getY()) {
            x = first.getX();
            xx = second.getX();
        }
        else {
            x = second.getX();
            xx = first.getX();
        }
        if (first.getY() < second.getY()) {
            y = first.getY();
            yy = second.getY();
        }
        else {
            y = second.getY();
            yy = first.getY();
        }
        if (first.getZ() < second.getZ()) {
            z = first.getZ();
            zz = second.getZ();
        }
        else {
            z = second.getZ();
            zz = first.getZ();
        }
        gapX = (int) ((int) xx - x);
        gapY = (int) ((int) yy - y);
        gapZ = (int) ((int) zz - z);

        if (gapX == 0 && gapY == 0 && gapZ == 0)
            return first;
        Location min = new Location(first.getWorld(), x, y, z);
        if (gapX > 0) {
            int add = ThreadLocalRandom.current().nextInt(0, gapX + 1);
            min.setX(min.getX() + add);
        }
        if (gapY > 0) {
            int add = ThreadLocalRandom.current().nextInt(0, gapY + 1);
            min.setY(min.getY() + add);

        }
        if (gapZ > 0) {
            int add = ThreadLocalRandom.current().nextInt(0, gapZ + 1);
            min.setZ(min.getZ() + add);
        }
        while (min.getBlock().getType() != Material.AIR || min.clone().add(new Vector(0, -1, 0)).getBlock().getType() == Material.AIR) {
            if (tryCount == 30)
                break;
            min = new Location(first.getWorld(), x, y, z);
            if (gapX > 0) {
                int add = ThreadLocalRandom.current().nextInt(0, gapX + 1);
                min.setX(min.getX() + add);
            }
            if (gapY > 0) {
                int add = ThreadLocalRandom.current().nextInt(0, gapY + 1);
                min.setY(min.getY() + add);

            }
            if (gapZ > 0) {
                int add = ThreadLocalRandom.current().nextInt(0, gapZ + 1);
                min.setZ(min.getZ() + add);
            }
            tryCount++;
        }
        return min;
    }
    public static ArrayList<ItemStack> getArmors() {
        return new ArrayList<>(Arrays.asList(new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS), new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS)));
    }

    public static ArrayList<ItemStack> getPotions() {
        ItemStack potion1 = new ItemStack(Material.POTION);
        PotionMeta meta1 = (PotionMeta) potion1.getItemMeta();
        meta1.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 2, true, true), false);
        potion1.setItemMeta(meta1);
        ItemStack potion2 = new ItemStack(Material.POTION);
        PotionMeta meta2 = (PotionMeta) potion2.getItemMeta();
        meta2.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 2, true, true), false);
        potion2.setItemMeta(meta2);
        ItemStack potion3 = new ItemStack(Material.POTION);
        PotionMeta meta3 = (PotionMeta) potion3.getItemMeta();
        meta3.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 1, true, true), false);
        potion3.setItemMeta(meta3);
        ItemStack potion4 = new ItemStack(Material.POTION);
        PotionMeta meta4 = (PotionMeta) potion4.getItemMeta();
        meta4.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 4, true, true), false);
        potion4.setItemMeta(meta4);
        return new ArrayList<>(Arrays.asList(potion1, potion2, potion3, potion4));
    }
}
