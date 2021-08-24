package dev.rampage.rampagecore.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJumpEvent
        extends Event {
    private static final HandlerList handlers = new HandlerList();
    private static final PlayerJumpEventListener listener = new PlayerJumpEventListener();
    private final Player player;

    public PlayerJumpEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static void register(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    private static class PlayerJumpEventListener
            implements Listener {
        private final Map<UUID, Integer> jumps = new HashMap<UUID, Integer>();

        private PlayerJumpEventListener() {
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent e) {
            this.jumps.put(e.getPlayer().getUniqueId(), e.getPlayer().getStatistic(Statistic.JUMP));
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent e) {
            this.jumps.remove(e.getPlayer().getUniqueId());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerMove(PlayerMoveEvent e) {
            Player player = e.getPlayer();
            if (e.getFrom().getY() < e.getTo().getY()) {
                int current = player.getStatistic(Statistic.JUMP);
                int last = this.jumps.getOrDefault(player.getUniqueId(), -1);
                if (last != current) {
                    this.jumps.put(player.getUniqueId(), current);
                    double yDif = (double) ((long) ((e.getTo().getY() - e.getFrom().getY()) * 1000.0)) / 1000.0;
                    if ((yDif < 0.035 || yDif > 0.037) && (yDif < 0.116 || yDif > 0.118)) {
                        Bukkit.getPluginManager().callEvent(new PlayerJumpEvent(player));
                    }
                }
            }
        }
    }
}

