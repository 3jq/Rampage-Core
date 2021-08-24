package dev.rampage.rampagecore.classes;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import dev.rampage.rampagecore.utils.ActionBar;
import dev.rampage.rampagecore.utils.ClanUtils;
import dev.rampage.rampagecore.utils.Cooldown;
import dev.rampage.rampagecore.utils.PEX;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Tank
        implements Listener {
    private final RampageCore plugin;
    HashMap<UUID, Long> cooldownResetAndBuff = new HashMap();
    HashMap<UUID, Long> cooldownPoisonedSkin = new HashMap();
    List<UUID> poisonedSkin = new ArrayList<UUID>();
    HashMap<UUID, Long> cooldownAttraction = new HashMap();

    public Tank(RampageCore plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void poisonedSkin(PlayerInteractEvent event) {
        int cooldownTime = 25;
        int duration = 8;
        int unlock_lvl = 30;
        final Player p = event.getPlayer();
        final UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (PEX.inGroup(p, "tank") && lvl >= unlock_lvl && event.getMaterial() == Material.LEATHER) {
            if (!this.cooldownPoisonedSkin.containsKey(id)) {
                this.cooldownPoisonedSkin.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if (Cooldown.SecLeft(this.cooldownPoisonedSkin.get(id), cooldownTime) <= 0) {
                this.cooldownPoisonedSkin.put(id, System.currentTimeMillis());
                this.poisonedSkin.add(id);

                new BukkitRunnable() {

                    public void run() {
                        Tank.this.poisonedSkin.remove(id);
                    }
                }.runTaskLater(this.plugin, duration * 20);

                new BukkitRunnable() {

                    public void run() {
                        ActionBar.send(p, ChatColor.GREEN + "Ядовитая кожа перезарядилась!");
                    }
                }.runTaskLater(this.plugin, (duration + cooldownTime) * 20);
            }
        }
    }

    @EventHandler
    public void poisonedSkinTouch(EntityDamageByEntityEvent event) {
        int duration = 6;
        if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
            Player victim = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            UUID id = victim.getUniqueId();
            if (this.poisonedSkin.contains(id)) {
                damager.addPotionEffect(PotionEffectType.POISON.createEffect(duration * 20, 0));
                damager.addPotionEffect(PotionEffectType.WEAKNESS.createEffect(duration * 20, 0));
            }
        }
    }

    @EventHandler
    public void attraction(PlayerInteractEvent event) {
        int cooldownTime = 20;
        int r = 10;
        int unlock_lvl = 40;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (PEX.inGroup(p, "tank") && lvl >= unlock_lvl && event.getMaterial() == Material.SUGAR) {
            if (!this.cooldownAttraction.containsKey(id)) {
                this.cooldownAttraction.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if (Cooldown.SecLeft(this.cooldownAttraction.get(id), cooldownTime) <= 0) {
                this.cooldownAttraction.put(id, System.currentTimeMillis());
                Location pLoc = p.getLocation();
                Vector pVector = pLoc.toVector();
                List list = p.getNearbyEntities(r, r, r);
                for (Entity e : list) {
                    if (e.getType() == EntityType.PLAYER && ClanUtils.sameClan((Player) e, p)) continue;
                    Location eLoc = e.getLocation();
                    Vector eVector = eLoc.toVector();
                    Vector vector = eVector.subtract(pVector);
                    vector.normalize();
                    vector.multiply(new Vector(-2.0, -1.8, -2.0));
                    e.setVelocity(vector);
                }
                new BukkitRunnable() {

                    public void run() {
                        ActionBar.send(p, ChatColor.GREEN + "Притяжение перезарядилось!");
                    }
                }.runTaskLater(this.plugin, cooldownTime * 20);
            }
        }
    }

    @EventHandler
    public void resetAndBuff(PlayerInteractEvent event) {
        int cooldownTime = 40;
        int duration = 8;
        int unlock_lvl = 50;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (PEX.inGroup(p, "tank") && lvl >= unlock_lvl && p.getInventory().getItemInMainHand().getType() == Material.APPLE && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (!this.cooldownResetAndBuff.containsKey(id)) {
                this.cooldownResetAndBuff.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if ((int) (this.cooldownResetAndBuff.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                this.cooldownResetAndBuff.put(id, System.currentTimeMillis());
                p.removePotionEffect(PotionEffectType.CONFUSION);
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.removePotionEffect(PotionEffectType.WEAKNESS);
                p.removePotionEffect(PotionEffectType.POISON);
                p.removePotionEffect(PotionEffectType.WITHER);
                p.removePotionEffect(PotionEffectType.LEVITATION);
                p.removePotionEffect(PotionEffectType.UNLUCK);
                p.removePotionEffect(PotionEffectType.GLOWING);
                p.addPotionEffect(PotionEffectType.REGENERATION.createEffect(duration * 20, 1));
                p.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(duration * 20, 1));
                p.addPotionEffect(PotionEffectType.SPEED.createEffect(duration * 20, 1));
                p.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(duration * 20, 0));

                new BukkitRunnable() {

                    public void run() {
                        ActionBar.send(p, ChatColor.GREEN + "Очищение перезарядилось!");
                    }
                }.runTaskLater(this.plugin, cooldownTime * 20);
            }
        }
    }
}

