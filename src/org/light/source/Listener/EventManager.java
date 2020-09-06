package org.light.source.Listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
    public void onLeave(PlayerQuitEvent event){
        Player p = event.getPlayer();
        if (GameManager.getInstance().contains(p.getUniqueId()))
            GameManager.getInstance().removePlayer(p);
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
                        event.setRespawnLocation(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[0], DataManager.getInstance().getLocations()[1]));
                    }
                }
            }
        }
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (GameManager.getInstance().isgaming()){
            Player killer = event.getEntity().getKiller();
            Player victim = event.getEntity();
            if (GameManager.getInstance().contains(killer.getUniqueId()) && GameManager.getInstance().contains(victim.getUniqueId())){
                String knife = DataManager.getInstance().getWeaponName(-1);
                if (knife != null && CrackShotApi.getCSID(killer.getInventory().getItemInMainHand()).equalsIgnoreCase(knife)){
                    //칼로 죽였을경우 킬 수치 감소
                    for (UserMananger mananger : GameManager.getInstance().getUserlist()){
                        if (mananger.getUUID().equals(victim.getUniqueId()) && mananger.getKills() != 0) {
                            mananger.setKills(mananger.getKills() - 1);
                            //레벨다운
                            if (mananger.getKills() != 0 && (mananger.getKills() + 1) / DataManager.getInstance().getKilltolevel() != mananger.getKills() / DataManager.getInstance().getKilltolevel())
                                sendLevelDown(victim, (mananger.getKills() + 1) / DataManager.getInstance().getKilltolevel(), mananger.getKills()/ DataManager.getInstance().getKilltolevel());
                        }
                        else if (mananger.getUUID().equals(killer.getUniqueId()))
                            mananger.setKills(mananger.getKills()+1);
                    }
                }
                else{
                    for (UserMananger mananger : GameManager.getInstance().getUserlist()) {
                        if (mananger.getUUID().equals(killer.getUniqueId()))
                            mananger.setKills(mananger.getKills()+1);
                    }
                }
                UserMananger mgr = null;
                for (UserMananger mananger : GameManager.getInstance().getUserlist()){
                    if (mananger.getUUID().equals(killer.getUniqueId())) {
                        mgr = mananger;
                    }
                }
                if (mgr != null) {
                    if (DataManager.getInstance().getRounds() * DataManager.getInstance().getKilltolevel() <= mgr.getKills()) {
                        //승리!
                        for (UserMananger mananger : GameManager.getInstance().getUserlist()) {
                            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                            target.sendMessage("§c[ §fDeathMatch §6] §b" + killer.getName() + "§f님이 §6" + DataManager.getInstance().getRounds() + " §f레벨을 달성하여 게임을 승리하셨습니다!");
                        }
                        GameManager.getInstance().stop();
                    } else {
                        //레벨업 및 경고
                        if (mgr.getKills()-1 != 0 && (mgr.getKills() - 1) / DataManager.getInstance().getKilltolevel() != mgr.getKills() / DataManager.getInstance().getKilltolevel()) {
                            if (mgr.getKills() == DataManager.getInstance().getKilltolevel() * (DataManager.getInstance().getRounds() - 1))
                                for (UserMananger mananger : GameManager.getInstance().getUserlist()) {
                                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                                    target.sendMessage("§c[ §fDeathMatch §6] §b" + killer.getName() + " §f님이 §6" + (DataManager.getInstance().getRounds() - 1) + "§f레벨에 도달하셨습니다!");
                                }
                            killer.getInventory().setItem(0, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(mgr.getKills() / DataManager.getInstance().getKilltolevel())));
                            sendLevelUp(killer, (mgr.getKills() - 1) / DataManager.getInstance().getKilltolevel(), mgr.getKills() / DataManager.getInstance().getKilltolevel());
                        }
                    }
                }
                event.setDeathMessage("");
                for (UserMananger mananger : GameManager.getInstance().getUserlist()){
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    target.sendMessage("§c[ §fDeathMatch §6] §c" + killer.getName() + " §7➾ §b" + victim.getName());
                }
                RatingManager.getInstance().updateRank();
                event.getDrops().clear();

            }

        }
    }

    public void sendLevelUp(Player p, int back, int to){
        p.sendTitle("§c[ §fDeathMatch §6] §bLevel UP!", "§6" + back + " §f=> §b" + to, 5,50,5);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void sendLevelDown(Player p, int now, int to){
        p.sendTitle("§c[ §fDeathMatch §6] §cLevel Down..", "§6" + now + " §f=> §b" + to, 5,50,5);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }
}
