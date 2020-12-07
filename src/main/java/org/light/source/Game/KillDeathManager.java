package org.light.source.Game;

import org.light.source.Singleton.KillDeathObject;

import java.util.HashMap;
import java.util.UUID;

public class KillDeathManager {

    private static KillDeathManager instance;
    private HashMap<UUID, KillDeathObject> list;

    static {
        instance = new KillDeathManager();
    }

    private KillDeathManager() {
        list = new HashMap<>();
    }

    public static KillDeathManager getInstance() {
        return instance;
    }

    public KillDeathObject setValue(String name, UUID uid, int kill, int death) {
        if (list.containsKey(uid))
            list.get(uid)
                    .setKill(kill)
                    .setDeath(death);
        else
            list.put(uid, new KillDeathObject(name, kill, death));
        return list.get(uid);
    }

    public KillDeathObject getValue(UUID uuid){
        if (list.containsKey(uuid))
            return list.get(uuid);
        else
            return setValue("Offline", uuid,0,0);
    }

    public HashMap<UUID, KillDeathObject> getList(){
        return list;
    }

}
