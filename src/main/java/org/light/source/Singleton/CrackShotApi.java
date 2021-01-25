package org.light.source.Singleton;

import com.shampaggon.crackshot.CSDirector;
import com.shampaggon.crackshot.CSUtility;
import me.DeeCaaD.CrackShotPlus.CSPapi;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CrackShotApi {

    private static CSUtility utility;
    private static Map<String, ItemStack> csItems;

    static {
        utility = new CSUtility();
        csItems = new HashMap<>();
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
        int random = ThreadLocalRandom.current().nextInt(1, csItems.size());
        ArrayList<String> list = new ArrayList<>(csItems.keySet());
        ItemStack weapon = csItems.get(list.get(random));
        if (weapon == null)
            weapon = csItems.get(list.get(0));
        return weapon;
    }

    public static ItemStack generateNotOPWeapon(){
        ArrayList<String> list = new ArrayList<>(csItems.keySet());
        Collections.shuffle(list);
        while(list.get(0) == null || list.get(0).equalsIgnoreCase("CHICKENGUN") || list.get(0).equalsIgnoreCase("C4_Ultimate"))
            Collections.shuffle(list);
        return csItems.get(list.get(0));
    }

    public static void generateWeaponMap() {
        csItems.clear();
        for (String weapon : getPlugin().wlist.values())
            csItems.put(weapon, CSPapi.updateItemStackFeaturesNonPlayer(weapon, getCSWeapon(weapon)));
    }
}
