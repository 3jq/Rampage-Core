package dev.rampage.rampagecore.utils;

import dev.rampage.rampagecore.RampageCore;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIClickEvent
        implements Listener {

    public GUIClickEvent(RampageCore plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void guiClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "\u0421\u043f\u043e\u0441\u043e\u0431\u043d\u043e\u0441\u0442\u0438")) {
            event.setCancelled(true);
        }
    }
}

