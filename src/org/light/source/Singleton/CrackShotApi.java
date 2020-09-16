package org.light.source.Singleton;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSMinion;
import com.shampaggon.crackshot.CSUtility;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

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

    public static CSDirector getPlugin(){
        return utility.getHandle();
    }

    public static ItemStack generateRandomWeapon(){
        CSDirector director = getPlugin();
        int size = director.wlist.size();
        int random = ThreadLocalRandom.current().nextInt(1, size);
        String weapon = director.wlist.get(random);
        if (weapon == null){
            weapon = director.wlist.get(1);
        }
        return getCSWeapon(weapon);
    }
}
