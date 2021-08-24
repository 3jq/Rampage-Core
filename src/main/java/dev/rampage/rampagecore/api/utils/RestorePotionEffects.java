package dev.rampage.rampagecore.api.utils;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RestorePotionEffects
        implements Listener {

    final RampageCore plugin;

    public RestorePotionEffects(RampageCore plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static void calculateHP(Player p) {
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        String selectedClass = playerInfo.getSelectedClass();
        if (selectedClass.equalsIgnoreCase("tank")) {
            p.setHealthScale(20 + lvl / 10 * 2);
        } else {
            p.setHealthScale(20 + lvl / 20 * 2);
        }
    }

    public static void setSpeed(Player p) {
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        String selectedClass = playerInfo.getSelectedClass();
        if (selectedClass.equalsIgnoreCase("archer") && lvl >= 30 || selectedClass.equalsIgnoreCase("assassin")) {
            p.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
        }
    }

    public static void setNightVision(Player p) {
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        String selectedClass = playerInfo.getSelectedClass();
        if (selectedClass.equalsIgnoreCase("assassin") && lvl >= 30) {
            p.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
        }
    }

    @EventHandler
    public void death(PlayerRespawnEvent event) {
        final Player p = event.getPlayer();
        new BukkitRunnable() {

            public void run() {
                RestorePotionEffects.calculateHP(p);
                RestorePotionEffects.setSpeed(p);
                RestorePotionEffects.setNightVision(p);
            }
        }.runTaskLater(this.plugin, 5L);
    }

    @EventHandler
    public void milk(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.MILK_BUCKET) {
            final Player player = event.getPlayer();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            String selectedClass = playerInfo.getSelectedClass();
            final int lvl = playerInfo.getLvl();

            if (selectedClass.equalsIgnoreCase("archer")) {
                new BukkitRunnable() {

                    public void run() {
                        if (lvl >= 30) {
                            player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                        }
                    }
                }.runTaskLater(this.plugin, 5L);
            }

            if (selectedClass.equalsIgnoreCase("assassin")) {
                new BukkitRunnable() {

                    public void run() {
                        player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                        if (lvl >= 30) {
                            player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
                        }
                    }
                }.runTaskLater(this.plugin, 5L);
            }

            if (selectedClass.equalsIgnoreCase("tank")) {
                new BukkitRunnable() {
                    public void run() {
                        player.addPotionEffect(PotionEffectType.SLOW.createEffect(6000000, 0));
                    }
                }.runTaskLater(this.plugin, 5L);
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        String nickname = p.getName();
        if (JsonUtils.getPlayerInfoName(nickname) == null) {
            JsonUtils.createPlayerInfo(nickname, "none", 1, 0);
            RampageCore.logger.info("Created player " + nickname);
        }

        RestorePotionEffects.calculateHP(p);
        RestorePotionEffects.setSpeed(p);
        RestorePotionEffects.setNightVision(p);
    }
}

