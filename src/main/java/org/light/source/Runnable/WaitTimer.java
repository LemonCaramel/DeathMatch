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


public class WaitTimer extends BukkitRunnable {

    private int countValue;
    private boolean isRunning;
    private DeathMatch Plugin;
    private BossBar bossBar;

    public WaitTimer(DeathMatch Plugin) {
        countValue = DataManager.getInstance().getWaitTime();
        this.Plugin = Plugin;
        isRunning = false;
        bossBar = Bukkit.createBossBar("§cRemain : ", BarColor.RED, BarStyle.SOLID);
        start();
    }

    @Override
    public void run() {
        //1s주기 타이머
        if (!GameManager.getInstance().canStart() || GameManager.getInstance().getUsers().size() < DataManager.getInstance().getMinimumUser()) {
            if (countValue <= 5)
                countValue = DataManager.getInstance().getWaitTime();
            pause();
        }
        else if (countValue <= 0) {
            //start
            GameManager.getInstance().start();
            GameManager.getInstance().getUsers().forEach(data -> {
                Player target = Bukkit.getPlayer(data.getUUID());
                bossBar.removePlayer(target);
            });
            bossBar.removeAll();
            stop();
        }
        else if (countValue <= 5) {
            GameManager.getInstance().getUsers().forEach(data -> {
                int randomMap = GameManager.getInstance().getRandomNumber();
                Player target = Bukkit.getPlayer(data.getUUID());
                target.sendTitle("§b준비!", "§6" + countValue + "§f초후 게임이 시작됩니다.", 0, 24, 0);
                target.teleport(GameManager.getInstance().getTeleportLocation(DataManager.getInstance().getLocations()[randomMap], DataManager.getInstance().getLocations()[randomMap + 1]));
            });
        }
        bossBar.setTitle("§cRemain §7: §6" + countValue + "§f초");
        bossBar.setProgress(calcProgress(countValue, DataManager.getInstance().getWaitTime()));
        countValue--;
    }

    public boolean isRunning() {
        return isRunning;
    }

    //성공시 true, 실패시 false
    public boolean start() {
        if (!isRunning) {
            countValue = DataManager.getInstance().getWaitTime();
            runTaskTimer(Plugin, 0L, 20L);
            isRunning = true;
            bossBar = Bukkit.createBossBar("§cRemain : ", BarColor.RED, BarStyle.SOLID);
            return true;
        }
        return false;
    }

    public boolean pause() {
        if (isRunning) {
            cancel();
            GameManager.getInstance().getUsers().forEach(data -> {
                Player target = Bukkit.getPlayer(data.getUUID());
                target.teleport(DataManager.getInstance().getLocations()[0]);
            });
            isRunning = false;
            return true;
        }
        return false;
    }

    public boolean resume() {
        if (!isRunning) {
            runTaskTimer(Plugin, 0L, 20L);
            isRunning = true;
            return true;
        }
        return false;
    }

    public boolean stop() {
        if (isRunning) {
            cancel();
            isRunning = false;
            countValue = DataManager.getInstance().getWaitTime();
            return true;
        }
        return false;
    }

    public int returnRemainTime() {
        return countValue;
    }

    private float calcProgress(int remain, int now){
        if ((1.0 - (float)now / remain) < 0)
            return 0.0f;
        else
            return (float) (1.0 - (float)now / remain);
    }

    public BossBar returnBossbarInstance(){
        return bossBar;
    }
}
