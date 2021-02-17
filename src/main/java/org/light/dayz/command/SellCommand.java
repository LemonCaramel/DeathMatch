package org.light.dayz.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.game.GameController;
import org.light.source.Game.GameManager;
import org.light.source.Singleton.EconomyApi;

public class SellCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GameManager.getInstance().contains(p.getUniqueId()) || GameController.contains(p.getUniqueId()))
                p.sendMessage("§4게임 도중엔 사용할 수 없는 명령어 입니다.");
            else
                sellZombie(p);
            return true;
        }
        return false;
    }

    public void sellZombie(Player p) {
        int amount = 0;
        Inventory inventory = p.getInventory();
        for (ItemStack stack : inventory.getContents()) {
            if (stack != null && stack.getType() == Material.ROTTEN_FLESH) {
                amount += stack.getAmount();
                inventory.remove(stack);
            }
        }
        EconomyApi.getInstance().giveMoney(p, amount);
        p.sendMessage("§c[ §f! §c] §f썩은 살점 §b" + amount + "§f개를 판매하였습니다. (개당 1원)");
    }
}
