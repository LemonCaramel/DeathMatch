package org.light.dayz.event;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnMob implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (world.getName().contains("dayz_") && event.getEntityType() != EntityType.ZOMBIE && event.getEntityType() != EntityType.PIG_ZOMBIE && event.getEntityType() != EntityType.ARMOR_STAND) {
            event.setCancelled(true);
            Zombie zombie = (Zombie) world.spawnEntity(event.getLocation(), EntityType.ZOMBIE);
            zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(30.0);
            zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60.0);
            zombie.setHealth(60.0);
            zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            zombie.setSilent(true);
            zombie.setBaby(false);
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false), false);
            if (ThreadLocalRandom.current().nextInt(0, 10) <= 1)
                world.spawnEntity(event.getLocation(), EntityType.PIG_ZOMBIE);
        }
        else if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 30.0) {
                zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(30.0);
                zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(60.0);
                zombie.setHealth(60.0);
                zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                zombie.setSilent(true);
                zombie.setBaby(false);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false), false);

            }

        }
        else if (event.getEntityType() == EntityType.PIG_ZOMBIE) {
            PigZombie zombie = (PigZombie)event.getEntity();
            if (zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 60.0) {
                zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(60.0);
                zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0);
                zombie.setHealth(100.0);
                zombie.setBaby(false);
                zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
                zombie.setSilent(true);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, true, false), false);
            }
        }
    }
}
