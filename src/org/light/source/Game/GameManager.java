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
import org.light.source.Log.MinimizeLogger;
import org.light.source.Runnable.Countdown;
import org.light.source.Runnable.MainTimer;
import org.light.source.Singleton.*;

import java.util.ArrayList;
import java.util.UUID;

public class GameManager {

    private static GameManager manager;
    private boolean isgaming;
    private ArrayList<UserMananger> userlist;
    private DeathMatch Plugin;
    private int taskid;
    private int randomMap;
    private Countdown countRunnable;
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
        randomMap = 1;
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
        if (!canstart()){
            p.sendMessage("§c[ §fDeathMatch §6] §c데스매치 기초설정이 끝나지 않아 참여하실 수 없습니다.");
        }
        else {
            for (UserMananger mananger : userlist) {
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                target.sendMessage("§c[ §fDeathMatch §6] §b" + p.getName() + "§f님이 §c데스매치§f에 참여하셨습니다.");
            }
            userlist.add(new UserMananger(p.getUniqueId()));
            if (isgaming) {
                setPlayer(p);
                RatingManager.getInstance().updateRank();
                gameRunnable.getbossbarInstance().addPlayer(p);
            }
            else if (getusercount() >= DataManager.getInstance().getMinimumUser()) {
                if (countRunnable == null) {
                    randomMap = ((int)(Math.random()*(DataManager.getInstance().getLocationAmount()-1)) + 1);
                    while (randomMap % 2 != 1)
                        randomMap = ((int)(Math.random()*(DataManager.getInstance().getLocationAmount()-1)) + 1);
                    for (UserMananger mananger : userlist) {
                        Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                        target.sendTitle("§c[ §fDeathMatch §6] §b시작 준비!", "§c최소 인원이 충족되어 곧 게임이 시작됩니다.", 5, 20, 5);
                        target.teleport(getTeleportLocation(DataManager.getInstance().getLocations()[randomMap], DataManager.getInstance().getLocations()[randomMap+1]));
                    }
                    countRunnable = new Countdown(userlist);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
                        if (countRunnable != null) {
                            countRunnable.runTaskTimer(Plugin, 0L, 2L).getTaskId();
                        }}, 20L);
                }
            }
        }
    }

    public void removePlayer(Player p){
        userlist.removeIf(userMananger -> userMananger.getUUID().equals(p.getUniqueId()));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c[ §fDeathMatch §6] §f데스매치에서 퇴장하셨습니다."));
        for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendMessage("§c[ §fDeathMatch §6] §b" + p.getName() + "§f님이 §c데스매치§f에서 퇴장하셨습니다.");
        }
        if (isgaming) {
            RatingManager.getInstance().updateRank();
            p.teleport(DataManager.getInstance().getLocations()[0]);
            p.getInventory().clear();
            p.setHealth(20.0);
            gameRunnable.getbossbarInstance().removePlayer(p);
            p.removePotionEffect(PotionEffectType.WEAKNESS);
        }
        if (canstart() && getusercount() + 1 == DataManager.getInstance().getMinimumUser()){
            if (!isgaming) {
                if (countRunnable != null && countRunnable.countnum != 102)
                    countRunnable.cancel();
                countRunnable = null;
                for (UserMananger mananger : userlist) {
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    target.sendMessage("§c[ §fDeathMatch §6] §f최소인원을 만족하지 못해 게임 준비가 취소되었습니다.");
                    target.teleport(DataManager.getInstance().getLocations()[0]);
                }
                p.teleport(DataManager.getInstance().getLocations()[0]);
            }
            else {
                for (UserMananger mananger : userlist) {
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    target.sendMessage("§c[ §fDeathMatch §6] §c데스매치 최소인원을 만족하지 못해 게임이 중단되었습니다.");
                }
                p.teleport(DataManager.getInstance().getLocations()[0]);
                p.getInventory().clear();
                p.setHealth(20.0);
                p.removePotionEffect(PotionEffectType.WEAKNESS);
                gameRunnable.cancel();
                stop();
            }
        }
        //게임 시작전인지 게임 중인지
    }

    public void sendActionBar(){
        Bukkit.getScheduler().runTask(Plugin, ()-> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (!isgaming && (getusercount() < DataManager.getInstance().getMinimumUser() || DataManager.getInstance().getMinimumUser() == 0))
                    ScoreboardObject.getInstance().sendScoreboard(1, player);
                else if (!isgaming && getusercount() >= DataManager.getInstance().getMinimumUser())
                    ScoreboardObject.getInstance().sendScoreboard(2, player);
                else
                    ScoreboardObject.getInstance().sendScoreboard(3, player);
                }
            }
        );

    }

    public int getusercount(){
        return userlist.size();
    }

    public ArrayList<UserMananger> getUserlist(){
        return userlist;
    }
    public boolean contains(UUID uuid){
        for (UserMananger mananger : userlist){
            if (mananger.getUUID().equals(uuid))
                return true;
        }
        return false;
    }
    public int getRandomNumber(){
        return randomMap;
    }
    public void start(){
        if (!isgaming) {
            setGameState(true);
            Bukkit.broadcastMessage(randomMap + "값");
            gameRunnable = new MainTimer(DataManager.getInstance().getTime(), userlist);
            gameRunnable.runTaskTimerAsynchronously(Plugin, 0L, 20L);
            Bukkit.getServer().getScheduler().runTask(Plugin, () -> {
                for (UserMananger mananger : userlist) {
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    setPlayer(target);
                }
            });
            RatingManager.getInstance().updateRank();
        }
    }

    public void stop() {
        if (isgaming) {
            setGameState(false);
            for (UserMananger mananger : userlist) {
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                Bukkit.getServer().getScheduler().runTask(Plugin, () -> target.teleport(DataManager.getInstance().getLocations()[0]));
                target.getInventory().clear();
                target.setHealth(20.0);
                target.removePotionEffect(PotionEffectType.WEAKNESS);
                gameRunnable.getbossbarInstance().removeAll();
                if (DataManager.getInstance().getJoinMoney() != 0 && getusercount() >= DataManager.getInstance().getMinimumUser()) {
                    target.sendMessage("§c[ §fDeathMatch §6] §f참여 보상 §6" + DataManager.getInstance().getJoinMoney() + "§f원을 흭득하셨습니다!");
                    EconomyApi.getInstance().giveMoney(target, DataManager.getInstance().getJoinMoney());
                    MinimizeLogger.getInstance().appendLog(target.getName() + "님이 데스매치에 참여해 " + DataManager.getInstance().getJoinMoney() + "원을 흭득함");
                }
            }
            giveRatingReward();
            gameRunnable.cancel();
            gameRunnable = null;
            countRunnable = null;
            userlist.clear();
        }
    }

    public boolean canstart(){
        DataManager manager = DataManager.getInstance();
        if (manager.getMinimumUser() > 1 && manager.getTime() >= 10 && manager.getKilltolevel() >= 2 && manager.getLocations() != null && manager.getRounds() >= 1 && manager.getListSize() >= manager.getRounds())
            return true;
        return false;
    }

    public void setPlayer(Player p){
        p.setHealth(20.0);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 5, true, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 100,true,false));
        p.teleport(getTeleportLocation(DataManager.getInstance().getLocations()[randomMap], DataManager.getInstance().getLocations()[randomMap+1]));
        p.getInventory().setItem(0, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(0)));
        if (DataManager.getInstance().getWeaponName(-1) != null)
            p.getInventory().setItem(1, CrackShotApi.getCSWeapon(DataManager.getInstance().getWeaponName(-1)));
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
        if (min.getBlock().getType() != Material.AIR)
            min = getTeleportLocation(first, second);
        return min;
    }

    public void giveRatingReward(){
        if (userlist.size() >= 5){
            if (RatingManager.getInstance().getFirst() != null && DataManager.getInstance().getFirstReward() != 0){
                Player first = Bukkit.getPlayer(RatingManager.getInstance().getFirst());
                EconomyApi.getInstance().giveMoney(first, DataManager.getInstance().getFirstReward());
                first.sendMessage("§c[ §fDeathMatch §6] §b1위를 하셔서 추가 보상 §6" + DataManager.getInstance().getFirstReward() + "§f원을 흭득하셨습니다!");
                MinimizeLogger.getInstance().appendLog(first.getName() + "님이 데스매치에서 1등을 하여 " + DataManager.getInstance().getFirstReward() + "원을 흭득함");
            }
            if (RatingManager.getInstance().getSecond() != null && DataManager.getInstance().getSecondReward() != 0){
                Player second = Bukkit.getPlayer(RatingManager.getInstance().getSecond());
                EconomyApi.getInstance().giveMoney(second, DataManager.getInstance().getSecondReward());
                second.sendMessage("§c[ §fDeathMatch §6] §a2위를 하셔서 추가 보상 §6" + DataManager.getInstance().getSecondReward() + "§f원을 흭득하셨습니다!");
                MinimizeLogger.getInstance().appendLog(second.getName() + "님이 데스매치에서 2등을 하여 " + DataManager.getInstance().getSecondReward() + "원을 흭득함");
            }
            if (RatingManager.getInstance().getThird() != null && DataManager.getInstance().getThirdReward() != 0){
                Player third = Bukkit.getPlayer(RatingManager.getInstance().getThird());
                EconomyApi.getInstance().giveMoney(third, DataManager.getInstance().getThirdReward());
                third.sendMessage("§c[ §fDeathMatch §6] §c3위를 하셔서 추가 보상 §6" + DataManager.getInstance().getThirdReward() + "§f원을 흭득하셨습니다!");
                MinimizeLogger.getInstance().appendLog(third.getName() + "님이 데스매치에서 3등을 하여 " + DataManager.getInstance().getThirdReward() + "원을 흭득함");
            }
        }
    }
}
