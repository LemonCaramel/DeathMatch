package org.light.source.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;
import org.light.source.Singleton.DataManager;

import java.util.ArrayList;

public class Countdown extends BukkitRunnable {

    public int countnum;
    private ArrayList<UserMananger> userlist;

    public Countdown(ArrayList<UserMananger> userlist){
        countnum = 102;
        this.userlist = userlist;
    }

    @Override
    public void run() {
        if (countnum == 102)
            countnum = 100;
        if (countnum <= 0) {
            if (GameManager.getInstance().getusercount() < DataManager.getInstance().getMinimumUser()){
                for (UserMananger mananger : userlist){
                    Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                    target.sendTitle("§c오류!", "§c최소인원을 충족하지 못해 게임이 중단되었습니다.", 0,3,0);
                }
            }
            else{
                GameManager.getInstance().start();
            }
            cancel();
        }
        double value = formattedDouble((double)countnum/20);
        for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendTitle("§b카운트 다운!", "§6" + value + "s §f초후 시작합니다.", 0,3,0);
        }
        countnum -= 2;
    }

    public double formattedDouble(double value){
        String format = String.format("%.1f",value);
        return Double.parseDouble(format);
    }
}
