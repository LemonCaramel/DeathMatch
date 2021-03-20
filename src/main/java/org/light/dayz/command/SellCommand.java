package org.light.dayz.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.game.GameController;
import org.light.dayz.util.Regen;
import org.light.source.Game.GameManager;
import org.light.source.Singleton.CrackShotApi;
import org.light.source.Singleton.EconomyApi;

public class SellCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (GameManager.getInstance().contains(p.getUniqueId()) || GameController.contains(p.getUniqueId()))
                p.sendMessage("§4게임 도중엔 사용할 수 없는 명령어 입니다.");
            else
                openSellGUI(p);
            return true;
        }
        return false;
    }

    public void openSellGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 18, "§0상점");
        inv.setItem(0, Regen.createItemStack(Material.ROTTEN_FLESH, "§c[ §f! §c] §f썩은 고기 판매", (short) 0, " ", " §8-  §f썩은 고기를 1개당 1원에 판매합니다.", " §8-  §7[ §f좌클릭 §7] §f1개 판매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f모두 판매", " §8-  §7[ §f우클릭 §7] §f10개 판매", " "));
        inv.setItem(1, Regen.createItemStack(Material.DIAMOND_SWORD, "§c[ §f! §c] §f총기 판매", (short) 0, " ", " §8-  §f총을 1개 당 썩은 고기 1~5개(스킨의 경우 5~15개)에 판매합니다.", "§c (스킨 취급받는 일반 무기가 존재할 수 있습니다.)", " §8-  §7[ §f좌클릭 §7] §f손에 든 총기 판매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f모두 판매", " "));
        inv.setItem(2, Regen.createItemStack(Material.BREAD, "§c[ §f! §c] §f빵 구매", (short) 0, " ", " §8-  §f빵을 개당 5원에 구매합니다. (인벤토리 비우고 사용하세요)", " §8-  §7[ §f좌클릭 §7] §f빵 1개 구매", " §8-  §7[ §f쉬프트 + 좌클릭 §7] §f빵 64개 구매", " §8-  §7[ §f우클릭 §7] §f빵 10개 구매", " "));
        p.openInventory(inv);
    }


}
