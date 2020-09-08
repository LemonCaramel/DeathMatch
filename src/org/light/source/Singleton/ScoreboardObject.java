package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.light.source.Game.GameManager;

public class ScoreboardObject {

    private static ScoreboardObject instance;
    private ScoreboardManager boardManager;
    private Scoreboard wait;
    private Objective readyObject;
    static {
        instance = new ScoreboardObject();
    }

    private ScoreboardObject(){
        boardManager = Bukkit.getScoreboardManager();
        wait = boardManager.getNewScoreboard();
    }

    public static ScoreboardObject getInstance(){
        return instance;
    }

    public void sendScoreboard(int value, Player p){
        if (value == 1){
            //시작전
            if (readyObject != null)
                readyObject.unregister();
            readyObject = wait.registerNewObjective("test", "dummy");
            readyObject.setDisplaySlot(DisplaySlot.SIDEBAR);
            readyObject.setDisplayName("§c[ §fDeathMatch §6]");
            Score onlineusers = readyObject.getScore("       §6온라인 §7: §e" + Bukkit.getServer().getOnlinePlayers().size() + "§f명");
            Score blink = readyObject.getScore("                         ");
            Score stat = readyObject.getScore("       §b진행도 §7: §6대기중..");
            Score amount = readyObject.getScore("       §a참여자 §7: §6" + GameManager.getInstance().getusercount() + "§f명");
            Score blink1 = readyObject.getScore("                        ");
            onlineusers.setScore(5);
            blink.setScore(4);
            stat.setScore(3);
            amount.setScore(2);
            blink1.setScore(1);
            p.setScoreboard(wait);
        }
        else if (value == 2){
            //시작 준비
            if (readyObject != null)
                readyObject.unregister();
            readyObject = wait.registerNewObjective("test", "dummy");
            readyObject.setDisplaySlot(DisplaySlot.SIDEBAR);
            readyObject.setDisplayName("§c[ §fDeathMatch §6]");
            Score onlineusers = readyObject.getScore("       §6온라인 §7: §e" + Bukkit.getServer().getOnlinePlayers().size() + "§f명");
            Score blink = readyObject.getScore("                         ");
            Score stat = readyObject.getScore("       §b진행도 §7: §e준비중..");
            Score amount = readyObject.getScore("       §a참여자 §7: §6" + GameManager.getInstance().getusercount() + "§f명");
            Score blink1 = readyObject.getScore("                        ");
            onlineusers.setScore(5);
            blink.setScore(4);
            stat.setScore(3);
            amount.setScore(2);
            blink1.setScore(1);
            p.setScoreboard(wait);
        }
        else if (value == 3){
            //시작
            if (readyObject != null)
                readyObject.unregister();
            readyObject = wait.registerNewObjective("test", "dummy");
            readyObject.setDisplaySlot(DisplaySlot.SIDEBAR);
            readyObject.setDisplayName("§c[ §fDeathMatch §6]");
            Score onlineusers = readyObject.getScore("       §6온라인 §7: §e" + Bukkit.getServer().getOnlinePlayers().size() + "§f명");
            Score blink = readyObject.getScore("                         ");
            Score first = readyObject.getScore("§fLV. §6"  + RatingManager.getLV(RatingManager.getInstance().getFirstKill()) + " §b" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getFirst())));
            Score second = readyObject.getScore("§fLV. §6"  + RatingManager.getLV(RatingManager.getInstance().getSecondKill()) + " §c" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getSecond())));
            Score third = readyObject.getScore("§fLV. §6"  + RatingManager.getLV(RatingManager.getInstance().getThirdKill()) + " §6" + checkLength(RatingManager.changeNick(RatingManager.getInstance().getThird())));
            Score blink2 = readyObject.getScore("                        ");
            onlineusers.setScore(10);
            blink.setScore(9);
            first.setScore(5);
            second.setScore(4);
            third.setScore(3);
            blink2.setScore(2);
            p.setScoreboard(wait);
        }
    }


    public String checkLength(String value){
        if (value.length() >= 40)
            return "§cLOOOONG..";
        return value;
    }
}
