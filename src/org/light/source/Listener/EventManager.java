package org.light.source.Listener;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.DataManager;
import org.light.source.Singleton.RatingManager;

public class EventManager implements Listener {

    private DeathMatch Plugin;

    public EventManager(DeathMatch Plugin){
        this.Plugin = Plugin;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player target = event.getPlayer();
        target.setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void onPhysics(PlayerInteractEvent event){
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND)
            event.setCancelled(true);
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player p = event.getPlayer();
        if (GameManager.getInstance().contains(p.getUniqueId()))
            GameManager.getInstance().removePlayer(p);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (GameManager.getInstance().isgaming() && GameManager.getInstance().contains(event.getEntity().getUniqueId()))
            event.getDrops().clear();
    }

    @EventHandler
    public void onWeaponDamage(WeaponDamageEntityEvent event){
        if (event.getVictim() instanceof Player && GameManager.getInstance().isgaming()){
            Player killer = event.getPlayer();
            Player victim = (Player) event.getVictim();
            if (GameManager.getInstance().contains(killer.getUniqueId()) && GameManager.getInstance().contains(victim.getUniqueId())){
                if (victim.getHealth() - event.getDamage() <= 0) {
                    sendMsg("§c[ §fDeathMatch §6] §c" + killer.getName() + " §7➾ §b" + victim.getName());
                    String knife = DataManager.getInstance().getWeaponName(-1);
                    String csval = CrackShotApi.getCSID(killer.getInventory().getItemInMainHand());
                    UserMananger mgr = null;
                    for (UserMananger mananger : GameManager.getInstance().getUserlist()) {
                        if (knife != null && csval != null && csval.equalsIgnoreCase(knife)) {
                            //칼로 죽였을경우 킬 수치 감소
                            if (mananger.getUUID().equals(victim.getUniqueId()) && mananger.getKills() != 0) {
                                mananger.setKills(mananger.getKills() - 1);
                                //레벨다운
                                int now = (mananger.getKills() + 1) / DataManager.getInstance().getKilltolevel();
                                int to = mananger.getKills() / DataManager.getInstance().getKilltolevel();
                                if (mananger.getKills() != 0 && now != to)
                                    sendLevelDown(victim, now, to);
                            }
                        }
                        if (mananger.getUUID().equals(killer.getUniqueId())) {
                            mananger.setKills(mananger.getKills() + 1);
                            mgr = mananger;
                        }
                    }
                    if (mgr != null) {
                        if (DataManager.getInstance().getRounds() * DataManager.getInstance().getKilltolevel() <= mgr.getKills()) {
                            //승리!
                            sendMsg("§c[ §fDeathMatch §6] §b" + killer.getName() + "§f님이 §6" + DataManager.getInstance().getRounds() + " §f레벨을 달성하여 게임을 승리하셨습니다!");
                            GameManager.getInstance().stop();
                        }
                        else {
                            //레벨업 및 경고
                            int back = (mgr.getKills() - 1) / DataManager.getInstance().getKilltolevel();
                            int to = mgr.getKills() / DataManager.getInstance().getKilltolevel();
                            if (mgr.getKills() - 1 != 0 && back != to) {
                                if (mgr.getKills() == DataManager.getInstance().getKilltolevel() * (DataManager.getInstance().getRounds() - 1))
                                    sendMsg("§c[ §fDeathMatch §6] §b" + killer.getName() + " §f님이 §6" + (DataManager.getInstance().getRounds() - 1) + "§f레벨에 도달하셨습니다!");
                                killer.getInventory().setItem(0, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(to)));
                                if (killer.getInventory().getHelmet().getType() == Material.PUMPKIN)
                                    killer.getInventory().setHelmet(new ItemStack(Material.AIR));
                                sendLevelUp(killer, back, to);
                            }
                        }
                    }

                    RatingManager.getInstance().updateRank();
                    for (UserMananger victimgr : GameManager.getInstance().getUserlist()) {
                        if (victimgr.getUUID().equals(victim.getUniqueId())) {
                            victim.getInventory().clear();
                            event.setDamage(0.0);
                            victim.setHealth(20.0);
                            victim.teleport(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[0], DataManager.getInstance().getLocations()[1]));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 5, true, false));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 100,true,false));
                            victim.getInventory().setItem(0, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(victimgr.getKills() / DataManager.getInstance().getKilltolevel())));
                            victim.getInventory().setItem(1, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(-1)));

                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        if (GameManager.getInstance().isgaming()){
            Player target = event.getPlayer();
            if (GameManager.getInstance().contains(target.getUniqueId())){
                for (UserMananger mananger : GameManager.getInstance().getUserlist()){
                    if (mananger.getUUID().equals(target.getUniqueId())){
                        target.getInventory().setItem(0, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(mananger.getKills() / DataManager.getInstance().getKilltolevel())));
                        if (DataManager.getInstance().getWeaponName(-1) != null)
                            target.getInventory().setItem(1, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(-1)));
                        Bukkit.getScheduler().runTaskLater(Plugin, ()-> target.teleport(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[0], DataManager.getInstance().getLocations()[1])), 1L);
                    }
                }
            }
        }
    }

    public void sendLevelUp(Player p, int back, int to){
        p.sendTitle("§c[ §fDeathMatch §6] §bLevel UP!", "§6" + back + " §f=> §b" + to, 5,50,5);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void sendLevelDown(Player p, int now, int to){
        p.sendTitle("§c[ §fDeathMatch §6] §cLevel Down..", "§6" + now + " §f=> §b" + to, 5,50,5);
    }

    public void sendMsg(String msg){
        for (UserMananger mananger : GameManager.getInstance().getUserlist()) {
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendMessage(msg);
        }
    }
}
