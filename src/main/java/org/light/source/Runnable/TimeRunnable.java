package org.light.source.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.light.source.DeathMatch;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimeRunnable extends BukkitRunnable {

    private DeathMatch Plugin;
    private SimpleDateFormat format;
    private long[] times;

    public TimeRunnable(DeathMatch Plugin){
        format = new SimpleDateFormat("kk");
        this.Plugin = Plugin;
        //0~24 0틱은 오전 6시부터 시작
        times = new long[]{18000,19000,20000,21000,22000,23000,0,1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,11000,12000,13000,14000,15000,16000,17000};
        start();
    }

    @Override
    public void run() {
        int hour, val;
        val = Integer.parseInt(format.format(Calendar.getInstance().getTime()));
        if (val < 0 || val > times.length)
            hour = 0;
        else
            hour = val;
        Bukkit.getWorlds().forEach(world -> world.setTime(times[hour]));
    }

    public void start(){
        runTaskTimerAsynchronously(Plugin, 0L, 400L);
    }
}
