package org.light.source.Game;

import org.light.source.Singleton.DataManager;

import java.util.UUID;

public class UserMananger {

    private UUID uuid;
    private int kills;
    private long lastKillTime;
    private int killMaintain;

    public UserMananger(UUID uuid){
        this.uuid = uuid;
        kills = 0;
        killMaintain = 0;
        lastKillTime = 0;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getKillMaintain() {
        return killMaintain;
    }

    public void setKillMaintain(int killMaintain) {
        this.killMaintain = killMaintain;
    }

    public long getLastKillTime() {
        return lastKillTime;
    }

    public void setLastKillTime(long lastKillTime) {
        this.lastKillTime = lastKillTime;
    }

    public boolean calcKillStay(){
        return System.currentTimeMillis() - getLastKillTime() <= DataManager.getInstance().getKillMaintain()*1000;
    }

}
