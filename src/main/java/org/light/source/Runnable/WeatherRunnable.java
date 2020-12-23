package org.light.source.Runnable;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.light.source.DeathMatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class WeatherRunnable extends BukkitRunnable {

    private URL link;
    private BufferedReader reader;
    private StringBuilder builder;
    private WeatherState state;
    private DeathMatch Plugin;
    private int taskID;

    public WeatherRunnable(DeathMatch Plugin) {
        this.Plugin = Plugin;
        taskID = 0;
        runTaskTimerAsynchronously(Plugin, 0L, 36000L);
    }

    public enum WeatherState {
        SUNNY,
        RAINY,
        SNOWY
    }

    @Override
    public void run() {
        try {
            String temp;
            link = new URL("http://api.openweathermap.org/data/2.5/weather?q=seoul&appid=933774ae4221314db7a4f320f0db1d05");
            reader = new BufferedReader(new InputStreamReader(link.openStream()));
            builder = new StringBuilder();
            temp = reader.readLine();
            while (temp != null) {
                builder.append(temp);
                temp = reader.readLine();
            }
            temp = builder.toString();
            if (temp.contains("Rain") || temp.contains("rain"))
                state = WeatherState.RAINY;
            else if (temp.contains("Snow") || temp.contains("snow"))
                state = WeatherState.SNOWY;
            else
                state = WeatherState.SUNNY;
            if (taskID != 0)
                Bukkit.getScheduler().cancelTask(taskID);
            setWeather(state);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setWeather(WeatherState state) {
        boolean storm;
        if (state == WeatherState.SUNNY) {
            storm = false;
        }
        else if (state == WeatherState.RAINY) {
            storm = true;
        }
        else {
            storm = false;
            taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(Plugin, () -> {
                for (Player target : Bukkit.getOnlinePlayers())
                    target.spawnParticle(Particle.SPIT, target.getLocation().add(new Vector(0, 1, 0)), 250, 10, 10, 10, 0.05);
            }, 0L, 5L).getTaskId();
        }
        for (World world : Bukkit.getWorlds())
            world.setStorm(storm);

    }
}
