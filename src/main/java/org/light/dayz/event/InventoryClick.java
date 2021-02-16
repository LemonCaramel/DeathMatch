package org.light.dayz.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.light.dayz.util.Regen;
import org.light.source.Singleton.CrackShotApi;

public class InventoryClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (event.getInventory().getTitle().contains("보급품")) {
            event.setCancelled(true);
            if (event.getRawSlot() != -999 && CrackShotApi.getCSID(event.getCurrentItem()) != null) {
                if (p.getInventory().addItem(event.getCurrentItem()).isEmpty()) {
                    p.sendMessage("§c[ §f! §c] §f보급품을 선택하였습니다.");
                    Regen.addPlayer(p.getUniqueId());
                    p.closeInventory();
                }
                else {
                    p.sendMessage("§c[ §f! §c] §f인벤토리가 꽉 차있어 아이템을 지급하지 못했습니다.");
                }
            }
        }
    }
}
