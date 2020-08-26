package org.light.source.Singleton;

import org.bukkit.Location;

public class DataManager {

    private int rounds;
    private int kills;
    private int time; //초단위
    private Location[] locations; //2개
    private static DataManager manager;


    static {
        manager = new DataManager();
    }

    private DataManager(){
        super();
        kills = 0;
        rounds = 0;
        time = 0;
    }

    public static DataManager getInstance(){
        return manager;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getRounds() {
        return rounds;
    }

    public int getKills() {
        return kills;
    }

    public int getTime() {
        return time;
    }

    public Location[] getLocations() {
        if (locations == null || locations.length != 2)
            return null;
        return locations;
    }

    public void setLocations(Location[] locations) {
        this.locations = locations;
    }


}
