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
import org.light.source.Log.MinimizeLogger;
import org.light.source.Phone.PhoneObject;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.DataManager;
import org.light.source.Singleton.FileManager;
import org.light.source.Singleton.PhoneManager;

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
                if (PhoneManager.getInstance().contains(p.getUniqueId())) {
                    if (checkPhone(p)){
                        p.sendMessage(first + " §c모바일로는 플레이가 불가능합니다.");
                        return true;
                    }
                    if (args.length >= 1 && correctArg(args[0])) {
                        if (args[0].equalsIgnoreCase("참여")) {
                            if (GameManager.getInstance().contains(p.getUniqueId())) {
                                p.sendMessage(first + "§c이미 참여중입니다.");
                            } else {
                                GameManager.getInstance().addPlayer(p);
                                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치에 참여함");
                            }
                        } else if (args[0].equalsIgnoreCase("정보")) {
                            currentGameInfo(p);
                        } else if (args[0].equalsIgnoreCase("나가기")) {
                            if (GameManager.getInstance().contains(p.getUniqueId())) {
                                GameManager.getInstance().removePlayer(p);
                                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치에서 퇴장함");
                            } else {
                                p.sendMessage(first + "§6게임에 참여하지 않으셨습니다.");
                            }
                        } else {
                            if (p.hasPermission("DeathMatch.Control")) {
                                if (args[0].equalsIgnoreCase("관리")) {
                                    if (args.length >= 2 && (args[1].equalsIgnoreCase("확인") || args[1].equalsIgnoreCase("추방"))) {
                                        if (args[1].equalsIgnoreCase("확인")) {
                                            currentDataInfo(p);
                                        } else if (args[1].equalsIgnoreCase("추방")) {
                                            if (args.length == 3) {
                                                Player target = Bukkit.getServer().getPlayer(args[2]);
                                                if (target == null || !GameManager.getInstance().contains(target.getUniqueId()))
                                                    p.sendMessage(first + "§4해당 플레이어는 온라인이 아니거나 게임에 참여하지 않은 상태입니다.");
                                                else {
                                                    GameManager.getInstance().removePlayer(target);
                                                    MinimizeLogger.getInstance().appendLog(p.getName() + "님이 " + target.getName() + "님을 추방함");
                                                }
                                            } else {
                                                p.sendMessage(first + "§c추방할 플레이어를 입력해주세요");
                                            }
                                        }
                                    } else {
                                        settingInfo(p);
                                    }
                                } else if (args[0].equalsIgnoreCase("설정")) {
                                    if (args.length >= 2 && (args[1].equalsIgnoreCase("라운드") || args[1].equalsIgnoreCase("킬") || args[1].equalsIgnoreCase("시간") || args[1].equalsIgnoreCase("최소인원") || args[1].equalsIgnoreCase("위치") || args[1].equalsIgnoreCase("총기") || args[1].equalsIgnoreCase("참여보상") || args[1].equalsIgnoreCase("1위보상") || args[1].equalsIgnoreCase("2위보상") || args[1].equalsIgnoreCase("3위보상") || args[1].equalsIgnoreCase("킬지속시간"))) {
                                        if (args[1].equalsIgnoreCase("라운드")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 라운드 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= 0)
                                                        p.sendMessage(first + "§c수치는 0이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setRounds(value);
                                                        p.sendMessage(first + "§f데스매치 라운드 수가 §6" + value + "§f라운드로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("킬")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 킬 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= 1)
                                                        p.sendMessage(first + "§c수치는 1이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setKilltolevel(value);
                                                        p.sendMessage(first + "§f데스매치 레벨업당 킬수가 §6" + value + "§f킬로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("시간")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 시간 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= 9)
                                                        p.sendMessage(first + "§c수치는 9이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setTime(value);
                                                        p.sendMessage(first + "§f데스매치 시간이 §6" + value + "§f초로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("최소인원")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 최소인원 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= 1)
                                                        p.sendMessage(first + "§c수치는 1이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setMinimumUser(value);
                                                        p.sendMessage(first + "§f데스매치 최소인원이 §6" + value + "§f명 으로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("위치")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 위치 <1~21>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value < 1 || value > 21)
                                                        p.sendMessage(first + "§c위치 값은 1~21이 와야합니다.");
                                                    else {
                                                        DataManager.getInstance().setLocations(p.getLocation(), value);
                                                        p.sendMessage(first + "§f데스매치 구역 " + value + "이 §6" + locationToString(p.getLocation()) + "§f으로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("총기")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 총기 <라운드 (시작 총기 = 0, 근접무기 = -1)>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value < -1)
                                                        p.sendMessage(first + "§c총기 라운드 값은 -1미만이 올 수 없습니다.");
                                                    else if (DataManager.getInstance().getRounds() == 0)
                                                        p.sendMessage(first + "§c라운드값이 설정되어 있지 않습니다.");
                                                    else if (DataManager.getInstance().getRounds() <= value)
                                                        p.sendMessage(first + "§c설정되어 있는 라운드값보다 더 큰 값을 입력하셨습니다.");
                                                    else if (CrackShotApi.getCSID(p.getInventory().getItemInMainHand()) == null)
                                                        p.sendMessage(first + "§c현재 들고 있는 아이템이 크랙샷 아이템이 아닙니다.");
                                                    else {
                                                        String weaponName = CrackShotApi.getCSID(p.getInventory().getItemInMainHand());
                                                        DataManager.getInstance().setWeapon(value, weaponName);
                                                        p.sendMessage(first + "§b" + value + "§f번째 데스매치 총기가 §c" + weaponName + " §f으로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("참여보상")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 참여보상 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= -1)
                                                        p.sendMessage(first + "§c수치는 -1이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setJoinMoney(value);
                                                        p.sendMessage(first + "§f데스매치 참여보상이 §6" + value + "§f원 으로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("1위보상")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 1위보상 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= -1)
                                                        p.sendMessage(first + "§c수치는 -1이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setFirstReward(value);
                                                        p.sendMessage(first + "§f데스매치 1위보상이 §6" + value + "§f원 으로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("2위보상")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 2위보상 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= -1)
                                                        p.sendMessage(first + "§c수치는 -1이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setSecondReward(value);
                                                        p.sendMessage(first + "§f데스매치 2위보상이 §6" + value + "§f원 으로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("3위보상")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 3위보상 <수치>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= -1)
                                                        p.sendMessage(first + "§c수치는 -1이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setThirdReward(value);
                                                        p.sendMessage(first + "§f데스매치 3위보상이 §6" + value + "§f원 으로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        } else if (args[1].equalsIgnoreCase("킬지속시간")) {
                                            if (args.length != 3)
                                                p.sendMessage(first + "§c/데스매치 설정 킬지속시간 <초>");
                                            else {
                                                try {
                                                    int value = Integer.parseInt(args[2]);
                                                    if (value <= -1)
                                                        p.sendMessage(first + "§c수치는 -1이하의 값으로 설정할 수 없습니다..");
                                                    else {
                                                        DataManager.getInstance().setKillMaintain(value);
                                                        p.sendMessage(first + "§f데스매치 킬 지속시간이 §6" + value + "§f초로 지정되었습니다.");
                                                    }
                                                } catch (NumberFormatException e) {
                                                    p.sendMessage(first + "§c올바른 수치를 입력해주세요.");
                                                }
                                            }
                                        }
                                    } else {
                                        valuesettinginfo(p);
                                    }
                                } else if (args[0].equalsIgnoreCase("강제종료")) {
                                    if (GameManager.getInstance().isgaming()) {
                                        Bukkit.broadcastMessage(first + "§4관리자에 의해 데스매치가 강제종료 되었습니다.");
                                        GameManager.getInstance().stop();
                                        MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치를 강제종료함");
                                    }
                                } else if (args[0].equalsIgnoreCase("리로드")) {
                                    DataManager.getInstance().flushLocation();
                                    FileManager.getInstance().load();
                                    p.sendMessage(first + "§f콘피그 파일이 리로드 되었습니다.");
                                }
                            } else {
                                info(p);
                            }
                        }
                    } else {
                        info(p);
                    }
                }
                else{
                    p.sendMessage(first + "§f데이터 로딩중입니다. 잠시만 기다려주세요.");
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
            p.sendMessage(first + "§f/데스매치 <참여/정보/나가기/관리/설정/강제종료/리로드>");
        else
            p.sendMessage(first + "§f/데스매치 <참여/정보/나가기>");
    }

    public boolean correctArg(String value){
        return value.equalsIgnoreCase("참여") || value.equalsIgnoreCase("정보") || value.equalsIgnoreCase("나가기") || value.equalsIgnoreCase("관리") || value.equalsIgnoreCase("설정") || value.equalsIgnoreCase("강제종료") || value.equalsIgnoreCase("리로드");
    }

    public void settingInfo(Player p){
        p.sendMessage(first + "§b/데스매치 관리 <확인/추방>");
    }

    public void currentDataInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage(first + "§bRounds §7: §6" + DataManager.getInstance().getRounds() + "§f라운드");
        p.sendMessage(first + "§cKillToLevel §7: §6" + DataManager.getInstance().getKilltolevel() + "§f킬");
        p.sendMessage(first + "§aMaxTime §7: §6" + DataManager.getInstance().getTime() + "§f초");
        p.sendMessage(first + "§8MinimumUser §7: §6" + DataManager.getInstance().getMinimumUser() + "§f명");
        p.sendMessage(first + "§6킬 지속시간 §7: §6" + DataManager.getInstance().getKillMaintain() + "§f초");
        p.sendMessage(first + "§f참여 보상 §7: §6" + DataManager.getInstance().getJoinMoney() + "§f원");
        p.sendMessage(first + "§c1위 보상 §7: §6" + DataManager.getInstance().getFirstReward() + "§f원");
        p.sendMessage(first + "§b2위 보상 §7: §6" + DataManager.getInstance().getSecondReward() + "§f원");
        p.sendMessage(first + "§a3위 보상 §7: §6" + DataManager.getInstance().getThirdReward() + "§f원");
        if (DataManager.getInstance().getLocations() == null)
            p.sendMessage(first + "§7Location §7: §c설정되지 않음");
        else{
            for (int i = 1; i < DataManager.getInstance().getLocationAmount(); i++){
                p.sendMessage(first + "§7Location §6" + (i+1) + " §7: " + locationToString(DataManager.getInstance().getLocations()[i]));
            }
            p.sendMessage(first + "§6SpawnLocation [ 1 ]  §7: " + locationToString(DataManager.getInstance().getLocations()[0]));
        }
        if (DataManager.getInstance().getRounds() != 0){
            for (int i = 0; i < DataManager.getInstance().getRounds(); i++){
                p.sendMessage(first + "§6Weapon §7- §f" + i + ". §8: §f" + changeWeaponValue(DataManager.getInstance().getWeaponName(i)));
            }
            p.sendMessage(first + "§bMelee Weapon §7: §f" + changeWeaponValue(DataManager.getInstance().getWeaponName(-1)));
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
            p.sendMessage(" ");
            p.sendMessage(first + "§7참여 인원 §7: §b" + GameManager.getInstance().getusercount());
            for (UserMananger mananger : GameManager.getInstance().getUserlist())
                p.sendMessage("  §7-  §f" + Bukkit.getServer().getPlayer(mananger.getUUID()).getName());
        }
        p.sendMessage(" ");
    }

    public String changeWeaponValue(String value){
        if (value == null)
            return "§4X";
        else
            return value;
    }
    public void valuesettinginfo(Player p){
        p.sendMessage(first + "§c/데스매치 설정 <라운드/킬/시간/최소인원/위치/총기/참여보상/1위보상/2위보상/3위보상/킬지속시간> <값/1~21>");
    }

    public String locationToString(Location loc){
        return "§7[ §fX : " + Math.round(loc.getX())+ ", Y : " +  Math.round(loc.getY())+ ", Z : " +  Math.round(loc.getZ()) + ", World : " + loc.getWorld().getName() + " §7]";
    }

    public boolean checkPhone(Player p) {
        for (PhoneObject object : PhoneManager.getInstance().getPhoneObjects()){
            if (object.getUuid().equals(p.getUniqueId()))
                return object.getPhoneState();
        }
        return true;
    }
}
