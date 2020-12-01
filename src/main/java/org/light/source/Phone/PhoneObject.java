package org.light.source.Phone;

import java.util.UUID;

public class PhoneObject {

    private UUID uuid;
    private boolean isphone;

    public PhoneObject(UUID uuid, boolean isphone){
        this.uuid = uuid;
        this.isphone = isphone;
    }

    public boolean getPhoneState(){
        return isphone;
    }

    public UUID getUuid() {
        return uuid;
    }
}
