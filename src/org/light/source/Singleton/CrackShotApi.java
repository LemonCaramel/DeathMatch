package org.light.source.Singleton;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.inventory.ItemStack;

public class CrackShotApi {

    private static CSUtility utility;

    static {
        utility = new CSUtility();
    }

    public static ItemStack getCSWeapon(String WeaponName){
        return utility.generateWeapon(WeaponName);
    }

    public static String getCSID(ItemStack Weapon){
        return utility.getWeaponTitle(Weapon);
    }

    public CSUtility getUtility(){
        return utility;
    }
}
