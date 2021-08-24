package dev.rampage.rampagecore.api.utils;

import dev.rampage.rampagecore.RampageCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EnderPearl
        implements Listener {

    final RampageCore plugin;

    public EnderPearl(RampageCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void throwPerl(PlayerInteractEvent event) {
        ItemStack mainHand = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack offHand = event.getPlayer().getInventory().getItemInOffHand();
        if (mainHand.getType() == Material.AIR && offHand.getType() == Material.AIR) {
            return;
        }

        if ((mainHand.getType() == Material.ENDER_PEARL || offHand.getType() == Material.ENDER_PEARL) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Location loc_block = event.getClickedBlock().getLocation();
            Location loc_p = event.getPlayer().getLocation();
            if (Math.abs(loc_block.getX() - Math.floor(loc_p.getX())) + Math.abs(loc_block.getZ() - Math.floor(loc_p.getZ())) <= 1.0 && Math.abs(loc_block.getY() - (double) Math.round(loc_p.getY() - 1.0)) < 4.0) {
                event.getPlayer().sendMessage(ChatColor.RED + "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043a\u0438\u043d\u0443\u0442\u044c \u0436\u0435\u043c\u0447\u0443\u0433 \u043a\u0440\u0430\u044f \u0432 \u0431\u043b\u043e\u043a \u043d\u0430 \u0442\u0430\u043a\u043e\u043c \u0431\u043b\u0438\u0437\u043a\u043e\u043c \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0438");
                event.setCancelled(true);
            }
        }
    }
}

