package org.light.source.Game;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.DeathMatch;
import org.light.source.Runnable.Countdown;
import org.light.source.Runnable.MainTimer;
import org.light.source.Singleton.DataManager;

import java.util.ArrayList;
import java.util.UUID;

public class GameManager {

    private static GameManager manager;
    private boolean isgaming;
    private ArrayList<UserMananger> userlist;
    private DeathMatch Plugin;
    private int taskid;
    private BukkitRunnable countRunnable;
    private MainTimer gameRunnable;

    static {
        manager = new GameManager();
    }
    private GameManager(){
        userlist = new ArrayList<>();
        isgaming = false;
        Plugin = JavaPlugin.getPlugin(DeathMatch.class);
        taskid = Bukkit.getScheduler().runTaskTimerAsynchronously(Plugin, this::sendActionBar, 0L, 20L).getTaskId();
        countRunnable = null;
        gameRunnable = null;
    }

    public static GameManager getInstance(){
        return manager;
    }

   public boolean isgaming(){
        return isgaming;
   }

   public void setGameState(boolean GameState){
        isgaming = GameState;
   }

    public void addPlayer(Player p) {
        if (!p.getWorld().getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN) || p.getWorld().getDifficulty() != Difficulty.PEACEFUL){
            p.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            p.getWorld().setDifficulty(Difficulty.PEACEFUL);
        }
        for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendMessage("§c[ §fDeathMatch §6] §b" + p.getName() + "§f님이 §c데스매치§f에 참여하셨습니다.");
        }
        userlist.add(new UserMananger(p.getUniqueId()));
        if (isgaming){
            setPlayer(p);
        }
        else if (canstart() && getusercount() >= DataManager.getInstance().getMinimumUser()){
            for (UserMananger mananger : userlist) {
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                target.sendTitle("§c[ §fDeathMatch §6] §b시작 준비!", "§c최소 인원이 충족되어 곧 게임이 시작됩니다.", 5,20,5);
            }
            countRunnable = new Countdown(userlist);
            Bukkit.getScheduler().runTaskLaterAsynchronously(Plugin, ()->countRunnable.runTaskTimer(Plugin, 0L, 2L), 20L);
        }
    }

    public void removePlayer(Player p){
        userlist.removeIf(userMananger -> userMananger.getUUID().equals(p.getUniqueId()));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c[ §fDeathMatch §6] §f데스매치에서 퇴장하셨습니다."));
        for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendMessage("§c[ §fDeathMatch §6] §b" + p.getName() + "§f님이 §c데스매치§f에서 퇴장하셨습니다.");
        }
        if (canstart() && getusercount() + 1 == DataManager.getInstance().getMinimumUser()){
            if (!isgaming) {
                if (countRunnable != null)
                    countRunnable.cancel();
                countRunnable = null;
                for (UserMananger mananger : userlist) {
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    target.sendMessage("§c[ §fDeathMatch §6] §f최소인원을 만족하지 못해 게임 준비가 취소되었습니다.");
                }
            }
            else {
                for (UserMananger mananger : userlist) {
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    target.sendMessage("§c[ §fDeathMatch §6] §c데스매치 최소인원을 만족하지 못해 게임이 중단되었습니다.");
                }
                p.teleport(p.getWorld().getSpawnLocation());
                p.getInventory().clear();
                p.setHealth(20.0);
                gameRunnable.cancel();
                stop();
            }
        }
        //게임 시작전인지 게임 중인지
    }

    public void sendActionBar(){
        for (UserMananger mananger : userlist) {
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            String value;
            if (!isgaming && (getusercount() < DataManager.getInstance().getMinimumUser() || DataManager.getInstance().getMinimumUser() == 0))
                value = "§c[ §fDeathMatch §6] §b플레이어의 참여를 기다리는 중입니다... §f" + userlist.size() + " §7/ §6" + DataManager.getInstance().getMinimumUser();
            else if (!isgaming && getusercount() >= DataManager.getInstance().getMinimumUser())
                value = "§c[ §fDeathMatch §6] §a게임 시작 준비중입니다.. §f" + userlist.size() + " §7/ §6" + DataManager.getInstance().getMinimumUser();
            else
                value = "§c[ §fDeathMatch §6] §c현재 게임이 진행중입니다.. §f" + userlist.size() + " §7/ §6" + DataManager.getInstance().getMinimumUser();
            //To-Do
            target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(value));

        }
    }

    public int getusercount(){
        return userlist.size();
    }

    public ArrayList<UserMananger> getUserlist(){
        return userlist;
    }
    public boolean contains(UUID uuid){
        for (UserMananger mananger : userlist){
            if (mananger.getUUID() == uuid)
                return true;
        }
        return false;
    }
    public void start(){
        setGameState(true);

        gameRunnable = new MainTimer(DataManager.getInstance().getTime(), userlist);
        gameRunnable.runTaskTimerAsynchronously(Plugin, 0L, 20L);
        Bukkit.getServer().getScheduler().runTask(Plugin, ()->{for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            setPlayer(target);
        }});
    }

    public void stop(){
        setGameState(false);
        for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            Bukkit.getServer().getScheduler().runTask(Plugin, ()->target.teleport(target.getWorld().getSpawnLocation()));
            target.getInventory().clear();
            target.setHealth(20.0);
            gameRunnable.getbossbarInstance().removeAll();
        }
        gameRunnable.cancel();
        gameRunnable = null;
        userlist.clear();
    }

    public boolean canstart(){
        DataManager manager = DataManager.getInstance();
        if (manager.getTime() >= 10 && manager.getKilltolevel() >= 1 && manager.getLocations() != null && manager.getRounds() >= 1)
            return true;
        return false;
    }

    public void setPlayer(Player p){
        p.setHealth(20.0);
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().clear();
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5, true, false));
        p.teleport(getTeleportLocation(DataManager.getInstance().getLocations()[0], DataManager.getInstance().getLocations()[1]));
    }

    public Location getTeleportLocation(Location first, Location second){
        double x,xx,y,yy,z,zz;
        int gapx,gapy,gapz;
        if (first.getX() < second.getY()){
            x = first.getX();
            xx = second.getX();
        }
        else{
            x = second.getX();
            xx = first.getX();
        }
        if (first.getY() < second.getY()){
            y = first.getY();
            yy = second.getY();
        }
        else{
            y = second.getY();
            yy = first.getY();
        }
        if (first.getZ() < second.getZ()){
            z = first.getZ();
            zz = second.getZ();
        }
        else{
            z = second.getZ();
            zz = first.getZ();
        }
        gapx = (int) ((int)xx-x);
        gapy = (int) ((int)yy-y);
        gapz = (int) ((int)zz-z);

        if (gapx == 0 && gapy == 0 && gapz == 0)
            return first;
        Location min = new Location(first.getWorld(),x,y,z);
        if (gapx != 0){
            double add = Math.random()*(gapx-1) + 1;
            min.setX(min.getX()+add);
        }
        if (gapy != 0){
            double add = Math.random()*(gapy-1) + 1;
            min.setY(min.getY()+add);
        }
        if (gapz != 0){
            double add = Math.random()*(gapz-1) + 1;
            min.setZ(min.getZ()+add);
        }
        return min;
    }
}
