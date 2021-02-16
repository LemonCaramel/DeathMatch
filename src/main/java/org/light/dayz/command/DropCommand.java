package org.light.dayz.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.light.source.Game.GameManager;

public class DropCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() != Material.AIR && !GameManager.getInstance().contains(p.getUniqueId())) {
                Item item = p.getWorld().dropItem(p.getLocation(), p.getInventory().getItemInMainHand());
                item.setVelocity(p.getLocation().getDirection());
                p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
            return true;
        }
        return false;
    }
}
