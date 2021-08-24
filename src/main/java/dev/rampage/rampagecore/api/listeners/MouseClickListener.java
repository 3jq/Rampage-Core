package dev.rampage.rampagecore.api.listeners;

import dev.rampage.rampagecore.RampageCore;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MouseClickListener
        implements Listener {

    public MouseClickListener(RampageCore plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void guiClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Способности")) {
            event.setCancelled(true);
        }
    }
}

