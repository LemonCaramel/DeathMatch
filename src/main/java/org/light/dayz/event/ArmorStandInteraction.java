package org.light.dayz.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ArmorStandInteraction implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteractArmorStand(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            Player p = event.getPlayer();
            Entity entity = event.getRightClicked();
            if (entity.getCustomName() != null) {
                if (entity.getCustomName().contains("상점"))
                    Bukkit.getServer().dispatchCommand(p, "상점");
                else if (entity.getCustomName().contains("창고"))
                    Bukkit.getServer().dispatchCommand(p, "창고");
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onManipulate(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(!event.getPlayer().isOp());
    }
}
