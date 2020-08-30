package org.light.source.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;

import java.util.UUID;

public class RatingManager {

    private static RatingManager manager;
    private String[] strings; //3개짜리
    private int[] kills; //얘두

    static {
        manager = new RatingManager();
    }

    private RatingManager(){
        strings = new String[3];
        kills = new int[3];
    }

    public static RatingManager getInstance(){
        return manager;
    }

    public String getFirst(){
        return strings[0];
    }

    public String getSecond(){
        return strings[1];
    }

    public String getThird(){
        return strings[2];
    }

    public void updateRank(){
        strings[0] = strings[1] = strings[2] = null;
        kills[0] = kills[1] = kills[2] = 0;
        for (UserMananger mananger : GameManager.getInstance().getUserlist()){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            int kill = mananger.getKills();
            if (strings[0] == null || kills[0] < kill){
                strings[0] = target.getName();
                kills[0] = kill;
            }
            else if (strings[1] == null || kills[1] < kill){
                strings[1] = target.getName();
                kills[1] = kill;
            }
            else if (strings[2] == null || kills[2] < kill){
                strings[2] = target.getName();
                kills[2] = kill;
            }
        }
        //왜 자꾸 null아닌데 2번값이 null이 떠 ㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠㅠ
    }
}
