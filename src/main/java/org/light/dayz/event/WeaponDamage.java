package org.light.dayz.event;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.data.DayZData;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.source.DeathMatch;

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
                if (victim.getHealth() - event.getDamage() <= 0) {
                    if (event.isHeadshot())
                        Bukkit.broadcastMessage("§c" + shooter.getName() + "§f의 " + event.getWeaponTitle() + "을(를) 사용한 헤드샷으로 §b" + victim.getName() + "§f이(가) 사망했습니다.");
                    else
                        Bukkit.broadcastMessage("§c" + shooter.getName() + "§f의 " + event.getWeaponTitle() + "(으)로 인해 §b" + victim.getName() + "§f이(가) 사망했습니다.");
                    DayZData data = GameController.getData(shooter.getUniqueId());
                    data.setKill(data.getKill() + 1);
                    data.setAccumulateMoney(data.getAccumulateMoney() + config.getHKill());
                    shooter.sendActionBar("§c[ §f! §c] §4" + data.getKill() + "§f킬");
                    event.setCancelled(true);
                    for (ItemStack stack : victim.getInventory().getContents())
                        if (stack != null)
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
                        if (stack != null)
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
}
