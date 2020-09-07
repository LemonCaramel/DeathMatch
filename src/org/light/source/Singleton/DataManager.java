package org.light.source.Singleton;

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
    private HashMap<Integer,String> weapons; //rounds개를 가져야함
    private static DataManager manager;
    private int joinMoney;
    private int firstReward;
    private int secondReward;
    private int thirdReward;

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
        locations = new Location[3];
        weapons = new HashMap<>();
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
        if (locations == null || locations.length != 3)
            return null;
        if (locations[0] == null || locations[1] == null || locations[2] == null)
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

    public String getWeaponName(int index){
        return weapons.get(index);
    }

    public void setWeapon(int index, String weaponName){
        weapons.put(index, weaponName);
    }

    public int getListSize(){
        int i = 0, returnValue = 0;
        while (weapons.get(i) != null){
            returnValue++;
            i++;
        }
        return returnValue;
    }

    public HashMap<Integer,String> getWeapons(){
        return weapons;
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
}
