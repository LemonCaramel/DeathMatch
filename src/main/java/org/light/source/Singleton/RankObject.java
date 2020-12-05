package org.light.source.Singleton;

import java.util.UUID;

public class RankObject {

    public int kill;
    public int death;
    public UUID UUID;

    public RankObject(UUID uuid, KillDeathObject object){
        this.UUID = uuid;
        this.kill = object.getKill();
        this.death = object.getDeath();
    }

}
