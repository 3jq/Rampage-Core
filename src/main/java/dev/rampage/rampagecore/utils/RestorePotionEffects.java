package dev.rampage.rampagecore.utils;

import java.util.UUID;
import dev.rampage.rampagecore.ClanWarClasses;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RestorePotionEffects
implements Listener {
    final ClanWarClasses plugin;

    public RestorePotionEffects(ClanWarClasses plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    @EventHandler
    public void death(PlayerRespawnEvent event) {
        final Player p = event.getPlayer();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        new BukkitRunnable(){

            public void run() {
                RestorePotionEffects.calculateHP(p);
                RestorePotionEffects.setSpeed(p);
                RestorePotionEffects.setNightVision(p);
            }
        }.runTaskLater((Plugin)this.plugin, 5L);
    }

    @EventHandler
    public void milk(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType() == Material.MILK_BUCKET) {
            final Player player = event.getPlayer();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            String klass = playerInfo.getKlass();
            final int lvl = playerInfo.getLvl();
            if (klass.equals("archer")) {
                new BukkitRunnable(){

                    public void run() {
                        if (lvl >= 30) {
                            player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                        }
                    }
                }.runTaskLater((Plugin)this.plugin, 5L);
            }
            if (klass.equals("assassin")) {
                new BukkitRunnable(){

                    public void run() {
                        player.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                        if (lvl >= 30) {
                            player.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
                        }
                    }
                }.runTaskLater((Plugin)this.plugin, 5L);
            }
            if (klass.equals("tank")) {
                new BukkitRunnable(){

                    public void run() {
                        player.addPotionEffect(PotionEffectType.SLOW.createEffect(6000000, 0));
                    }
                }.runTaskLater((Plugin)this.plugin, 5L);
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        String nickname = p.getName();
        if (JsonUtils.getPlayerInfoName(p.getName()) == null) {
            JsonUtils.createPlayerInfo(nickname, "none", 1, 0);
            System.out.println("Created " + nickname);
        }
        RestorePotionEffects.calculateHP(p);
        RestorePotionEffects.setSpeed(p);
        RestorePotionEffects.setNightVision(p);
    }

    public static void calculateHP(Player p) {
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        String klass = playerInfo.getKlass();
        if (klass.equals("tank")) {
            p.setHealthScale((double)(20 + lvl / 10 * 2));
        } else {
            p.setHealthScale((double)(20 + lvl / 20 * 2));
        }
    }

    public static void setSpeed(Player p) {
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        String klass = playerInfo.getKlass();
        if (klass.equals("archer") && lvl >= 30 || klass.equals("assassin")) {
            p.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
        }
    }

    public static void setNightVision(Player p) {
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        String klass = playerInfo.getKlass();
        if (klass.equals("assassin") && lvl >= 30) {
            p.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
        }
    }
}

