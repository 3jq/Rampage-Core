package dev.rampage.rampagecore.utils;

import dev.rampage.rampagecore.ClanWarClasses;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class EnderPerl
implements Listener {
    final ClanWarClasses plugin;

    public EnderPerl(ClanWarClasses plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    @EventHandler
    public void throwPerl(PlayerInteractEvent event) {
        ItemStack item1 = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack item2 = event.getPlayer().getInventory().getItemInOffHand();
        if (item1.getType() == Material.AIR && item2.getType() == Material.AIR) {
            return;
        }
        if ((item1.getType() == Material.ENDER_PEARL || item2.getType() == Material.ENDER_PEARL) && event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
            Location loc_block = event.getClickedBlock().getLocation();
            Location loc_p = event.getPlayer().getLocation();
            if (Math.abs(loc_block.getX() - Math.floor(loc_p.getX())) + Math.abs(loc_block.getZ() - Math.floor(loc_p.getZ())) <= 1.0 && Math.abs(loc_block.getY() - (double)Math.round(loc_p.getY() - 1.0)) < 4.0) {
                event.getPlayer().sendMessage((Object)ChatColor.RED + "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043a\u0438\u043d\u0443\u0442\u044c \u0436\u0435\u043c\u0447\u0443\u0433 \u043a\u0440\u0430\u044f \u0432 \u0431\u043b\u043e\u043a \u043d\u0430 \u0442\u0430\u043a\u043e\u043c \u0431\u043b\u0438\u0437\u043a\u043e\u043c \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0438");
                event.setCancelled(true);
            }
        }
    }
}

