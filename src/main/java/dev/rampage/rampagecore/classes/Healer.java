package dev.rampage.rampagecore.classes;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Healer
implements Listener {
    private final ClanWarClasses plugin;
    List<EntityType> undeads = new ArrayList<EntityType>(Arrays.asList(new EntityType[]{EntityType.SKELETON, EntityType.SNOWMAN, EntityType.WITHER_SKELETON, EntityType.WITCH, EntityType.ZOMBIE, EntityType.HUSK, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE}));
    HashMap<UUID, Long> cooldownMassiveHeal = new HashMap();
    HashMap<UUID, Long> cooldownMassiveRegen = new HashMap();
    HashMap<UUID, Long> cooldownRejection = new HashMap();
    HashMap<UUID, Long> cooldownBattleCry = new HashMap();
    HashMap<UUID, Long> cooldownAlmostDead = new HashMap();

    public Healer(ClanWarClasses plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    @EventHandler
    public void increaseUndeadDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            Player p = (Player)event.getDamager();
            UUID id = p.getUniqueId();
            if (PEX.inGroup(p, "healer") && this.undeads.contains((Object)event.getEntity().getType())) {
                event.setDamage(event.getDamage() * 1.3);
            }
        }
    }

    @EventHandler
    public void massiveHeal(PlayerItemConsumeEvent event) {
        int cooldownTime = 12;
        int unlock_lvl = 10;
        if (event.getItem().getType() == Material.POTION) {
            final Player player = event.getPlayer();
            UUID id = player.getUniqueId();
            PotionMeta potionMeta = (PotionMeta)event.getItem().getItemMeta();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            int lvl = playerInfo.getLvl();
            if (potionMeta.getBasePotionData().getType().equals((Object)PotionType.INSTANT_HEAL) && PEX.inGroup(player, "healer")) {
                int r = 10;
                List list = player.getNearbyEntities((double)r, (double)r, (double)r);
                if (lvl >= unlock_lvl) {
                    int secondsLeft;
                    if (!this.cooldownMassiveHeal.containsKey(id)) {
                        this.cooldownMassiveHeal.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
                    }
                    if ((secondsLeft = (int)(this.cooldownMassiveHeal.get(player.getUniqueId()) / 1000L + (long)cooldownTime - System.currentTimeMillis() / 1000L)) <= 0) {
                        this.cooldownMassiveHeal.put(id, System.currentTimeMillis());
                        for (Entity entity : list) {
                            Player curPlayer;
                            if (entity.getType() != EntityType.PLAYER || !ClanUtils.sameClan(player, curPlayer = (Player)entity)) continue;
                            curPlayer.setHealth(Math.min(curPlayer.getHealthScale(), curPlayer.getHealth() + 4.0));
                            ActionBar.send(player, (Object)ChatColor.YELLOW + "\u0412\u044b \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u043b\u0438 4 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f " + curPlayer.getName() + '.');
                        }
                        new BukkitRunnable(){

                            public void run() {
                                ActionBar.send(player, (Object)ChatColor.GREEN + "\u041c\u0430\u0441\u0441\u043e\u0432\u043e\u0435 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0435 \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u043e\u0441\u044c!");
                            }
                        }.runTaskLater((Plugin)this.plugin, (long)(cooldownTime * 20));
                    }
                }
            }
        }
    }

    @EventHandler
    public void massiveRegen(PlayerItemConsumeEvent event) {
        int cooldownTime = 25;
        int unlock_lvl = 10;
        int duration = 10;
        if (event.getItem().getType() == Material.POTION) {
            final Player player = event.getPlayer();
            UUID id = player.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            PotionMeta potionMeta = (PotionMeta)event.getItem().getItemMeta();
            if (potionMeta.getBasePotionData().getType().equals((Object)PotionType.REGEN) && PEX.inGroup(player, "healer")) {
                int lvl = playerInfo.getLvl();
                int r = 10;
                List list = player.getNearbyEntities((double)r, (double)r, (double)r);
                if (lvl >= unlock_lvl) {
                    int secondsLeft;
                    if (!this.cooldownMassiveRegen.containsKey(id)) {
                        this.cooldownMassiveRegen.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
                    }
                    if ((secondsLeft = (int)(this.cooldownMassiveRegen.get(player.getUniqueId()) / 1000L + (long)cooldownTime - System.currentTimeMillis() / 1000L)) <= 0) {
                        this.cooldownMassiveRegen.put(id, System.currentTimeMillis());
                        for (Entity entity : list) {
                            Player curPlayer;
                            if (entity.getType() != EntityType.PLAYER || !ClanUtils.sameClan(player, curPlayer = (Player)entity)) continue;
                            curPlayer.addPotionEffect(PotionEffectType.REGENERATION.createEffect(duration * 20, 0));
                            ActionBar.send(player, (Object)ChatColor.YELLOW + "\u0412\u044b \u043d\u0430\u043b\u043e\u0436\u0438\u043b\u0438 \u044d\u0444\u0444\u0435\u043a\u0442 \u0440\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 " + curPlayer.getName() + '.');
                        }
                        new BukkitRunnable(){

                            public void run() {
                                ActionBar.send(player, (Object)ChatColor.GREEN + "\u041c\u0430\u0441\u0441\u043e\u0432\u0430\u044f \u0440\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u0430\u0441\u044c!");
                            }
                        }.runTaskLater((Plugin)this.plugin, (long)(cooldownTime * 20));
                    }
                }
            }
        }
    }

    @EventHandler
    public void regainHealth(EntityRegainHealthEvent event) {
        int unlock_lvl = 20;
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.PLAYER) {
            EntityRegainHealthEvent.RegainReason reason;
            Player player = (Player)event.getEntity();
            UUID id = player.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            int lvl = playerInfo.getLvl();
            if (PEX.inGroup(player, "healer") && ((reason = event.getRegainReason()) == EntityRegainHealthEvent.RegainReason.SATIATED || reason == EntityRegainHealthEvent.RegainReason.MAGIC || reason == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN || reason == EntityRegainHealthEvent.RegainReason.EATING) && lvl >= unlock_lvl) {
                event.setAmount(event.getAmount() * 1.2);
            }
        }
    }

    @EventHandler
    public void rejection(PlayerInteractEvent event) {
        int cooldownTime = 15;
        int r = 5;
        int unlock_lvl = 30;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (PEX.inGroup(p, "healer") && lvl >= unlock_lvl && event.getMaterial() == Material.SUGAR) {
            int secondsLeft;
            if (!this.cooldownRejection.containsKey(id)) {
                this.cooldownRejection.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
            }
            if ((secondsLeft = Cooldown.SecLeft(this.cooldownRejection.get(id), cooldownTime)) <= 0) {
                this.cooldownRejection.put(id, System.currentTimeMillis());
                Location pLoc = p.getLocation();
                Vector pVector = pLoc.toVector();
                List list = p.getNearbyEntities((double)r, (double)r, (double)r);
                for (Entity e : list) {
                    if (e.getType() == EntityType.PLAYER && ClanUtils.sameClan((Player)e, p)) continue;
                    Location eLoc = e.getLocation();
                    Vector eVector = eLoc.toVector();
                    Vector vector = eVector.subtract(pVector);
                    vector.normalize();
                    vector.multiply(new Vector(2.0, 2.4, 2.0));
                    e.setVelocity(vector);
                }
                new BukkitRunnable(){

                    public void run() {
                        ActionBar.send(p, (Object)ChatColor.GREEN + "\u041e\u0442\u0442\u0430\u043b\u043a\u0438\u0432\u0430\u043d\u0438\u0435 \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u043e\u0441\u044c!");
                    }
                }.runTaskLater((Plugin)this.plugin, (long)(cooldownTime * 20));
            }
        }
    }

    @EventHandler
    public void battleCry(PlayerInteractEvent event) {
        int cooldownTime = 40;
        int duration = 10;
        int r = 10;
        int unlock_lvl = 40;
        final Player p = event.getPlayer();
        UUID id = p.getUniqueId();
        PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(p.getName());
        int lvl = playerInfo.getLvl();
        if (PEX.inGroup(p, "healer") && lvl >= unlock_lvl) {
            ArrayList<Material> swords = new ArrayList<Material>(Arrays.asList(new Material[]{Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD}));
            if (swords.contains((Object)event.getMaterial())) {
                int secondsLeft;
                if (!this.cooldownBattleCry.containsKey(id)) {
                    this.cooldownBattleCry.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
                }
                if ((secondsLeft = Cooldown.SecLeft(this.cooldownBattleCry.get(id), cooldownTime)) <= 0) {
                    this.cooldownBattleCry.put(id, System.currentTimeMillis());
                    p.addPotionEffect(PotionEffectType.SPEED.createEffect(duration * 20, 0));
                    p.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(duration * 20, 0));
                    p.addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(duration * 20, 0));
                    List list = p.getNearbyEntities((double)r, (double)r, (double)r);
                    for (Entity e : list) {
                        if (e.getType() != EntityType.PLAYER || !ClanUtils.sameClan((Player)e, p)) continue;
                        ((Player)e).addPotionEffect(PotionEffectType.SPEED.createEffect(duration * 20, 0));
                        ((Player)e).addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(duration * 20, 0));
                        ((Player)e).addPotionEffect(PotionEffectType.INCREASE_DAMAGE.createEffect(duration * 20, 0));
                    }
                    new BukkitRunnable(){

                        public void run() {
                            ActionBar.send(p, (Object)ChatColor.GREEN + "\u0411\u043e\u0435\u0432\u043e\u0439 \u043a\u043b\u0438\u0447 \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u0441\u044f!");
                        }
                    }.runTaskLater((Plugin)this.plugin, (long)(cooldownTime * 20));
                }
            }
        }
    }

    @EventHandler
    public void almostDead(EntityDamageEvent event) {
        int cooldownTime = 300;
        int unlock_lvl = 50;
        Entity entity = event.getEntity();
        if (event.getEntityType() == EntityType.PLAYER) {
            double curHealth;
            final Player player = (Player)entity;
            UUID id = player.getUniqueId();
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(player.getName());
            int lvl = playerInfo.getLvl();
            double damage = event.getFinalDamage();
            if (damage >= (curHealth = player.getHealth()) && PEX.inGroup(player, "healer") && lvl >= unlock_lvl) {
                int secondsLeft;
                if (!this.cooldownAlmostDead.containsKey(player.getUniqueId())) {
                    this.cooldownAlmostDead.put(id, System.currentTimeMillis() - (long)(cooldownTime * 1000));
                }
                if ((secondsLeft = (int)(this.cooldownAlmostDead.get(player.getUniqueId()) / 1000L + (long)cooldownTime - System.currentTimeMillis() / 1000L)) <= 0) {
                    double maxHealth = player.getHealthScale();
                    player.setHealth(maxHealth / 2.0 + damage);
                    this.cooldownAlmostDead.put(player.getUniqueId(), System.currentTimeMillis());
                    new BukkitRunnable(){

                        public void run() {
                            ActionBar.send(player, (Object)ChatColor.GREEN + "\u0412\u043e\u043b\u044f \u043a \u0436\u0438\u0437\u043d\u0438 \u043f\u0435\u0440\u0435\u0437\u0430\u0440\u044f\u0434\u0438\u043b\u0430\u0441\u044c!");
                        }
                    }.runTaskLater((Plugin)this.plugin, (long)cooldownTime * 20L);
                }
            }
        }
    }
}

