package org.light.source.Singleton;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.light.source.DeathMatch;

public class ScoreboardObject {

    private static final ScoreboardObject instance;
    private final Scoreboard wait;
    private final Objective readyObject;
    private final DeathMatch plugin;

    static {
        instance = new ScoreboardObject(DeathMatch.instance);
    }

    private ScoreboardObject(DeathMatch plugin) {
        this.plugin = plugin;
        this.wait = Bukkit.getScoreboardManager().getNewScoreboard();
        this.readyObject = this.wait.registerNewObjective("score", "dummy",
                Component.text("caramel.moe"), RenderType.INTEGER);
        this.readyObject.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.readyObject.getScore("Intializing...").setScore(-1024);
    }

    public static ScoreboardObject getInstance() {
        return instance;
    }

    public Objective getReadyObject() {
        return this.readyObject;
    }

    public Scoreboard getObject() {
        return this.wait;
    }

    public void setScoreboard(Player p) {
        p.setScoreboard(this.wait);
        new moe.caramel.caramellibrarylegacy.api.score.Scoreboard(ScoreboardObject.getInstance().getReadyObject(), p)
                .setHandler(this.plugin.getScoreBoardManager())
                .setUpdateInterval(20L)
                .activate(this.plugin);
    }

}
