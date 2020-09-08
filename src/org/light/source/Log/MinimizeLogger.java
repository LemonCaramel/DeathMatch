package org.light.source.Log;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.DeathMatch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MinimizeLogger {

    private DeathMatch Plugin;
    private static MinimizeLogger instance;
    private ArrayList<String> logs;
    private File folder, logFile;
    private SimpleDateFormat fileFormat, logFormat;
    private int taskid;
    private YamlConfiguration configFormat;

    static
    {
        instance = new MinimizeLogger();
    }

    private MinimizeLogger(){
        Plugin = JavaPlugin.getPlugin(DeathMatch.class);
        logs = new ArrayList<>();
        folder = new File("plugins/" + Plugin.getDescription().getName() + "/log");
        fileFormat = new SimpleDateFormat("MM.dd-HH.mm.ss");
        logFormat = new SimpleDateFormat("[ MM-dd / HH:mm:ss ]");
        taskid = 0;
        if (!folder.exists())
            folder.mkdir();
    }

    public static MinimizeLogger getInstance(){
        return instance;
    }

    public int getTaskid(){
        return taskid;
    }

    public void appendLog(String log){
        String time = logFormat.format(Calendar.getInstance().getTime());
        logs.add(time + " " + log);
    }

    public void logStart(){
        taskid = Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin, this::saveLog,0L, 1200L);
    }

    public void saveLog(){
        if (logs.size() >= 20){
            logFile = new File("plugins/" + Plugin.getDescription().getName() + "/log/" + fileFormat.format(Calendar.getInstance().getTime()) + ".log");
            configFormat = YamlConfiguration.loadConfiguration(logFile);
            configFormat.set("로그", logs);
            try {
                configFormat.save(logFile);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            logs.clear();
        }
    }

    public void forceSaveLog(){
        if (!logs.isEmpty()){
            logFile = new File("plugins/" + Plugin.getDescription().getName() + "/log/" + fileFormat.format(Calendar.getInstance().getTime()) + ".log");
            configFormat = YamlConfiguration.loadConfiguration(logFile);
            configFormat.set("로그", logs);
            try {
                configFormat.save(logFile);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            logs.clear();
            Bukkit.getScheduler().cancelTask(taskid);
        }
    }

}