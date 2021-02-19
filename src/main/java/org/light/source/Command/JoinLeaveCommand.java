package org.light.source.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.light.dayz.game.GameController;
import org.light.source.Game.GameManager;
import org.light.source.Log.MinimizeLogger;
import org.light.source.Phone.PhoneObject;
import org.light.source.Singleton.PhoneManager;

public class JoinLeaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (s.equalsIgnoreCase("참여") || s.equalsIgnoreCase("나가기") || s.equalsIgnoreCase("join") || s.equalsIgnoreCase("leave")) {
                Player p = (Player) commandSender;
                if (PhoneManager.getInstance().contains(p.getUniqueId())) {
                    if (checkPhone(p)) {
                        p.sendMessage( " §c모바일로는 플레이가 불가능합니다.");
                        return true;
                    }
                    switch (s.toLowerCase()) {
                        case "join":
                        case "참여":
                            if (GameManager.getInstance().contains(p.getUniqueId()) || GameController.contains(p.getUniqueId())) {
                                p.sendMessage("§c이미 게임에 참여중입니다.");
                            }
                            else {
                                GameManager.getInstance().addPlayer(p);
                                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치에 참여함");
                            }
                            break;
                        case "leave":
                        case "나가기":
                            if (GameManager.getInstance().contains(p.getUniqueId())) {
                                GameManager.getInstance().removePlayer(p);
                                MinimizeLogger.getInstance().appendLog(p.getName() + "님이 데스매치에서 퇴장함");
                            }
                            else {
                                p.sendMessage("§6게임에 참여하지 않으셨습니다.");
                            }
                            break;
                    }
                }
                else
                    p.sendMessage("§f데이터 로딩중입니다. 잠시만 기다려주세요.");

            }

        }
        return false;
    }

    public boolean checkPhone(Player p) {
        for (PhoneObject object : PhoneManager.getInstance().getPhoneObjects()) {
            if (object.getUuid().equals(p.getUniqueId()))
                return object.getPhoneState();
        }
        return true;
    }
}
