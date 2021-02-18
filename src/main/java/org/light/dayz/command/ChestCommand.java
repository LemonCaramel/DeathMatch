package org.light.dayz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.light.dayz.game.GameController;
import org.light.dayz.util.VirtualChest;
import org.light.source.Game.GameManager;

public class ChestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GameController.contains(p.getUniqueId()) || GameManager.getInstance().contains(p.getUniqueId()))
                p.sendMessage("§4게임에 참여중인 상태로 사용할 수 없는 명령어 입니다.");
            else {
                if (strings.length == 1 && (toInt(strings[0]) == 1 || toInt(strings[0]) == 2)) {
                    VirtualChest.openChest(p, toInt(strings[0]) == 1);
                }
                else
                    p.sendMessage("§c[ §f! §c] §f/창고 <1/2>");

            }
        }
        return false;
    }

    public int toInt(String val) {
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
}
