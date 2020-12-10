package org.light.source.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Singleton.DataManager;

import javax.xml.crypto.Data;


public class WaitTimer extends BukkitRunnable {

    private int countValue;
    private boolean isRunning;
    private DeathMatch Plugin;
    private BossBar bossBar;
    private int taskID;

    public WaitTimer(DeathMatch Plugin) {
        countValue = DataManager.getInstance().getWaitTime();
        this.Plugin = Plugin;
        isRunning = false;
        bossBar = Bukkit.createBossBar("§cRemain : ", BarColor.RED, BarStyle.SOLID);
        setBossBar();
        start();

    }

    @Override
    public void run() {
        //1s주기 타이머
        if (!GameManager.getInstance().canStart() || GameManager.getInstance().getUsers().size() < DataManager.getInstance().getMinimumUser()) {
            if (countValue <= 5) {
                countValue = DataManager.getInstance().getWaitTime();
                GameManager.getInstance().getUsers().forEach(data -> {
                    Player target = Bukkit.getPlayer(data.getUUID());
                    target.teleport(DataManager.getInstance().getLocations()[0]);
                });
                countValue = DataManager.getInstance().getWaitTime();
            }
        }
        else if (countValue <= 0) {
            //start
            GameManager.getInstance().start();
            bossBar.removeAll();
            stop();
        }
        else if (countValue <= 5) {
            GameManager.getInstance().getUsers().forEach(data -> {
                Player target = Bukkit.getPlayer(data.getUUID());
                if (countValue == 5) {
                    int randomMap;
                    randomMap = GameManager.getInstance().getRandomNumber();
                    target.teleport(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[randomMap], DataManager.getInstance().getLocations()[randomMap + 1]));
                }
                target.sendTitle("§b준비!", "§6" + countValue + "§f초 후 게임이 시작됩니다.", 0, 24, 0);
            });
        }
        bossBar.setTitle("§cRemain §7: §6" + countValue + "§f초");
        bossBar.setProgress(calcProgress(DataManager.getInstance().getWaitTime() - countValue, DataManager.getInstance().getWaitTime()));
        countValue--;
    }

    public boolean isRunning() {
        return isRunning;
    }

    //성공시 true, 실패시 false
    public boolean start() {
        if (!isRunning) {
            countValue = DataManager.getInstance().getWaitTime();
            taskID = runTaskTimer(Plugin, 0L, 20L).getTaskId();
            isRunning = true;
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (isRunning) {
            Bukkit.getScheduler().cancelTask(taskID);
            isRunning = false;
            countValue = DataManager.getInstance().getWaitTime();
            return true;
        }
        return false;
    }

    public int returnRemainTime() {
        return countValue;
    }

    private float calcProgress(int now, int remain){
        if ((1.0 - (float)now / remain) < 0)
            return 0.0f;
        else
            return (float) (1.0 - (float)now / remain);
    }

    public BossBar returnBossbarInstance(){
        return bossBar;
    }

    public void setBossBar(){
        GameManager.getInstance().getUsers().forEach(data -> {
            Player target = Bukkit.getPlayer(data.getUUID());
            if (!bossBar.getPlayers().contains(target))
                bossBar.addPlayer(target);
        });
    }
}
