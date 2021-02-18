package org.light.dayz.event;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.data.DayZData;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.source.DeathMatch;

import java.util.concurrent.ThreadLocalRandom;

public class WeaponDamage implements Listener {

    private YamlConfig config;
    private DeathMatch Plugin;

    public WeaponDamage(YamlConfig config, DeathMatch Plugin) {
        this.config = config;
        this.Plugin = Plugin;
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent event) {
        World world = event.getPlayer().getWorld();
        Player shooter = event.getPlayer();
        if (world.getName().contains("dayz")) {
            if (GameController.contains(shooter.getUniqueId()) && event.getVictim() instanceof Player && GameController.contains(event.getVictim().getUniqueId())) {
                if (shooter.hasPotionEffect(PotionEffectType.INVISIBILITY))
                    event.setCancelled(true);
                Player victim = (Player) event.getVictim();
                double distance = Math.round(shooter.getLocation().distance(victim.getLocation()) * 100.0) / 100.0;
                if (victim.getHealth() - event.getDamage() <= 0) {
                    if (event.isHeadshot())
                        Bukkit.broadcastMessage("§c" + shooter.getName() + "§f의 " + event.getWeaponTitle() + "을(를) 사용한 헤드샷으로 §b" + victim.getName() + "§f이(가) 사망했습니다. (" + distance + "m)");
                    else
                        Bukkit.broadcastMessage("§c" + shooter.getName() + "§f의 " + event.getWeaponTitle() + "(으)로 인해 §b" + victim.getName() + "§f이(가) 사망했습니다. (" + distance + "m)");
                    DayZData data = GameController.getData(shooter.getUniqueId());
                    data.setKill(data.getKill() + 1);
                    data.setAccumulateMoney(data.getAccumulateMoney() + config.getHKill());
                    shooter.sendActionBar("§c[ §f! §c] §4" + data.getKill() + "§f킬");
                    event.setCancelled(true);
                    for (ItemStack stack : victim.getInventory().getContents())
                        if (stack != null && !stack.equals(victim.getInventory().getItemInOffHand()))
                            world.dropItem(victim.getLocation(), stack);
                    victim.getInventory().clear();
                    GameController.removePlayer(victim, false);
                }
            }
            else if (event.getVictim() instanceof Player)
                event.setDamage(0);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        //그외 사유로 죽었을때
        Entity entity = event.getEntity();
        if (entity.getWorld().getName().contains("dayz")) {
            if (entity instanceof Zombie) {
                Zombie victim = (Zombie) entity;
                Player killer = victim.getKiller();
                if (killer != null && GameController.contains(killer.getUniqueId())) {
                    int zKill = config.getZKill();
                    killer.sendActionBar("§f+ §6" + zKill + "§f포인트!");
                    DayZData data = GameController.getData(killer.getUniqueId());
                    data.setAccumulateMoney(data.getAccumulateMoney() + zKill);
                }
            }
            else if (entity instanceof Player) {
                Player victim = (Player) entity;
                if (GameController.contains(victim.getUniqueId())) {
                    for (ItemStack stack : victim.getInventory().getContents())
                        if (stack != null && !stack.equals(victim.getInventory().getItemInOffHand()))
                            victim.getWorld().dropItem(victim.getLocation(), stack);

                    victim.getInventory().clear();
                    victim.spigot().respawn();
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(Plugin, () -> {
            if (GameController.contains(event.getPlayer().getUniqueId())) {
                GameController.removePlayer(event.getPlayer(), false);
            }
        }, 1L);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getDamage() >= 15) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1200, 4, true, true), true);
                p.sendMessage("§c[ §f! §c] §f높은곳에서 떨어져 골절상태가 되었습니다. 치료제를 이용하여 상태이상을 제거할 수 있습니다.");
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Zombie && event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (random.nextInt(0, 101) <= 5) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 1200, 1, true, false), true);
                victim.sendMessage("§c[ §f! §c] §f좀비에게 물려 §c감염§f상태가 되어 3초마다 데미지를 입게 됩니다. 치료제를 사용하십시오");
            }
        }
    }
}
