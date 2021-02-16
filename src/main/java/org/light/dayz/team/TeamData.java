package org.light.dayz.team;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.light.source.Singleton.ScoreboardObject;

public class TeamData {

    private Team dayzTeam;

    public TeamData() {
         dayzTeam = ScoreboardObject.getInstance().getObject().registerNewTeam("dayz");
         setTeam(dayzTeam);
    }

    private void setTeam(Team team){
        team.setPrefix("§c");
        team.setAllowFriendlyFire(true);
        team.setCanSeeFriendlyInvisibles(true);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    public void removePlayer(Player p){
        dayzTeam.removeEntry(p.getName());
    }

    public void addPlayer(Player p){
        dayzTeam.addEntry(p.getName());
    }
}
