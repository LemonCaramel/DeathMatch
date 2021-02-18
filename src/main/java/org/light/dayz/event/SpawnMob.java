package org.light.dayz.event;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpawnMob implements Listener {

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (world.getName().contains("dayz") && event.getEntityType() != EntityType.ZOMBIE) {
            event.setCancelled(true);
            Zombie zombie = (Zombie) world.spawnEntity(event.getLocation(), EntityType.ZOMBIE);
            zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(30.0);
            zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
            zombie.setHealth(50.0);
            zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            zombie.setSilent(true);
            zombie.setBaby(false);
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false), false);
        }
        else if (event.getEntityType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).getBaseValue() != 65.0) {
                zombie.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(30.0);
                zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                zombie.setHealth(50.0);
                zombie.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                zombie.setSilent(true);
                zombie.setBaby(false);
                zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false), false);

            }
        }
    }
}
