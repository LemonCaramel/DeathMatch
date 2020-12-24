package org.light.source.Singleton;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import me.DeeCaaD.CrackShotPlus.CSPapi;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
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
        /*CSDirector director = getPlugin();
        int size = director.wlist.size();
        int random = ThreadLocalRandom.current().nextInt(1, size);
        String weapon = director.wlist.get(random);
        if (weapon == null){
            weapon = director.wlist.get(1);
        }
        return CSPapi.updateItemStackFeaturesNonPlayer(weapon,getCSWeapon(weapon)); 이벤트 주석*/
        return CSPapi.updateItemStackFeaturesNonPlayer("DEAGLE_Golden", utility.generateWeapon("DEAGLE_Golden"));
    }

    public static ItemStack generateNotOPWeapon(){
        CSDirector director = getPlugin();
        ArrayList<String> list = new ArrayList<>(director.wlist.values());
        Collections.shuffle(list);
        while(list.get(0) == null || list.get(0).equalsIgnoreCase("CHICKENGUN") || list.get(0).equalsIgnoreCase("C4_Ultimate"))
            Collections.shuffle(list);
        return CSPapi.updateItemStackFeaturesNonPlayer(list.get(0),getCSWeapon(list.get(0)));
    }
}
