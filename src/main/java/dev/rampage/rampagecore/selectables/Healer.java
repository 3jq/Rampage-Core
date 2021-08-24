package dev.rampage.rampagecore.selectables;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.api.selectable.Selectable;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import dev.rampage.rampagecore.api.utils.ActionBar;
import dev.rampage.rampagecore.api.utils.ClanUtils;
import dev.rampage.rampagecore.api.utils.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@Selectable.Manifest(name = "healer")
public class Healer
        extends Selectable {

    List<EntityType> undeads = new ArrayList<EntityType>(Arrays.asList(EntityType.SKELETON, EntityType.SNOWMAN, EntityType.WITHER_SKELETON, EntityType.WITCH, EntityType.ZOMBIE, EntityType.HUSK, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE));
    HashMap<UUID, Long> cooldownMassiveHeal = new HashMap();
    HashMap<UUID, Long> cooldownMassiveRegen = new HashMap();
    HashMap<UUID, Long> cooldownRejection = new HashMap();
    HashMap<UUID, Long> cooldownBattleCry = new HashMap();
    HashMap<UUID, Long> cooldownAlmostDead = new HashMap();

    public Healer(RampageCore plugin) { super(plugin); }

    @EventHandler public void increaseUndeadDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player) event.getDamager();
            if (RampageCore.selectables.isSelectedClass(p, "healer") && this.undeads.contains(event.getEntity().getType())) {
                event.setDamage(event.getDamage() * 1.3);
            }
        }
    }

    @EventHandler public void massiveHeal(PlayerItemConsumeEvent event) {
        int cooldownTime = 12;
        int unlock_lvl = 10;
        if (event.getItem().getType() == Material.POTION) {
            final Player player = event.getPlayer();
            UUID id = player.getUniqueId();
            PotionMeta potionMeta = (PotionMeta) event.getItem().getItemMeta();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            int lvl = playerInfo.getLvl();
            if (potionMeta.getBasePotionData().getType().equals(PotionType.INSTANT_HEAL) && RampageCore.selectables.isSelectedClass(player, "healer")) {
                int r = 10;
                List list = player.getNearbyEntities(r, r, r);
                if (lvl >= unlock_lvl) {
                    if (!this.cooldownMassiveHeal.containsKey(id)) {
                        this.cooldownMassiveHeal.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                    }

                    if ((int) (this.cooldownMassiveHeal.get(player.getUniqueId()) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                        this.cooldownMassiveHeal.put(id, System.currentTimeMillis());
                        for (Entity entity : list) {
                            Player curPlayer;
                            if (entity.getType() != EntityType.PLAYER || !ClanUtils.sameClan(player, curPlayer = (Player) entity))
                                continue;
                            curPlayer.setHealth(Math.min(curPlayer.getHealthScale(), curPlayer.getHealth() + 4.0));
                            ActionBar.send(player, ChatColor.YELLOW + "Вы восстановили 4 здоровья " + curPlayer.getName() + '.');
                        }

                        new BukkitRunnable() {

                            public void run() {
                                ActionBar.send(player, ChatColor.GREEN + "Массовое восстановление перезарядилось!");
                            }
                        }.runTaskLater(this.plugin, cooldownTime * 20);
                    }
                }
            }
        }
    }

    @EventHandler public void massiveRegen(PlayerItemConsumeEvent event) {
        int cooldownTime = 25;
        int unlock_lvl = 10;
        int duration = 10;
        if (event.getItem().getType() == Material.POTION) {
            final Player player = event.getPlayer();
            UUID id = player.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            PotionMeta potionMeta = (PotionMeta) event.getItem().getItemMeta();
            if (potionMeta.getBasePotionData().getType().equals(PotionType.REGEN) && RampageCore.selectables.isSelectedClass(player, "healer")) {
                int lvl = playerInfo.getLvl();
                int r = 10;
                List list = player.getNearbyEntities(r, r, r);
                if (lvl >= unlock_lvl) {
                    if (!this.cooldownMassiveRegen.containsKey(id)) {
                        this.cooldownMassiveRegen.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                    }

                    if ((int) (this.cooldownMassiveRegen.get(player.getUniqueId()) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                        this.cooldownMassiveRegen.put(id, System.currentTimeMillis());
                        for (Entity entity : list) {
                            Player curPlayer;
                            if (entity.getType() != EntityType.PLAYER || !ClanUtils.sameClan(player, curPlayer = (Player) entity))
                                continue;
                            curPlayer.addPotionEffect(PotionEffectType.REGENERATION.createEffect(duration * 20, 0));
                            ActionBar.send(player, ChatColor.YELLOW + "Вы наложили эффект регенерации на игрока " + curPlayer.getName() + '.');
                        }

                        new BukkitRunnable() {

                            public void run() {
                                ActionBar.send(player, ChatColor.GREEN + "Массовая регенерация перезарядилась!");
                            }
                        }.runTaskLater(this.plugin, cooldownTime * 20);
                    }
                }
            }
        }
    }

    @EventHandler public void regainHealth(EntityRegainHealthEvent event) {
        int unlock_lvl = 20;
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.PLAYER) {
            EntityRegainHealthEvent.RegainReason reason;
            Player player = (Player) event.getEntity();
            UUID id = player.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            int lvl = playerInfo.getLvl();
            if (RampageCore.selectables.isSelectedClass(player, "healer") && ((reason = event.getRegainReason()) == EntityRegainHealthEvent.RegainReason.SATIATED || reason == EntityRegainHealthEvent.RegainReason.MAGIC || reason == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN || reason == EntityRegainHealthEvent.RegainReason.EATING) && lvl >= unlock_lvl) {
                event.setAmount(event.getAmount() * 1.2);
            }
        }
    }

    @EventHandler public void rejection(PlayerInteractEvent event) {
        int cooldownTime = 15;
        int r = 5;
        int unlock_lvl = 30;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (RampageCore.selectables.isSelectedClass(p, "healer") && lvl >= unlock_lvl && event.getMaterial() == Material.SUGAR) {
            if (!this.cooldownRejection.containsKey(id)) {
                this.cooldownRejection.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
            }

            if (Cooldown.SecLeft(this.cooldownRejection.get(id), cooldownTime) <= 0) {
                this.cooldownRejection.put(id, System.currentTimeMillis());
                Location pLoc = p.getLocation();
                Vector pVector = pLoc.toVector();
                List list = p.getNearbyEntities(r, r, r);
                for (Entity e : list) {
                    if (e.getType() == EntityType.PLAYER && ClanUtils.sameClan((Player) e, p)) continue;
                    Location eLoc = e.getLocation();
                    Vector eVector = eLoc.toVector();
                    Vector vector = eVector.subtract(pVector);
                    vector.normalize();
                    vector.multiply(new Vector(2.0, 2.4, 2.0));
                    e.setVelocity(vector);
                }
                new BukkitRunnable() {

                    public void run() {
                        ActionBar.send(p, ChatColor.GREEN + "Отталкивание перезарядилось!");
                    }
                }.runTaskLater(this.plugin, cooldownTime * 20);
            }
        }
    }

    @EventHandler public void battleCry(PlayerInteractEvent event) {
        int cooldownTime = 40;
        int duration = 10;
        int r = 10;
        int unlock_lvl = 40;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (RampageCore.selectables.isSelectedClass(p, "healer") && lvl >= unlock_lvl) {
            ArrayList<Material> swords = new ArrayList<Material>(Arrays.asList(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD));
            if (swords.contains(event.getMaterial())) {
                if (!this.cooldownBattleCry.containsKey(id)) {
                    this.cooldownBattleCry.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                }

                if (Cooldown.SecLeft(this.cooldownBattleCry.get(id), cooldownTime) <= 0) {
                    this.cooldownBattleCry.put(id, System.currentTimeMillis());
                    p.addPotionEffect(PotionEffectType.SPEED.createEffect(duration * 20, 0));
                    p.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(duration * 20, 0));
                    p.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(duration * 20, 0));
                    List list = p.getNearbyEntities(r, r, r);
                    for (Entity e : list) {
                        if (e.getType() != EntityType.PLAYER || !ClanUtils.sameClan((Player) e, p)) continue;
                        ((Player) e).addPotionEffect(PotionEffectType.SPEED.createEffect(duration * 20, 0));
                        ((Player) e).addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(duration * 20, 0));
                        ((Player) e).addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(duration * 20, 0));
                    }

                    new BukkitRunnable() {
                        public void run() { ActionBar.send(p, ChatColor.GREEN + "Боевой клич перезарядился!"); }
                    }.runTaskLater(this.plugin, cooldownTime * 20);
                }
            }
        }
    }

    @EventHandler public void almostDead(EntityDamageEvent event) {
        int cooldownTime = 300;
        int unlock_lvl = 50;
        Entity entity = event.getEntity();
        if (event.getEntityType() == EntityType.PLAYER) {
            final Player player = (Player) entity;
            UUID id = player.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            int lvl = playerInfo.getLvl();
            double damage = event.getFinalDamage();
            if (damage >= player.getHealth() && RampageCore.selectables.isSelectedClass(player, "healer") && lvl >= unlock_lvl) {
                if (!this.cooldownAlmostDead.containsKey(player.getUniqueId())) {
                    this.cooldownAlmostDead.put(id, System.currentTimeMillis() - (long) (cooldownTime * 1000));
                }

                if ((int) (this.cooldownAlmostDead.get(player.getUniqueId()) / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L) <= 0) {
                    double maxHealth = player.getHealthScale();
                    player.setHealth(maxHealth / 2.0 + damage);
                    this.cooldownAlmostDead.put(player.getUniqueId(), System.currentTimeMillis());
                    new BukkitRunnable() {

                        public void run() {
                            ActionBar.send(player, ChatColor.GREEN + "Воля к жизни перезарядилась!");
                        }
                    }.runTaskLater(this.plugin, (long) cooldownTime * 20L);
                }
            }
        }
    }
}

