package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TeamManager {

    private static TeamManager instance;
    private Team joinTeam;

    static {
        instance = new TeamManager();
    }

    private TeamManager(){
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            if (team.getName().contains("join"))
                joinTeam = team;
        }
        if (joinTeam == null)
            joinTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("join");
        setTeam(joinTeam);

    }
    public static TeamManager getInstance(){
        return instance;
    }

    private void setTeam(Team team){
        team.setPrefix("§b");
        team.setAllowFriendlyFire(true);
    }

    public void removePlayer(Player p){
        joinTeam.removeEntry(p.getName());
    }

    public void addPlayer(Player p){
        joinTeam.addEntry(p.getName());
    }
}
