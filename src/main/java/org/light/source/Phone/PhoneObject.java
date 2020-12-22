package org.light.source.Phone;

import java.util.UUID;

public class PhoneObject {

    private UUID uuid;
    private boolean isPhone;

    public PhoneObject(UUID uuid, boolean isPhone){
        this.uuid = uuid;
        this.isPhone = isPhone;
    }

    public boolean getPhoneState(){
        return isPhone;
    }

    public UUID getUuid() {
        return uuid;
    }
}
