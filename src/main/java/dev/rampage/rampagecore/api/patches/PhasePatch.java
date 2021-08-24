package dev.rampage.rampagecore.api.patches;

import dev.rampage.rampagecore.RampageCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PhasePatch
        implements Listener {

    final RampageCore plugin;

    public PhasePatch(RampageCore plugin) {
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
            Location block = event.getClickedBlock().getLocation();
            Location playerLocation = event.getPlayer().getLocation();
            if (Math.abs(block.getX() - Math.floor(playerLocation.getX())) + Math.abs(block.getZ() - Math.floor(playerLocation.getZ())) <= 1.0 && Math.abs(block.getY() - (double) Math.round(playerLocation.getY() - 1.0)) < 4.0) {
                event.getPlayer().sendMessage(ChatColor.RED + "Вы не можете кинуть жемчуг края в блок на таком близком расстоянии!");
                event.setCancelled(true);
            }
        }
    }
}

