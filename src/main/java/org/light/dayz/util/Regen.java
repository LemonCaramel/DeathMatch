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
    public static HashMap<UUID, Integer> exitMap = new HashMap<>();
    public static HashMap<Location, Integer> chestRegen = new HashMap<>();
    public static ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(new ItemStack(Material.BREAD, 5), new ItemStack(Material.COOKED_BEEF, 2), new ItemStack(Material.APPLE, 10), new ItemStack(Material.GRILLED_PORK, 3), new ItemStack(Material.BEETROOT, 10), new ItemStack(Material.COOKED_CHICKEN, 3)));

    public static boolean isWeaponGet(UUID data) {
        return kitMap.containsKey(data);
    }

    public static void addPlayer(UUID data) {
        if (!kitMap.containsKey(data))
            kitMap.put(data, 0);
    }

    public static void addExitPlayer(UUID data) {
        exitMap.putIfAbsent(data, 0);
    }

    public static boolean isExitPlayer(UUID data) {
        return exitMap.containsKey(data);
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
                        if (!(entity instanceof Player) && !(entity instanceof Painting) && !(entity instanceof ArmorStand))
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
                        zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(30.0);
                        zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                        zombie.setHealth(40.0);
                        zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                        zombie.setSilent(true);
                        zombie.setBaby(false);
                        zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false), false);
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
            ArrayList<UUID> delEUIDs = new ArrayList<>();
            for (UUID uuid : kitMap.keySet()) {
                kitMap.put(uuid, kitMap.get(uuid) + 1);
                if (kitMap.get(uuid) >= 360)
                    delUIDs.add(uuid);
            }
            for (UUID id : delUIDs)
                kitMap.remove(id);
            for (UUID uuid : exitMap.keySet()) {
                exitMap.put(uuid, exitMap.get(uuid) + 1);
                if (exitMap.get(uuid) >= 720)
                    delEUIDs.add(uuid);
            }
            for (UUID id : delEUIDs)
                exitMap.remove(id);
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
    public static ItemStack calcArmor() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int rand = random.nextInt(0, 101);
        if (rand < 10)
            return new ItemStack(Material.LEATHER_HELMET);
        else if (rand <= 20)
            return new ItemStack(Material.LEATHER_CHESTPLATE);
        else if (rand <= 40)
            return new ItemStack(Material.LEATHER_LEGGINGS);
        else if (rand <= 50)
            return new ItemStack(Material.LEATHER_BOOTS);
        else if (rand <= 55)
            return new ItemStack(Material.CHAINMAIL_HELMET);
        else if (rand <= 60)
            return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        else if (rand <= 65)
            return new ItemStack(Material.CHAINMAIL_LEGGINGS);
        else if (rand <= 72)
            return new ItemStack(Material.CHAINMAIL_BOOTS);
        else if (rand <= 78)
            return new ItemStack(Material.IRON_HELMET);
        else if (rand <= 84)
            return new ItemStack(Material.IRON_CHESTPLATE);
        else if (rand <= 90)
            return new ItemStack(Material.IRON_LEGGINGS);
        else if (rand <= 96)
            return new ItemStack(Material.IRON_BOOTS);
        else if (rand <= 97) {
            if (ThreadLocalRandom.current().nextInt(0,3) == 0)
                return new ItemStack(Material.DIAMOND_HELMET);
            else
                return new ItemStack(Material.IRON_HELMET);
        }
        else if (rand <= 98) {
            if (ThreadLocalRandom.current().nextInt(0,3) == 0)
                return new ItemStack(Material.DIAMOND_CHESTPLATE);
            else
                return new ItemStack(Material.IRON_CHESTPLATE);
        }
        else if (rand <= 99) {
            if (ThreadLocalRandom.current().nextInt(0, 3) == 0)
                return new ItemStack(Material.DIAMOND_LEGGINGS);
            else
                return new ItemStack(Material.IRON_LEGGINGS);
        }
        else {
            if (ThreadLocalRandom.current().nextInt(0,3) == 0)
                return new ItemStack(Material.DIAMOND_BOOTS);
            else
                return new ItemStack(Material.IRON_BOOTS);
        }

    }

    public static ArrayList<ItemStack> getPotions() {
        return new ArrayList<>(Arrays.asList(createItemStack(Material.PAPER, "§c[ §f! §c] §f붕대", (short)0, " ", " §8-  §f사용시 체력을 일부 회복합니다. (사용시간 3초)", " "), createItemStack(Material.MAGMA_CREAM, "§c[ §f! §c] §f에너지 드링크", (short)0, " ", " §8-  §f사용시 이동속도가 증가합니다. (사용시간 3초)", " "), createItemStack(Material.SUGAR, "§c[ §f! §c] §f구급상자", (short)0, " ", " §8-  §f사용시 체력을 모두 회복합니다. (사용시간 5초)", " "), createItemStack(Material.END_ROD, "§c[ §f! §c] §f치료제", (short)0, " ", " §8-  §f골절도 치료가능한 치료제", " §8-  §f사용시 상태이상을 모두 제거합니다. (사용시간 3초)", " ")));
    }

    public static ItemStack createItemStack(Material data, String name, short color, String... lore) {
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        stack.setDurability(color);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createItemStack(Material data, String name, short color, int amount, String... lore) {
        ItemStack stack = new ItemStack(data);
        ItemMeta meta = stack.getItemMeta();
        stack.setDurability(color);
        stack.setAmount(amount);
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }
}
