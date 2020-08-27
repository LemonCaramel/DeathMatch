package org.light.source.Game;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.light.source.DeathMatch;
import org.light.source.Singleton.DataManager;

import java.util.ArrayList;
import java.util.UUID;

public class GameManager {

    private static GameManager manager;
    private boolean isgaming;
    private ArrayList<UserMananger> userlist;
    private DeathMatch Plugin;
    private int taskid;
    private int counttaskid;
    private int countnum;

    static {
        manager = new GameManager();
    }
    //canceltask 안되는 이유 찾기
    private GameManager(){
        userlist = new ArrayList<>();
        isgaming = false;
        counttaskid = 0;
        countnum = 100;
        Plugin = JavaPlugin.getPlugin(DeathMatch.class);
        taskid = Bukkit.getScheduler().runTaskTimerAsynchronously(Plugin, this::sendActionBar, 0L, 20L).getTaskId();
    }

    public static GameManager getInstance(){
        return manager;
    }

   public boolean isgaming(){
        return isgaming;
   }

   public void setGameState(boolean GameState){
        isgaming = GameState;
   }

    public void addPlayer(Player p) {
        for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendMessage("§c[ §fDeathMatch §6] §b" + p.getName() + "§f님이 §c데스매치§f에 참여하셨습니다.");
        }
        userlist.add(new UserMananger(p.getUniqueId()));
        if (isgaming){
            //게임도중 참여
        }
        else if (canstart() && getusercount() >= DataManager.getInstance().getMinimumUser()){
            for (UserMananger mananger : userlist) {
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f);
                target.sendTitle("§c[ §fDeathMatch §6] §b시작 준비!", "§c최소 인원이 충족되어 곧 게임이 시작됩니다.", 5,20,5);
                Bukkit.getServer().getScheduler().runTaskLater(Plugin, this::countdown, 25L);
            }
        }
    }

    public void removePlayer(Player p){
        userlist.removeIf(userMananger -> userMananger.getUUID().equals(p.getUniqueId()));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§c[ §fDeathMatch §6] §f데스매치에서 퇴장하셨습니다."));
        for (UserMananger mananger : userlist){
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            target.sendMessage("§c[ §fDeathMatch §6] §b" + p.getName() + "§f님이 §c데스매치§f에서 퇴장하셨습니다.");
        }
        if (getusercount() + 1 == DataManager.getInstance().getMinimumUser()){
            Bukkit.getScheduler().cancelTask(counttaskid);
            for (UserMananger mananger : userlist) {
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                target.sendMessage("§c[ §fDeathMatch §6] §c준비 도중 플레이어가 나가 중단 되었습니다.");
            }
        }
    }

    public void sendActionBar(){
        for (UserMananger mananger : userlist) {
            Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
            String value;
            if (!isgaming)
                value = "§c[ §fDeathMatch §6] §b플레이어의 참여를 기다리는 중입니다... §f" + userlist.size() + " §7/ §6" + DataManager.getInstance().getMinimumUser();
            else if (getusercount() >= DataManager.getInstance().getMinimumUser())
                value = "§c[ §fDeathMatch §6] §a게임 시작 준비중입니다..§f" + userlist.size() + " §7/ §6" + DataManager.getInstance().getMinimumUser();
            else
                value = "§c[ §fDeathMatch §6] §c현재 게임이 진행중입니다..§f" + userlist.size() + " §7/ §6" + DataManager.getInstance().getMinimumUser();
            //To-Do
            target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(value));

        }
    }

    public int getusercount(){
        return userlist.size();
    }

    public ArrayList<UserMananger> getUserlist(){
        return userlist;
    }
    public boolean contains(UUID uuid){
        for (UserMananger mananger : userlist){
            if (mananger.getUUID() == uuid)
                return true;
        }
        return false;
    }
    private void countdown(){
        if (countnum != 100)
            countnum = 100;
        counttaskid = Bukkit.getScheduler().runTaskTimer(Plugin,()->{
            for (UserMananger mananger : userlist){
                if (countnum <= 0){
                    Bukkit.getScheduler().cancelTask(counttaskid);
                    //start
                }
                double value = formatteddouble((double)countnum / 20);
                Player target = Bukkit.getServer().getPlayer(mananger.getUUID());
                target.sendTitle("§c[ §fDeathMatch §6] §b카운트 다운!", "§6" + value + "s §f초후 시작합니다.", 0,3,0);
                countnum -= 2;

            }
        },0L, 2L).getTaskId();
    }
    private void start(){
        setGameState(true);
    }

    private void stop(){
        setGameState(false);
    }

    public boolean canstart(){
        DataManager manager = DataManager.getInstance();
        if (manager.getTime() >= 10 && manager.getKilltolevel() >= 1 && manager.getLocations() != null && manager.getRounds() >= 1)
            return true;
        return false;
    }

    public double formatteddouble(double value){
        String format = String.format("%.1f",value);
        return Double.parseDouble(format);
    }
}
