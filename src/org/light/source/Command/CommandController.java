package org.light.source.Command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.light.source.DeathMatch;
import org.light.source.Game.GameManager;
import org.light.source.Game.UserMananger;
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
                        if (GameManager.getInstance().contains(p.getUniqueId())) {
                            p.sendMessage(first + "§c이미 참여중입니다.");
                        }
                        else{
                            GameManager.getInstance().addPlayer(p);
                        }
                    }
                    else if (args[0].equalsIgnoreCase("정보")){
                        currentGameInfo(p);
                    }
                    else if (args[0].equalsIgnoreCase("나가기")){
                        if (GameManager.getInstance().contains(p.getUniqueId())) {
                            GameManager.getInstance().removePlayer(p);
                        }
                        else {
                            p.sendMessage(first + "§6게임에 참여하지 않으셨습니다.");
                        }
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
                                if (args.length >= 2 && (args[1].equalsIgnoreCase("라운드") || args[1].equalsIgnoreCase("킬") || args[1].equalsIgnoreCase("시간") || args[1].equalsIgnoreCase("최소인원") || args[1].equalsIgnoreCase("위치"))){
                                    if (args[1].equalsIgnoreCase("라운드")){
                                        if (args.length != 3)
                                            p.sendMessage(first + "&c/데스매치 설정 라운드 <수치>");
                                        else{
                                            try{
                                                int value = Integer.parseInt(args[2]);
                                                if (value <= 0)
                                                    p.sendMessage(first + "§c수치는 0이하의 값으로 설정할 수 없습니다..");
                                                else {
                                                    DataManager.getInstance().setRounds(value);
                                                    p.sendMessage(first + "§f데스매치 라운드 수가 §6" + value + "§f라운드로 지정되었습니다.");
                                                }
                                            }
                                            catch (NumberFormatException e){
                                                p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                            }
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("킬")) {
                                        if (args.length != 3)
                                            p.sendMessage(first + "§c/데스매치 설정 킬 <수치>");
                                        else{
                                            try{
                                                int value = Integer.parseInt(args[2]);
                                                if (value <= 0)
                                                    p.sendMessage(first + "§c수치는 0이하의 값으로 설정할 수 없습니다..");
                                                else {
                                                    DataManager.getInstance().setKilltolevel(value);
                                                    p.sendMessage(first + "§f데스매치 레벨업당 킬수가 §6" + value + "§f킬로 지정되었습니다.");
                                                }
                                            }
                                            catch (NumberFormatException e){
                                                p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                            }
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("시간")) {
                                        if (args.length != 3)
                                            p.sendMessage(first + "§c/데스매치 설정 시간 <수치>");
                                        else{
                                            try{
                                                int value = Integer.parseInt(args[2]);
                                                if (value <= 9)
                                                    p.sendMessage(first + "§c수치는 9이하의 값으로 설정할 수 없습니다..");
                                                else {
                                                    DataManager.getInstance().setTime(value);
                                                    p.sendMessage(first + "§f데스매치 시간이 §6" + value + "§f초로 지정되었습니다.");
                                                }
                                            }
                                            catch (NumberFormatException e){
                                                p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                            }
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("최소인원")) {
                                        if (args.length != 3)
                                            p.sendMessage(first + "§c/데스매치 설정 최소인원 <수치>");
                                        else{
                                            try{
                                                int value = Integer.parseInt(args[2]);
                                                if (value <= 1)
                                                    p.sendMessage(first + "§c수치는 1이하의 값으로 설정할 수 없습니다..");
                                                else {
                                                    DataManager.getInstance().setMinimumUser(value);
                                                    p.sendMessage(first + "§f데스매치 최소인원이 §6" + value + "§f명 으로 지정되었습니다.");
                                                }
                                            }
                                            catch (NumberFormatException e){
                                                p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                            }
                                        }
                                    }
                                    else if (args[1].equalsIgnoreCase("위치")) {
                                        if (args.length != 3)
                                            p.sendMessage(first + "§c/데스매치 설정 위치 <1/2>");
                                        else{
                                            try{
                                                int value = Integer.parseInt(args[2]);
                                                if (value != 1 && value != 2)
                                                    p.sendMessage(first + "§c위치 값은 1또는 2가 와야합니다.");
                                                else {
                                                    DataManager.getInstance().setLocations(p.getLocation(), value);
                                                    p.sendMessage(first + "§f데스매치 구역 " + value + "이 §6" + locationToString(p.getLocation()) + "§f으로 지정되었습니다.");
                                                }
                                            }
                                            catch (NumberFormatException e){
                                                p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                            }
                                        }
                                    }
                                }
                                else{
                                    valuesettinginfo(p);
                                }
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
            p.sendMessage(first + "§f/데스매치 <참여/정보/나가기/관리/설정/강제종료>");
        else
            p.sendMessage(first + "§f/데스매치 <참여/정보/나가기>");
    }

    public boolean correctArg(String value){
        return value.equalsIgnoreCase("참여") || value.equalsIgnoreCase("정보") || value.equalsIgnoreCase("나가기") || value.equalsIgnoreCase("관리") || value.equalsIgnoreCase("설정") || value.equalsIgnoreCase("강제종료");
    }

    public void settingInfo(Player p){
        p.sendMessage(first + "§b/데스매치 관리 <확인/추방/변수설정>");
    }

    public void currentDataInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage(first + "§bRounds §7: §6" + DataManager.getInstance().getRounds() + "§f라운드");
        p.sendMessage(first + "§cKillToLevel §7: §6" + DataManager.getInstance().getKilltolevel() + "§f킬");
        p.sendMessage(first + "§aMaxTime §7: §6" + DataManager.getInstance().getTime() + "§f초");
        p.sendMessage(first + "§8MinimumUser §7: §6" + DataManager.getInstance().getMinimumUser() + "§f명");
        if (DataManager.getInstance().getLocations() == null)
            p.sendMessage(first + "§7Location §7: §c설정되지 않음");
        else{
            p.sendMessage(first + "§7Location §a1 §7: " + locationToString(DataManager.getInstance().getLocations()[0]));
            p.sendMessage(first + "§7Location §b2 §7: " + locationToString(DataManager.getInstance().getLocations()[1]));
        }
        p.sendMessage(" ");
    }

    public void currentGameInfo(Player p){
        p.sendMessage(" ");
        if (!GameManager.getInstance().isgaming()) {
            p.sendMessage(first + "§b게임 상태 §7: §6유저 대기중..");
            p.sendMessage(" ");
            p.sendMessage(first + "§7참여 인원 §7: §b" + GameManager.getInstance().getusercount());
            for (UserMananger mananger : GameManager.getInstance().getUserlist())
                p.sendMessage("  §7-  §f" + Bukkit.getServer().getPlayer(mananger.getUUID()).getName());
        }
        else {
            p.sendMessage(first + "§b게임 상태 §7: §c게임 진행중");
        }
        p.sendMessage(" ");
    }

    public void valuesettinginfo(Player p){
        p.sendMessage(first + "§c/데스매치 설정 <라운드/킬/시간/최소인원/위치> <값/1/2>");
    }

    public String locationToString(Location loc){
        return "§7[ §fX : " + Math.round(loc.getX())+ ", Y : " +  Math.round(loc.getY())+ ", Z : " +  Math.round(loc.getZ()) + ", World : " + loc.getWorld().getName() + " §7]";
    }
}
