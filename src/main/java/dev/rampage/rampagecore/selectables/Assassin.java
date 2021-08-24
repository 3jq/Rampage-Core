package dev.rampage.rampagecore.selectables;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.api.selectable.Selectable;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.api.utils.ActionBar;
import dev.rampage.rampagecore.api.utils.ClanUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Assassin
        extends Selectable {

    HashMap<UUID, Location> map = new HashMap();
    HashMap<UUID, Long> cooldownBurrow = new HashMap();
    HashMap<UUID, Long> antiDoubleInteract = new HashMap();
    HashMap<UUID, Long> cooldownBlindnessArrow = new HashMap();

    public Assassin(RampageCore plugin) { super(plugin); }

    @EventHandler
    public void loner(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() == EntityType.PLAYER) {
            Player p = (Player) event.getDamager();
            double r = 16.0;
            int unlock_lvl = 10;
            if (RampageCore.selectables.isSelectedClass(p, "assassin") && JsonUtils.getPlayerInfoName(p.getName()).getLvl() >= unlock_lvl) {
                List<Entity> list = p.getNearbyEntities(r, r, r);
                int count = 0;

                for (Entity entity : list) {
                    if (entity.getType() != EntityType.PLAYER || ClanUtils.sameClan((Player) entity, p)) continue;
                    ++count;
                }

                if (count <= 1) {
                    event.setDamage(event.getDamage() * 1.3);
                }
            }
        }
    }

    @EventHandler
    public void trauma(EntityDamageByEntityEvent event) {
        int duration = 4;
        int unlock_lvl = 20;
        if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() == EntityType.PLAYER) {
            Player damager = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if (RampageCore.selectables.isSelectedClass(damager, "assassin") && JsonUtils.getPlayerInfoName(damager.getName()).getLvl() >= unlock_lvl && (double) ((int) (Math.random() * (100.0 - 1.0 + 1.0))) <= 20.0) {
                victim.addPotionEffect(PotionEffectType.SLOW.createEffect(duration * 20, 3));
            }
        }
    }

    @EventHandler
    public void burrowOn(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        int cooldownTime = 7;
        int duration = 3600;
        int unlock_lvl = 20;
        if (RampageCore.selectables.isSelectedClass(player, "assassin") && JsonUtils.getPlayerInfoName(player.getName()).getLvl() >= unlock_lvl && event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.getInventory().getItemInMainHand().getType() == Material.WOODEN_SHOVEL) {
            if (!this.cooldownBurrow.containsKey(id)) {
                this.cooldownBurrow.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if (!this.antiDoubleInteract.containsKey(id)) {
                this.antiDoubleInteract.put(id, System.currentTimeMillis() - 100L);
            }

            int secondsLeft = (int) (this.cooldownBurrow.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L);
            int millsAfter = (int) (System.currentTimeMillis() - this.antiDoubleInteract.get(id));
            if (secondsLeft <= 0 && millsAfter >= 200) {
                Block block = event.getClickedBlock();
                Location loc = this.map.get(id);
                Location ploc = player.getLocation();
                loc = block.getLocation();
                loc.setYaw(ploc.getYaw());
                loc.setPitch(ploc.getPitch());
                if (loc.getX() == Math.floor(ploc.getX()) && loc.getY() == (double) Math.round(ploc.getY() - 1.0) && loc.getZ() == Math.floor(ploc.getZ())) {
                    loc.setX(loc.getX() + 0.5);
                    loc.setZ(loc.getZ() + 0.5);
                    player.teleport(loc);
                    player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(duration * 20, 1));
                    event.setCancelled(true);
                    this.map.put(id, loc);
                    this.cooldownBurrow.put(id, System.currentTimeMillis());
                    this.cooldownBurrow.put(id, System.currentTimeMillis());
                    new BukkitRunnable() {

                        public void run() {
                            ActionBar.send(player, ChatColor.GREEN + "Засада перезарядилась!");
                        }
                    }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
                } else {
                    player.sendMessage(ChatColor.RED + "Используйте это умение на блок под собой.");
                    event.setCancelled(true);
                }
                this.antiDoubleInteract.put(id, System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void burrowOff(PlayerMoveEvent event) {
        Location lastLoc;
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        Location loc = player.getLocation();
        if (this.map.containsKey(id) && ((lastLoc = this.map.get(id)).getX() != loc.getX() || lastLoc.getY() != loc.getY() || lastLoc.getZ() != loc.getZ())) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            this.map.remove(id);
        }
    }

    @EventHandler
    public void blindnessArrow(EntityDamageByEntityEvent event) {
        int cooldownTime = 20;
        int duration = 5;
        int unlock_lvl = 50;
        if (event.getEntity().getType().equals(EntityType.PLAYER) && event.getDamager().getType().equals(EntityType.PLAYER)) {
            final Player player = (Player) event.getDamager();
            UUID id = player.getUniqueId();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.TIPPED_ARROW && RampageCore.selectables.isSelectedClass(player, "assassin") && JsonUtils.getPlayerInfoName(player.getName()).getLvl() >= unlock_lvl) {
                RampageCore.logger.info(player.getDisplayName() + " is assassin.");
                if (!this.cooldownBlindnessArrow.containsKey(id)) {
                    this.cooldownBlindnessArrow.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                }

                if ((int) (this.cooldownBlindnessArrow.get(id) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                    PotionMeta meta = (PotionMeta) item.getItemMeta();
                    if (meta.getBasePotionData().getType() == PotionType.WEAKNESS) {
                        Player victim = (Player) event.getEntity();
                        victim.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(duration * 20, 1));
                    }

                    this.cooldownBlindnessArrow.put(id, System.currentTimeMillis());
                    new BukkitRunnable() {

                        public void run() {
                            ActionBar.send(player, ChatColor.GREEN + "Ослепление перезарядилось!");
                        }
                    }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
                }
            }
        }
    }
}

