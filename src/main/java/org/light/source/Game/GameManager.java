package org.light.source.Game;

import moe.caramel.caramellibrarylegacy.api.API;
import moe.caramel.caramellibrarylegacy.user.CaramelUserData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.light.source.DeathMatch;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Runnable.WaitTimer;
import org.light.source.Runnable.MainTimer;
import org.light.source.Singleton.*;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GameManager {

    private static GameManager manager;
    private boolean isGaming;
    private ArrayList<UserMananger> users;
    private DeathMatch Plugin;
    private int randomMap;
    private WaitTimer timer;
    private MainTimer gameTimer;
    private API api;

    static {
        manager = new GameManager();
    }

    private GameManager() {
        users = new ArrayList<>();
        isGaming = false;
        Plugin = JavaPlugin.getPlugin(DeathMatch.class);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Plugin, this::sendScore, 0L, 20L);
        timer = new WaitTimer(Plugin);
        gameTimer = null;
        api = new API();
        selectRandomMap();
    }

    public static GameManager getInstance() {
        return manager;
    }

    public boolean isGaming() {
        return isGaming;
    }

    public void setGameState(boolean GameState) {
        isGaming = GameState;
    }

    public void addPlayer(Player p) {
        if (!canStart()) {
            p.sendMessage("§c데스매치 기초설정이 끝나지 않아 참여하실 수 없습니다.");
        }
        else {
            users.add(new UserMananger(p.getUniqueId()));
            sendMessage(" §6§l참여 §7§l》" + "§b" + p.getName());
            if (timer.isRunning())
                timer.returnBossbarInstance().addPlayer(p);
            if (isGaming()) {
                setPlayer(p);
                RatingManager.getInstance().updateRank();
                gameTimer.getbossbarInstance().addPlayer(p);
            }
            else if (timer.returnRemainTime() <= 5 && timer.isRunning())
                p.teleport(getTeleportLocation(DataManager.getInstance().getLocations()[randomMap], DataManager.getInstance().getLocations()[randomMap + 1]));
        }
    }

    public void removePlayer(Player p) {
        sendMessage(" §c§l퇴장 §7§l《 " + "§b" + p.getName());
        users.removeIf(data -> data.getUUID().equals(p.getUniqueId()));
        TeamManager.getInstance().removePlayer(p);
        if (isGaming()) {
            RatingManager.getInstance().updateRank();
            setNormalPlayer(p);
        }
        p.teleport(DataManager.getInstance().getLocations()[0]);
        if (isGaming() && getUserCount() < DataManager.getInstance().getMinimumUser()) {
            sendMessage("§c데스매치 최소인원을 만족하지 못해 게임이 중단되었습니다.");
            gameTimer.cancel();
            stop();
        }
        //게임 시작전인지 게임 중인지
    }

    public void sendScore() {
        Bukkit.getScheduler().runTask(Plugin, () -> {
                    if (!isGaming && (getUserCount() < DataManager.getInstance().getMinimumUser() || DataManager.getInstance().getMinimumUser() == 0))
                        ScoreboardObject.getInstance().sendScoreboard(1);
                    else if (!isGaming && getUserCount() >= DataManager.getInstance().getMinimumUser())
                        ScoreboardObject.getInstance().sendScoreboard(2);
                    else
                        ScoreboardObject.getInstance().sendScoreboard(3);
                }
        );

    }

    public int getUserCount() {
        return users.size();
    }

    public ArrayList<UserMananger> getUsers() {
        return users;
    }

    public boolean contains(UUID uuid) {
        for (UserMananger data : users) {
            if (data.getUUID().equals(uuid))
                return true;
        }
        return false;
    }

    public int getRandomNumber() {
        return randomMap;
    }

    public void start() {
        if (!isGaming) {
            setGameState(true);
            gameTimer = new MainTimer(DataManager.getInstance().getTime(), users);
            gameTimer.runTaskTimerAsynchronously(Plugin, 0L, 20L);
            Bukkit.getServer().getScheduler().runTask(Plugin, () -> users.forEach(data -> setPlayer(Bukkit.getPlayer(data.getUUID()))));
            RatingManager.getInstance().updateRank();
        }
    }

    public void stop() {
        if (isGaming()) {
            Bukkit.getScheduler().runTask(Plugin, () -> {
                setGameState(false);
                for (UserMananger data : users) {
                    Player target = Bukkit.getPlayer(data.getUUID());
                    setNormalPlayer(target);
                    gameTimer.getbossbarInstance().removeAll();
                    if (DataManager.getInstance().getJoinMoney() != 0 && getUserCount() >= DataManager.getInstance().getMinimumUser()) {
                        target.sendMessage("§f총 보상 §6" + (DataManager.getInstance().getJoinMoney() + data.getCalcResultMoney()) + "§f원을 흭득하셨습니다!");
                        EconomyApi.getInstance().giveMoney(target, DataManager.getInstance().getJoinMoney() + data.getCalcResultMoney());
                        MinimizeLogger.getInstance().appendLog(target.getName() + "님이 데스매치에 참여해 " + DataManager.getInstance().getJoinMoney() + "원을 흭득함");
                    }
                    if (RatingManager.getInstance().getFirst() != null) {
                        target.sendMessage("§f이번 게임의 §6MVP§f는 §b" + RatingManager.getInstance().getFirst() + "§f님 입니다!");
                    }
                }
                giveRatingReward();
                gameTimer.cancel();
                timer = new WaitTimer(Plugin);
                selectRandomMap();
            });
        }
    }

    public boolean canStart() {
        DataManager manager = DataManager.getInstance();
        return manager.getMinimumUser() > 1 && manager.getTime() >= 10 && manager.getKilltolevel() >= 2 && manager.getLocations() != null && manager.getRounds() >= 1;
    }

    public void setPlayer(Player p) {
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(80.0);
        p.setHealthScaled(true);
        p.setHealth(80.0);
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 5, true, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 100, true, false));
        p.teleport(getTeleportLocation(DataManager.getInstance().getLocations()[randomMap], DataManager.getInstance().getLocations()[randomMap + 1]));
        p.getInventory().setItem(0, CrackShotApi.generateRandomWeapon());
        p.setLevel(0);
        p.setExp(0.0f);
        TeamManager.getInstance().addPlayer(p);
        if (CaramelUserData.getData().getUser(p.getUniqueId()) == null) {
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Plugin, () -> {
                if (CaramelUserData.getData().getUser(p.getUniqueId()) != null)
                    CaramelUserData.getData().getUser(p.getUniqueId()).setInvincibility(false);
            }, 20L);
        }
        else {
            CaramelUserData.getData().getUser(p.getUniqueId()).setInvincibility(false);
        }
    }

    public void setNormalPlayer(Player p) {
        p.teleport(DataManager.getInstance().getLocations()[0]);
        p.getInventory().clear();
        api.giveChannel(p, 8);
        p.setHealth(20.0);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        p.setHealthScaled(true);
        gameTimer.getbossbarInstance().removePlayer(p);
        new ArrayList<>(p.getActivePotionEffects()).forEach(pos -> p.removePotionEffect(pos.getType()));
        p.setLevel(0);
        p.setExp(0.0f);
        if (CaramelUserData.getData().getUser(p.getUniqueId()) != null)
            CaramelUserData.getData().getUser(p.getUniqueId()).setInvincibility(true);
    }

    public Location getTeleportLocation(Location first, Location second) {
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
        if (min.getBlock().getType() != Material.AIR || min.clone().add(new Vector(0, 1, 0)).getBlock().getType() != Material.AIR)
            min = getTeleportLocation(first, second);
        return min;
    }

    public void giveRatingReward() {
        if (users.size() >= 5) {
            if (RatingManager.getInstance().getFirst() != null && DataManager.getInstance().getFirstReward() != 0) {
                Player first = Bukkit.getPlayer(RatingManager.getInstance().getFirst());
                EconomyApi.getInstance().giveMoney(first, DataManager.getInstance().getFirstReward());
                first.sendMessage("§b1위를 하셔서 추가 보상 §6" + DataManager.getInstance().getFirstReward() + "§f원을 흭득하셨습니다!");
                MinimizeLogger.getInstance().appendLog(first.getName() + "님이 데스매치에서 1등을 하여 " + DataManager.getInstance().getFirstReward() + "원을 흭득함");
            }
            if (RatingManager.getInstance().getSecond() != null && DataManager.getInstance().getSecondReward() != 0) {
                Player second = Bukkit.getPlayer(RatingManager.getInstance().getSecond());
                EconomyApi.getInstance().giveMoney(second, DataManager.getInstance().getSecondReward());
                second.sendMessage("§a2위를 하셔서 추가 보상 §6" + DataManager.getInstance().getSecondReward() + "§f원을 흭득하셨습니다!");
                MinimizeLogger.getInstance().appendLog(second.getName() + "님이 데스매치에서 2등을 하여 " + DataManager.getInstance().getSecondReward() + "원을 흭득함");
            }
            if (RatingManager.getInstance().getThird() != null && DataManager.getInstance().getThirdReward() != 0) {
                Player third = Bukkit.getPlayer(RatingManager.getInstance().getThird());
                EconomyApi.getInstance().giveMoney(third, DataManager.getInstance().getThirdReward());
                third.sendMessage("§c3위를 하셔서 추가 보상 §6" + DataManager.getInstance().getThirdReward() + "§f원을 흭득하셨습니다!");
                MinimizeLogger.getInstance().appendLog(third.getName() + "님이 데스매치에서 3등을 하여 " + DataManager.getInstance().getThirdReward() + "원을 흭득함");
            }
        }
    }

    public void sendMessage(String message) {
        users.forEach(data -> {
            Player target = Bukkit.getPlayer(data.getUUID());
            if (target != null)
                target.sendMessage(message);
        });
    }

    public float calcLevelProgress(int level) {
        if (level > DataManager.getInstance().getRounds() || level < 0)
            return 0.0f;
        else
            return (float) level / DataManager.getInstance().getRounds();
    }

    public void selectRandomMap() {
        randomMap = ThreadLocalRandom.current().nextInt(1, DataManager.getInstance().getLocationAmount());
        while (randomMap % 2 != 1)
            randomMap = ThreadLocalRandom.current().nextInt(1, DataManager.getInstance().getLocationAmount());
    }
}
