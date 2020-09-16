package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class DataManager {

    private int rounds;
    private int killtolevel;
    private int time; //초단위
    private int minimum;
    private Location[] locations; //2개
    private static DataManager manager;
    private int joinMoney;
    private int firstReward;
    private int secondReward;
    private int thirdReward;
    private int killMaintain;

    static {
        manager = new DataManager();
    }

    private DataManager(){
        super();
        killtolevel = 0;
        rounds = 0;
        time = 0;
        minimum = 0;
        joinMoney = 0;
        firstReward = 0;
        secondReward = 0;
        thirdReward = 0;
        killMaintain = 1;
        locations = new Location[21];
    }

    public static DataManager getInstance(){
        return manager;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setKilltolevel(int killtolevel) {
        this.killtolevel = killtolevel;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getRounds() {
        return rounds;
    }

    public int getKilltolevel() {
        return killtolevel;
    }

    public int getTime() {
        return time;
    }

    public Location[] getLocations() {
        if (locations == null)
            return null;

        if (getLocationAmount() % 2 != 1 || getLocationAmount() == 1)
            return null;
        return locations;
    }

    public void setLocations(Location location, int index) {
        this.locations[index-1] = location;
    }

    public int getMinimumUser() {
        return minimum;
    }

    public void setMinimumUser(int minimum) {
        this.minimum = minimum;
    }

    public int getLocationAmount(){
        int i = 0, count = 0;
        while (locations[i] != null){
            count++;
            i++;
        }
        return count;
    }

    public void flushLocation(){
        locations = new Location[locations.length];
    }


    public int getJoinMoney(){
        return joinMoney;
    }

    public void setJoinMoney(int reward){
        joinMoney = reward;
    }

    public int getFirstReward(){
        return firstReward;
    }

    public void setFirstReward(int reward){
        firstReward = reward;
    }

    public int getSecondReward(){
        return secondReward;
    }

    public void setSecondReward(int reward){
        secondReward = reward;
    }

    public int getThirdReward(){
        return thirdReward;
    }

    public void setThirdReward(int reward){
        thirdReward = reward;
    }

    public int getKillMaintain() {
        return killMaintain;
    }

    public void setKillMaintain(int killMaintain) {
        this.killMaintain = killMaintain;
    }
}
