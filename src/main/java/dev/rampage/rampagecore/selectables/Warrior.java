package dev.rampage.rampagecore.selectables;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.api.selectable.Selectable;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import dev.rampage.rampagecore.api.utils.ActionBar;
import dev.rampage.rampagecore.api.utils.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Warrior
        extends Selectable {

    final HashMap<UUID, Long> cooldownDash = new HashMap();
    int cooldownTimeDash = 7;
    HashMap<UUID, Long> cooldownShieldBreak = new HashMap();
    HashMap<UUID, Long> cooldownGetPower = new HashMap();
    HashMap<UUID, Long> cooldownImmunity = new HashMap();
    List<PotionEffectType> debuffs = new ArrayList<>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HUNGER, PotionEffectType.LEVITATION, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.UNLUCK, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));
    HashMap<UUID, Long> cooldownBerserkMode = new HashMap();

    public Warrior(RampageCore plugin) { super(plugin); }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        int unlock_lvl = 10;
        final Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
        int lvl = playerInfo.getLvl();
        if (RampageCore.selectables.isSelectedClass(player, "warrior") && player.getGameMode() == GameMode.SURVIVAL && lvl >= unlock_lvl) {
            if (!this.cooldownDash.containsKey(id)) {
                this.cooldownDash.put(id, System.currentTimeMillis() - (long) this.cooldownTimeDash * 1000L);
            }

            if ((int) (this.cooldownDash.get(player.getUniqueId()) / 1000L + (long) this.cooldownTimeDash - System.currentTimeMillis() / 1000L) <= 0) {
                this.cooldownDash.put(player.getUniqueId(), System.currentTimeMillis());
                Location loc = player.getLocation().clone();
                Vector jump = loc.getDirection();
                player.setVelocity(jump.multiply(1));
                player.setAllowFlight(false);
                player.setHealth(Math.min(player.getHealthScale(), player.getHealth() + 2.0));

                new BukkitRunnable() {
                    public void run() {
                        ActionBar.send(player, ChatColor.GREEN + "Рывок перезарядился!");
                        player.setAllowFlight(true);
                    }
                }.runTaskLater(this.plugin, (long) this.cooldownTimeDash * 20L);
            }
            if (player.getGameMode() == GameMode.SURVIVAL) {
                player.setFlying(false);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void shieldBreak(EntityDamageByEntityEvent event) {
        int cooldownTime = 7;
        int unlock_lvl = 20;
        if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() == EntityType.PLAYER) {
            final Player victim = (Player) event.getEntity();
            final Player damager = (Player) event.getDamager();
            UUID id = damager.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(damager.getName());
            int lvl = playerInfo.getLvl();
            if (RampageCore.selectables.isSelectedClass(damager, "warrior") && lvl >= unlock_lvl) {
                if (!this.cooldownShieldBreak.containsKey(id)) {
                    this.cooldownShieldBreak.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                }

                if ((int) (this.cooldownShieldBreak.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                    final ItemStack item = victim.getInventory().getItemInOffHand();
                    if (victim.isBlocking() && item.getType() == Material.SHIELD) {
                        this.cooldownShieldBreak.put(id, System.currentTimeMillis());
                        victim.getInventory().setItemInOffHand(null);
                        new BukkitRunnable() {

                            public void run() {
                                victim.getInventory().setItemInOffHand(item);
                            }
                        }.runTaskLater(this.plugin, 30L);
                        new BukkitRunnable() {

                            public void run() {
                                ActionBar.send(damager, ChatColor.GREEN + "Пробитие перезарядилось!");
                            }
                        }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void powerUp(PlayerInteractEvent event) {
        int cooldownTime = 20;
        int duration = 8;
        int unlock_lvl = 30;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (RampageCore.selectables.isSelectedClass(p, "warrior") && lvl >= unlock_lvl) {
            ItemStack item = p.getInventory().getItemInMainHand();
            ArrayList<Material> swords = new ArrayList<>(Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD));
            if (swords.contains(item.getType()) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (!this.cooldownGetPower.containsKey(id)) {
                    this.cooldownGetPower.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                }

                if ((int) (this.cooldownGetPower.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                    p.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(duration * 20, 0));
                    p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(duration * 20, 0));
                    this.cooldownGetPower.put(id, System.currentTimeMillis());
                    new BukkitRunnable() {

                        public void run() {
                            ActionBar.send(p, ChatColor.GREEN + "Усиление перезарядилось!");
                        }
                    }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
                }
            }
        }
    }

    @EventHandler
    public void immunity(PlayerInteractEvent event) {
        int cooldownTime = 50;
        int duration = 10;
        int unlock_lvl = 40;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (RampageCore.selectables.isSelectedClass(p, "warrior") && lvl >= unlock_lvl && event.getMaterial() == Material.GLOWSTONE_DUST) {
            if (!this.cooldownImmunity.containsKey(id)) {
                this.cooldownImmunity.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if (Cooldown.SecLeft(this.cooldownImmunity.get(id), cooldownTime + duration) <= 0) {
                this.cooldownImmunity.put(id, System.currentTimeMillis());
                p.addPotionEffect(PotionEffectType.GLOWING.createEffect(duration * 20, 0));
                for (int time = 0; time < duration * 20; time += 5) {
                    new BukkitRunnable() {

                        public void run() {
                            for (PotionEffectType a : Warrior.this.debuffs) {
                                p.removePotionEffect(a);
                            }
                        }
                    }.runTaskLater(this.plugin, time);
                }
                new BukkitRunnable() {

                    public void run() {
                        ActionBar.send(p, ChatColor.GREEN + "Иммунитет перезарядился!");
                    }
                }.runTaskLater(this.plugin, (cooldownTime + duration) * 20);
            }
        }
    }

    @EventHandler
    public void berserkMode(EntityDamageEvent event) {
        int cooldownTime = 300;
        int duration = 15;
        int unlock_lvl = 50;
        if (event.getEntity().getType() == EntityType.PLAYER) {
            double afterHP;
            final Player p = (Player) event.getEntity();
            UUID id = p.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
            int lvl = playerInfo.getLvl();
            if (RampageCore.selectables.isSelectedClass(p, "warrior") && lvl >= unlock_lvl && (afterHP = p.getHealth() - event.getFinalDamage()) <= p.getHealthScale() * 0.3 && afterHP > 0.0) {
                if (!this.cooldownBerserkMode.containsKey(id)) {
                    this.cooldownBerserkMode.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                }

                if (Cooldown.SecLeft(this.cooldownBerserkMode.get(id), cooldownTime) <= 0) {
                    this.cooldownBerserkMode.put(id, System.currentTimeMillis());
                    p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(duration * 20, 0));
                    p.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(duration * 20, 0));
                    p.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(duration * 20, 1));
                    new BukkitRunnable() {

                        public void run() {
                            ActionBar.send(p, ChatColor.GREEN + "Режим берсерка перезарядился!");
                        }
                    }.runTaskLater(this.plugin, cooldownTime * 20);
                }
            }
        }
    }
}

