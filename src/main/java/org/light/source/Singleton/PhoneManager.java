package org.light.source.Singleton;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.light.source.Phone.PhoneObject;

import java.util.ArrayList;
import java.util.UUID;

public class PhoneManager {

    private static PhoneManager instance;
    private ObjectArrayList<PhoneObject> phoneObjects;

    static {
        instance = new PhoneManager();
    }

    private PhoneManager(){
        phoneObjects = new ObjectArrayList<>();
    }

    public void addObject(UUID uuid, boolean isphone){
        phoneObjects.add(new PhoneObject(uuid, isphone));
    }

    public boolean contains(UUID uuid){
        for (PhoneObject object : phoneObjects){
            if (object.getUuid().equals(uuid))
                return true;
        }
        return false;
    }

    public ObjectArrayList<PhoneObject> getPhoneObjects(){
        return phoneObjects;
    }

    public static PhoneManager getInstance(){
        return instance;
    }
}
