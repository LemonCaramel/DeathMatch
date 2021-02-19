package org.light.dayz.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.light.dayz.game.GameController;

public class SuicideCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GameController.contains(p.getUniqueId()))
                p.damage(999.0);
            else
                p.sendMessage("§c[ §f! §c] §fDayZ에서만 사용가능한 커맨드입니다.");
        }
        return false;
    }
}
