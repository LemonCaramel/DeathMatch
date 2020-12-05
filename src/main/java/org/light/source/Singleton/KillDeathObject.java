package org.light.source.Singleton;

import java.util.UUID;

public class KillDeathObject {

    private int kill;
    private int death;

    public KillDeathObject(int kill, int death){
        this.kill = kill;
        this.death = death;
    }

    public KillDeathObject setKill(int kill){
        this.kill = kill;
        return this;
    }

    public int getKill(){
        return kill;
    }

    public KillDeathObject setDeath(int death){
        this.death = death;
        return this;
    }

    public int getDeath(){
        return death;
    }

}
