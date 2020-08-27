package org.light.source.Game;

import java.util.UUID;

public class UserMananger {

    private UUID uuid;
    private int kills;

    public UserMananger(UUID uuid){
        this.uuid = uuid;
        kills = 0;
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

}
