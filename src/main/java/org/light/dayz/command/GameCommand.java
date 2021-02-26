package org.light.dayz.command;

import me.DeeCaaD.CrackShotPlus.CSPapi;
import moe.caramel.caramellibrarylegacy.api.FontAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.light.dayz.data.YamlConfig;
import org.light.dayz.game.GameController;
import org.light.dayz.util.Regen;
import org.light.source.Game.GameManager;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.DataManager;

import java.util.ArrayList;

public class GameCommand implements CommandExecutor {

    private YamlConfig config;

    public GameCommand(YamlConfig config) {
        this.config = config;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (strings.length == 1 && strings[0].equalsIgnoreCase("참여"))
                GameController.addPlayer(p);
            else if (strings.length == 1 && strings[0].equalsIgnoreCase("보급품"))
                checkKit(p);
            else {
                if (!p.isOp())
                    p.sendMessage("§c[ §f! §c] §f/dayz [참여/보급품]");
                else {
                    if (strings.length == 1 && strings[0].equalsIgnoreCase("목록"))
                        locationInfo(p);
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("추가")) {
                        config.getLocations().add(p.getLocation());
                        p.sendMessage("§b현재 위치가 랜덤스폰 장소에 추가되었습니다.");
                    }
                    else if (strings.length == 2 && strings[0].equalsIgnoreCase("삭제") && parseInt(strings[1]) != -1 && config.getLocations().size() > parseInt(strings[1])){
                        config.getLocations().remove(parseInt(strings[1]));
                        p.sendMessage("§b해당 인덱스의 위치가 제거되었습니다.");
                    }
                    else if (strings.length == 2 && strings[0].equalsIgnoreCase("이동") && parseInt(strings[1]) != -1 && config.getLocations().size() > parseInt(strings[1])) {
                        p.teleport(config.getLocations().get(parseInt(strings[1])));
                        p.sendMessage("§6해당 위치로 이동하였습니다.");
                    }
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("저장")) {
                        config.save();
                        p.sendMessage("§c콘피그가 저장되었습니다.");
                    }
                    else if (strings.length == 1 && strings[0].equalsIgnoreCase("리로드")) {
                        config.load();
                        p.sendMessage("§b콘피그가 로드되었습니다.");
                    }
                    else
                        p.sendMessage("§c[ §f! §c] §f/dayz [참여/보급품/목록/추가/삭제/이동/저장/리로드]");
                }
            }
            return true;
        }
        return false;
    }

    public void locationInfo(Player p) {
        ArrayList<Location> locations = config.getLocations();
        if (locations.size() != 0) {
            FontAPI.sendCenteredMessage(p, "§b맨 윗줄부터 인덱스 0번입니다.");
            p.sendMessage(" ");
            locations.forEach(data -> p.sendMessage(locationToString(data)));
        }
        else
            p.sendMessage("§c지정된 데이터가 없습니다.");
    }

    public String locationToString(Location loc) {
        return "§7[ §fX : " + Math.round(loc.getX()) + ", Y : " + Math.round(loc.getY()) + ", Z : " + Math.round(loc.getZ()) + ", World : " + loc.getWorld().getName() + " §7]";
    }

    public int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    public void checkKit(Player p) {
        if (GameManager.getInstance().contains(p.getUniqueId()))
            p.sendMessage("§c[ §f! §c] §c데스매치에 참여한 상태로 사용할 수 없습니다.");
        else if (Regen.isWeaponGet(p.getUniqueId()))
            p.sendMessage("§c[ §f! §c] §f아직 보급품을 흭득할 수 없습니다.");
        else
            p.openInventory(getKitInventory());
    }

    public Inventory getKitInventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, "§0보급품 받기");
        for (int i = 0; i < 54; i++) {
            if (config.getHelpWeapon().size() == i)
                break;
            inventory.setItem(i, CSPapi.updateItemStackFeaturesNonPlayer(config.getHelpWeapon().get(i), CrackShotApi.getCSWeapon(config.getHelpWeapon().get(i))));
        }
        return inventory;
    }
}
