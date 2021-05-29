package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.light.source.DeathMatch;

public class ScoreboardObject {

    private static final ScoreboardObject instance;
    private final Scoreboard wait;
    private final Objective readyObject;
    private DeathMatch plugin;

    static {
        instance = new ScoreboardObject(DeathMatch.instance);
    }

    private ScoreboardObject(DeathMatch plugin) {
        this.plugin = plugin;
        wait = Bukkit.getScoreboardManager().getNewScoreboard();
        readyObject = wait.registerNewObjective("score", "dummy");
        readyObject.setDisplaySlot(DisplaySlot.SIDEBAR);
        readyObject.getScore("Intializing...").setScore(-1024);
        readyObject.setDisplayName("caramel.moe");
    }

    public static ScoreboardObject getInstance() {
        return instance;
    }

    public Objective getReadyObject() {
        return readyObject;
    }

    public Scoreboard getObject() {
        return wait;
    }

    public void setScoreboard(Player p) {
        p.setScoreboard(wait);
        new moe.caramel.caramellibrarylegacy.api.score.Scoreboard(ScoreboardObject.getInstance().getReadyObject(), p)
                .setHandler(this.plugin.getScoreBoardManager())
                .setUpdateInterval(20L)
                .activate(this.plugin);
    }

}
