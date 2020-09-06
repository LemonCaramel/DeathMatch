package org.light.source.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;

import java.util.ArrayList;
import java.util.Iterator;

public class MainTimer extends BukkitRunnable {

    private int maxSecond;
    private int now;
    private BossBar bossBar;
    private ArrayList<UserMananger> userlist;

    public MainTimer(int maxSecond, ArrayList<UserMananger> userlist){
        this.maxSecond = maxSecond;
        this.userlist = userlist;
        now = 0;
        bossBar = Bukkit.createBossBar("§c[ §fDeathMatch §6] §5Timer", BarColor.RED, BarStyle.SOLID);
    }

    @Override
    public void run() {
        if (now == 0){
            for (UserMananger mananger : userlist){
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                bossBar.setProgress(1.0f);
                bossBar.addPlayer(target);
                bossBar.setTitle("§c[ §fDeathMatch §6] §5Timer §7: §6" + maxSecond + "§fs");
            }
        }
        else if (now == maxSecond){
            bossBar.removeAll();
            //우승자 가리기
            for (UserMananger mananger : userlist){
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                target.sendMessage("§c[ §fDeathMatch §6] §b시간이 다 되어 게임이 종료되었습니다!");
            }
            GameManager.getInstance().stop();
        }
        else{
            bossBar.setTitle("§c[ §fDeathMatch §6] §5Timer §7: §6" + (maxSecond - now) + "§fs");
            bossBar.setProgress(calcProgress(maxSecond, now));
            if (maxSecond - now <= 5){
                for (UserMananger mananger : userlist){
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
                    target.sendMessage("§c[ §fDeathMatch §6] §f게임 종료까지 §6" + (maxSecond-now) + "§f초 남았습니다!");
                }
            }
        }
        now++;
    }

    public BossBar getbossbarInstance(){
        return bossBar;
    }

    private float calcProgress(int remain, int now){
        return (float) (1.0 - (float)now / remain);
    }
}
