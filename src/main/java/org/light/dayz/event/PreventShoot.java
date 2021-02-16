package org.light.dayz.event;

import com.shampaggon.crackshot.events.WeaponPreShootEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.light.dayz.game.GameController;

public class PreventShoot implements Listener {

    @EventHandler
    public void onShoot(WeaponPreShootEvent event) {
        Player p = event.getPlayer();
        if (p.getWorld().getName().contains("lobby") && !p.isOp())
            event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (GameController.contains(event.getPlayer().getUniqueId())) {
            for (ItemStack stack : event.getPlayer().getInventory().getContents())
                if (stack != null && !stack.equals(event.getPlayer().getInventory().getItemInOffHand()))
                    event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), stack);
            event.getPlayer().getInventory().clear();
            GameController.removePlayer(event.getPlayer(), false);
        }
    }
}
