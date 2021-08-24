package dev.rampage.rampagecore.selectables;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.api.selectable.Selectable;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.api.utils.ActionBar;
import dev.rampage.rampagecore.api.utils.PEX;
import dev.rampage.rampagecore.api.utils.PlayerJumpEvent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

@Selectable.Manifest(name = "archer")
public class Archer
        extends Selectable {

    final HashMap<UUID, Long> cooldownRebound = new HashMap();
    final HashMap<UUID, Long> cooldownRabbitJump = new HashMap();
    final HashMap<UUID, Long> cooldownPoisonArrow = new HashMap();
    HashMap<UUID, Long> cooldownAscent = new HashMap();
    HashMap<UUID, Long> damageAscent = new HashMap();

    public Archer(RampageCore plugin) { super(plugin); }

    @EventHandler public void arrowSpeed(ProjectileLaunchEvent event) {
        Arrow arrow;
        Projectile entity = event.getEntity();
        if (entity.getType() == EntityType.ARROW && (arrow = (Arrow) entity).getShooter() instanceof Player && PEX.inGroup((Player) arrow.getShooter(), "archer")) {
            arrow.setVelocity(arrow.getVelocity().multiply(1.5));
        }
    }

    @EventHandler public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        final Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        int cooldownTime = 7;
        int unlock_lvl = 10;
        if (PEX.inGroup(player, "archer") && player.getGameMode() == GameMode.SURVIVAL && JsonUtils.getPlayerInfoName(player.getName()).getLvl() >= unlock_lvl) {
            if (!this.cooldownRebound.containsKey(id)) {
                this.cooldownRebound.put(player.getUniqueId(), System.currentTimeMillis() - (long) cooldownTime * 1000L);
            }
            if ((int) (this.cooldownRebound.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                this.cooldownRebound.put(player.getUniqueId(), System.currentTimeMillis());
                Location loc = player.getLocation().clone();
                Vector jump = loc.getDirection();
                jump.multiply(new Vector(-0.7, -0.8, -0.7));
                player.setVelocity(jump);
                player.setAllowFlight(false);
                new BukkitRunnable() {

                    public void run() {
                        ActionBar.send(player, ChatColor.GREEN + "Отскок перезарядился!");
                        player.setAllowFlight(true);
                    }
                }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
            }
            player.setFlying(false);
            event.setCancelled(true);
        }
    }

    @EventHandler public void rebound(PlayerJumpEvent event) {
        final Player player = event.getPlayer();
        if ((PEX.inGroup(player, "archer") || PEX.inGroup(player, "warrior")) && player.getGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(true);
            new BukkitRunnable() {
                public void run() {
                    player.setAllowFlight(false);
                }
            }.runTaskLater(this.plugin, 10L);
        }
    }

    @EventHandler public void rabbitJump(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        int cooldownTime = 20;
        int duration = 7;
        int unlock_lvl = 20;
        if (event.getMaterial() == Material.RABBIT_FOOT && PEX.inGroup(player, "archer") && JsonUtils.getPlayerInfoName(player.getName()).getLvl() >= unlock_lvl) {
            if (!this.cooldownRabbitJump.containsKey(id)) {
                this.cooldownRabbitJump.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if ((int) (this.cooldownRabbitJump.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                player.addPotionEffect(PotionEffectType.JUMP.createEffect(duration * 20, 1));
                this.cooldownRabbitJump.put(id, System.currentTimeMillis());
                new BukkitRunnable() {
                    public void run() {
                        ActionBar.send(player, ChatColor.GREEN + "Кроличий прыжок перезарядился!");
                    }
                }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
            }
        }
    }

    @EventHandler public void superArrow(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        int cooldownTime = 15;
        int unlock_lvl = 40;
        if (event.getMaterial() == Material.TIPPED_ARROW && PEX.inGroup(player, "archer") && JsonUtils.getPlayerInfoName(player.getName()).getLvl() >= unlock_lvl && ((PotionMeta) event.getItem().getItemMeta()).getBasePotionData().getType().getEffectType() == PotionEffectType.POISON) {
            if (!this.cooldownPoisonArrow.containsKey(id)) {
                this.cooldownPoisonArrow.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }
            if ((int) (this.cooldownPoisonArrow.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                Location loc = player.getEyeLocation();
                Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setVelocity(loc.getDirection().multiply(4));
                this.cooldownPoisonArrow.put(id, System.currentTimeMillis());
                new BukkitRunnable() {
                    public void run() {
                        ActionBar.send(player, ChatColor.GREEN + "Супер-стрела перезарядилась!");
                    }
                }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
            }
        }
    }

    @EventHandler public void ascent(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        final UUID id = p.getUniqueId();
        int cooldownTime = 30;
        int unlock_lvl = 50;
        if (PEX.inGroup(p, "archer") && JsonUtils.getPlayerInfoName(p.getName()).getLvl() >= unlock_lvl && event.getMaterial() == Material.FEATHER) {
            if (!this.cooldownAscent.containsKey(id)) {
                this.cooldownAscent.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if ((int) (this.cooldownAscent.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                Vector jump = new Vector(0, 3, 0);
                p.setVelocity(jump);
                this.cooldownAscent.put(id, System.currentTimeMillis());
                this.damageAscent.put(id, System.currentTimeMillis());
                new BukkitRunnable() {
                    public void run() {
                        ActionBar.send(p, ChatColor.GREEN + "Взлёт перезарядился!");
                        Archer.this.damageAscent.remove(id);
                    }
                }.runTaskLater(this.plugin, cooldownTime * 20);
            }
        }
    }

    @EventHandler public void fallDamage(EntityDamageEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            Player p = (Player) event.getEntity();
            UUID id = p.getUniqueId();
            int unlock_lvl = 50;
            if (PEX.inGroup(p, "archer") && JsonUtils.getPlayerInfoName(p.getName()).getLvl() >= unlock_lvl && this.damageAscent.containsKey(id) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                this.damageAscent.remove(id);
                event.setCancelled(true);
            }
        }
    }
}

