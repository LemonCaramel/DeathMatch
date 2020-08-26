package org.light.source.Command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.light.source.DeathMatch;
import org.light.source.Singleton.DataManager;

public class CommandController implements CommandExecutor {

    private DeathMatch Plugin;
    private static String first;

    static {
        first = "§c[ §fDeathMatch §6] ";
    }
    public CommandController(DeathMatch Plugin){
        this.Plugin = Plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command c, String s, String[] args) {
        if (s.equalsIgnoreCase("데스매치")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (args.length >= 1 && correctArg(args[0])){
                    if (args[0].equalsIgnoreCase("참여")){
                        //To-Do
                    }
                    else if (args[0].equalsIgnoreCase("정보")){
                        //To-Do
                    }
                    else{
                        if (p.hasPermission("DeathMatch.Control")){
                            if (args[0].equalsIgnoreCase("관리")){
                                if (args.length >= 2 && (args[1].equalsIgnoreCase("확인") || args[1].equalsIgnoreCase("추방") || args[1].equalsIgnoreCase("변수설정"))){
                                    if (args[1].equalsIgnoreCase("확인")){
                                        currentDataInfo(p);
                                    }
                                    else{

                                    }
                                }
                                else{
                                    settingInfo(p);
                                }
                            }
                            else if (args[0].equalsIgnoreCase("설정")){
                                //To-Do
                            }
                            else if (args[0].equalsIgnoreCase("강제종료")){
                                //To-Do
                            }
                        }
                        else{
                            info(p);
                        }
                    }
                }
                else{
                    info(p);
                }
            }
            else{
                sender.sendMessage(first + "§c콘솔은 사용 불가능한 명령어 입니다.");
            }
            return true;
        }
        return false;
    }

    public void info(Player p){
        if (p.hasPermission("DeathMatch.Control"))
            p.sendMessage(first + "§f/데스매치 <참여/정보/관리/설정/강제종료>");
        else
            p.sendMessage(first + "§f/데스매치 <참여/정보>");
    }

    public boolean correctArg(String value){
        return value.equalsIgnoreCase("참여") || value.equalsIgnoreCase("정보") || value.equalsIgnoreCase("관리") || value.equalsIgnoreCase("설정") || value.equalsIgnoreCase("강제종료");
    }

    public void settingInfo(Player p){
        p.sendMessage(first + "§b/데스매치 관리 <확인/설정>");
    }

    public void currentDataInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage(first + "§bRounds §7: §6" + DataManager.getInstance().getRounds() + "§f라운드");
        p.sendMessage(first + "§cMaxKill §7: §6" + DataManager.getInstance().getKills() + "§f킬");
        p.sendMessage(first + "§aMaxTime §7: §6" + DataManager.getInstance().getTime() + "§f초");
        if (DataManager.getInstance().getLocations() == null)
            p.sendMessage(first + "§7Location §7: §c설정되지 않음");
        else{
            p.sendMessage(first + "§7Location §a1 §7: " + locationToString(DataManager.getInstance().getLocations()[0]));
            p.sendMessage(first + "§7Location §b2 §7: " + locationToString(DataManager.getInstance().getLocations()[1]));
        }
        p.sendMessage(" ");
    }

    public String locationToString(Location loc){
        return "§7[ §fX : " + Math.round(loc.getX())+ ", Y : " +  Math.round(loc.getY())+ ", Z : " +  Math.round(loc.getZ()) + ", World : " + loc.getWorld().getName() + " §7]";
    }
}
