package dev.rampage.rampagecore.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dev.rampage.rampagecore.ClanWarClasses;
import dev.rampage.rampagecore.utils.ClanUtils;
import dev.rampage.rampagecore.utils.PEX;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import dev.rampage.rampagecore.utils.ActionBar;
import dev.rampage.rampagecore.utils.Cooldown;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Tank
implements Listener {
    private final ClanWarClasses plugin;
    HashMap<UUID, Long> cooldownResetAndBuff = new HashMap();
    HashMap<UUID, Long> cooldownPoisonedSkin = new HashMap();
    List<UUID> poisonedSkin = new ArrayList<UUID>();
    HashMap<UUID, Long> cooldownAttraction = new HashMap();

    public Tank(ClanWarClasses plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
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
            int secondsLeft;
            if (!this.cooldownPoisonedSkin.containsKey(id)) {
                this.cooldownPoisonedSkin.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
            }
            if ((secondsLeft = Cooldown.SecLeft(this.cooldownPoisonedSkin.get(id), cooldownTime)) <= 0) {
                this.cooldownPoisonedSkin.put(id, System.currentTimeMillis());
                this.poisonedSkin.add(id);
                new BukkitRunnable(){

                    public void run() {
                        Tank.this.poisonedSkin.remove(id);
                    }
                }.runTaskLater((Plugin)this.plugin, (long)(duration * 20));
                new BukkitRunnable(){

                    public void run() {
                        ActionBar.send(p, (Object)ChatColor.GREEN + "\u042f\u0434\u043e\u0432\u0438\u0442\u0430\u044f \u043a\u043e\u0436\u0430 \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u0430\u0441\u044c!");
                    }
                }.runTaskLater((Plugin)this.plugin, (long)((duration + cooldownTime) * 20));
            }
        }
    }

    @EventHandler
    public void poisonedSkinTouch(EntityDamageByEntityEvent event) {
        int duration = 6;
        if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
            Player victim = (Player)event.getEntity();
            Player damager = (Player)event.getDamager();
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
            int secondsLeft;
            if (!this.cooldownAttraction.containsKey(id)) {
                this.cooldownAttraction.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
            }
            if ((secondsLeft = Cooldown.SecLeft(this.cooldownAttraction.get(id), cooldownTime)) <= 0) {
                this.cooldownAttraction.put(id, System.currentTimeMillis());
                Location pLoc = p.getLocation();
                Vector pVector = pLoc.toVector();
                List list = p.getNearbyEntities((double)r, (double)r, (double)r);
                for (Entity e : list) {
                    if (e.getType() == EntityType.PLAYER && ClanUtils.sameClan((Player)e, p)) continue;
                    Location eLoc = e.getLocation();
                    Vector eVector = eLoc.toVector();
                    Vector vector = eVector.subtract(pVector);
                    vector.normalize();
                    vector.multiply(new Vector(-2.0, -1.8, -2.0));
                    e.setVelocity(vector);
                }
                new BukkitRunnable(){

                    public void run() {
                        ActionBar.send(p, (Object)ChatColor.GREEN + "\u041f\u0440\u0438\u0442\u044f\u0436\u0435\u043d\u0438\u0435 \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u043e\u0441\u044c!");
                    }
                }.runTaskLater((Plugin)this.plugin, (long)(cooldownTime * 20));
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
        if (PEX.inGroup(p, "tank") && lvl >= unlock_lvl && p.getInventory().getItemInMainHand().getType() == Material.APPLE && event.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
            int secondsLeft;
            if (!this.cooldownResetAndBuff.containsKey(id)) {
                this.cooldownResetAndBuff.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
            }
            if ((secondsLeft = (int)(this.cooldownResetAndBuff.get(id) / 1000L + (long)cooldownTime - System.currentTimeMillis() / 1000L)) <= 0) {
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
                new BukkitRunnable(){

                    public void run() {
                        ActionBar.send(p, (Object)ChatColor.GREEN + "\u041e\u0447\u0438\u0449\u0435\u043d\u0438\u0435 \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u043e\u0441\u044c!");
                    }
                }.runTaskLater((Plugin)this.plugin, (long)(cooldownTime * 20));
            }
        }
    }
}
