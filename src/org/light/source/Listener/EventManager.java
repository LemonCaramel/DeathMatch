package org.light.source.Listener;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import moe.caramel.caramellibrarylegacy.user.CaramelUserData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;
import org.light.source.Phone.PhoneObject;
import org.light.source.Singleton.*;

import java.util.Iterator;

public class EventManager implements Listener {

    private DeathMatch Plugin;

    public EventManager(DeathMatch Plugin){
        this.Plugin = Plugin;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (event.getMessage().contains("ChatCraft")) {
            event.setCancelled(true);
            Player target = event.getPlayer();
            if (!PhoneManager.getInstance().contains(target.getUniqueId())) {
                PhoneManager.getInstance().addObject(target.getUniqueId(), true);
                Bukkit.broadcastMessage("§6" + target.getName() + "§f님이 §b모바일로 접속하여 참여가 제한되었습니다.");
            }
            else
                target.sendMessage("§c고의로 치지마세요..");

        }
    }
    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        if (GameManager.getInstance().contains(p.getUniqueId()) && GameManager.getInstance().isgaming())
            event.setCancelled(true);
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if (CrackShotApi.getCSID(event.getItemDrop().getItemStack()) != null)
            event.setCancelled(true);

    }

    @EventHandler
    public void onOffhandMove(PlayerSwapHandItemsEvent event){
        Player target = event.getPlayer();
        if (GameManager.getInstance().isgaming() && GameManager.getInstance().contains(target.getUniqueId()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player target = event.getPlayer();
        target.setGameMode(GameMode.ADVENTURE);
        ScoreboardObject.getInstance().setScoreboard(target);
        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Plugin, ()->{
            if (CaramelUserData.getData().getUser(target.getUniqueId()) != null && !GameManager.getInstance().contains(target.getUniqueId()))
                CaramelUserData.getData().getUser(target.getUniqueId()).setInvincibility(true);
        }, 20L);
        Bukkit.getScheduler().runTaskLater(Plugin, ()->{
            if (!PhoneManager.getInstance().contains(target.getUniqueId()))
                PhoneManager.getInstance().addObject(target.getUniqueId(), false);
        },40L);


    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player p = event.getPlayer();
        if (GameManager.getInstance().contains(p.getUniqueId()))
            GameManager.getInstance().removePlayer(p);
        if (PhoneManager.getInstance().contains(p.getUniqueId())){
            PhoneManager.getInstance().getPhoneObjects().removeIf(phoneObject -> phoneObject.getUuid().equals(p.getUniqueId()));
        }

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
                    sendKillMsg(killer,victim,"§c" + killer.getName() + " §7➾ §b" + victim.getName());
                    String knife = DataManager.getInstance().getWeaponName(-1);
                    String csval = event.getWeaponTitle();
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
                            killer.setHealth(20.0);
                        }
                    }
                    RatingManager.getInstance().updateRank();
                    for (UserMananger victimgr : GameManager.getInstance().getUserlist()) {
                        if (victimgr.getUUID().equals(victim.getUniqueId())) {
                            victim.getInventory().clear();
                            event.setDamage(0.0);
                            victim.setHealth(20.0);
                            String disname = killer.getInventory().getItemInMainHand().getType() == Material.AIR ? "X" : killer.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() ? killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName() : "X";
                            if (knife.equalsIgnoreCase(csval))
                                sendRespawn(victim, killer.getName(), killer.getInventory().getItemInMainHand().getItemMeta().getDisplayName(), true);
                            else
                                sendRespawn(victim, killer.getName(), disname, false);
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
                                if (killer.getInventory().getHelmet() != null)
                                    killer.getInventory().setHelmet(new ItemStack(Material.AIR));
                                sendLevelUp(killer, back, to);
                            }
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
                        sendRespawn(target, "MineCraft", "§c<none> §7<<x>>", false);
                    }
                }
            }
        }
        else{
            Player target = event.getPlayer();
            Bukkit.getScheduler().runTaskLater(Plugin, ()-> target.teleport(DataManager.getInstance().getLocations()[0]), 1L);
        }
    }

    public void sendLevelUp(Player p, int back, int to){
        p.sendTitle("§c[ §fDeathMatch §6] §bLevel UP!", "§6" + back + " §f=> §b" + to, 5,50,5);
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        p.setLevel(to);
        p.setExp(GameManager.getInstance().calcLevelProgress(to));
    }

    public void sendLevelDown(Player p, int now, int to){
        p.sendTitle("§c[ §fDeathMatch §6] §cLevel Down..", "§6" + now + " §f=> §b" + to, 5,50,5);
        p.setLevel(to);
        p.setExp(GameManager.getInstance().calcLevelProgress(to));
    }

    public void sendMsg(String msg){
        for (UserMananger mananger : GameManager.getInstance().getUserlist()) {
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendMessage(msg);
        }
    }

    public void sendKillMsg(Player killer, Player victim, String msg){
        UserMananger killManager = null, victimManager = null;
        for (UserMananger mananger : GameManager.getInstance().getUserlist()) {
            if (mananger.getUUID().equals(killer.getUniqueId())){
                killManager = mananger;
            }
            else if (mananger.getUUID().equals(victim.getUniqueId())){
                victimManager = mananger;
            }
        }
        if (killManager != null && victimManager != null){
            //죽은사람이 연속킬중일 경우
            killManager.setKillMaintain(killManager.getKillMaintain() + 1);
            if (victimManager.getKillMaintain() >= 2){
                sendMsg("§4§oShutDown! " + msg);
            }
            else{
                //아닌경우 그냥 출력
                if (killManager.calcKillStay()) {
                    //이어갈 수 있는경우
                    if (killManager.getKillMaintain() != 1) {
                        if (killManager.getKillMaintain() == 2)
                            sendMsg("§e§oDouble Kill! " + msg);
                        else if (killManager.getKillMaintain() == 3)
                            sendMsg("§b§oTriple Kill! " + msg);
                        else if (killManager.getKillMaintain() == 4)
                            sendMsg("§a§oQuadra Kill! " + msg);
                        else if (killManager.getKillMaintain() == 5)
                            sendMsg("§d§oPenta Kill! " + msg);
                        else if (killManager.getKillMaintain() == 6)
                            sendMsg("§4§oHexa Kill! " + msg);
                        else
                            sendMsg("§6§oLegendary! " + msg);
                    }
                    else
                        sendMsg(msg);
                }
                else
                    sendMsg(msg);
                }
            victimManager.setKillMaintain(0);
            killManager.setLastKillTime(System.currentTimeMillis());
            }

    }

    public void sendRespawn(Player victim, String killerName, String WeaponName, boolean melee){
        //게임 끝날때 사망 리스폰 처리
        victim.setGameMode(GameMode.SPECTATOR);
        victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f);
        if (melee)
            victim.sendTitle("§c§oRespawn..", "§c" + killerName + " §7メ §6" + victim.getName(), 0,40,0);
        else{
            victim.sendTitle("§c§oRespawn..", "§c" + killerName + " §7➾ §6" + victim.getName(), 0,40,0);
            victim.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c§oUsing §8: " + WeaponName));
        }
        Bukkit.getScheduler().runTaskLater(Plugin, ()->{
            //2초후 리스폰
            if (GameManager.getInstance().isgaming()){
                if (GameManager.getInstance().contains(victim.getUniqueId())){
                    for (UserMananger victimgr : GameManager.getInstance().getUserlist()) {
                        if (victimgr.getUUID().equals(victim.getUniqueId())) {
                            victim.setHealth(20.0);
                            victim.teleport(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[GameManager.getInstance().getRandomNumber()], DataManager.getInstance().getLocations()[GameManager.getInstance().getRandomNumber() + 1]));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 5, true, false));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 100, true, false));
                            victim.getInventory().setItem(0, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(victimgr.getKills() / DataManager.getInstance().getKilltolevel())));
                            victim.getInventory().setItem(1, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(-1)));
                        }
                    }
                    victim.setGameMode(GameMode.ADVENTURE);
                }
                else{
                    victim.teleport(DataManager.getInstance().getLocations()[0]);
                    victim.setGameMode(GameMode.ADVENTURE);
                }
            }
            else{
                victim.teleport(DataManager.getInstance().getLocations()[0]);
                victim.setGameMode(GameMode.ADVENTURE);
            }
        }, 40L);
    }
}
