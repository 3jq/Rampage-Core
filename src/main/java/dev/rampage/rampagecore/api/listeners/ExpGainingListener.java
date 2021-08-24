package dev.rampage.rampagecore.api.listeners;

import dev.rampage.rampagecore.RampageCore;
import dev.rampage.rampagecore.json.JsonUtils;
import dev.rampage.rampagecore.json.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;

public class ExpGainingListener
        implements Listener {

    public static double globalBuster = 1.0;

    public ExpGainingListener(RampageCore plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static int calcNewLvl(int lvl) {
        return (int) (Math.pow(1.13, lvl - 1) * 10.0);
    }

    @EventHandler
    public void mobDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(killer.getName());
            int amount = (int) ((double) event.getDroppedExp() * globalBuster);
            int exp = playerInfo.getExp() + amount;
            int lvl = playerInfo.getLvl();
            int newLvlExp = ExpGainingListener.calcNewLvl(lvl);
            String selectedClass = playerInfo.getSelectedClass();
            while (exp >= newLvlExp) {
                exp -= newLvlExp;
                newLvlExp = ExpGainingListener.calcNewLvl(++lvl);
                killer.sendMessage(ChatColor.YELLOW + "Вы повысили свой уровень до " + lvl + '!');
                if (selectedClass.equalsIgnoreCase("archer") && lvl >= 30) {
                    killer.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                } else if (selectedClass.equalsIgnoreCase("assassin") && lvl >= 30) {
                    killer.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
                }

                if (lvl % 20 == 0) {
                    killer.setHealthScale(killer.getHealthScale() + 2.0);
                    killer.sendMessage(ChatColor.YELLOW + "Ваше здоровье возросло до " + killer.getHealthScale() + '.');
                    continue;
                }

                if (!selectedClass.equalsIgnoreCase("tank") || lvl % 10 != 0) continue;
                killer.setHealthScale(killer.getHealthScale() + 2.0);
                killer.sendMessage(ChatColor.YELLOW + "Ваше здоровье возросло до " + killer.getHealthScale() + '.');
            }

            JsonUtils.createPlayerInfo(killer.getName(), selectedClass, lvl, exp);
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null && killer != event.getEntity().getPlayer()) {
            int exp;
            PlayerInfo playerInfo = JsonUtils.getPlayerInfoName(killer.getName());
            int amount = (int) ((double) Math.min(event.getDroppedExp(), 100) * globalBuster);
            int lvl = playerInfo.getLvl();
            int newLvlExp = ExpGainingListener.calcNewLvl(lvl);
            String selectedClass = playerInfo.getSelectedClass();
            killer.sendMessage("Вы получили " + amount + " опыта за убийство игрока " + event.getEntity().getPlayer().getName());
            for (exp = playerInfo.getExp() + amount; exp >= newLvlExp; exp -= newLvlExp) {
                killer.sendMessage(ChatColor.YELLOW + "Вы повысили свой уровень до " + ++lvl + '!');
                if (lvl % 20 != 0) continue;
                killer.setHealthScale(killer.getHealthScale() + 2.0);
                killer.sendMessage(ChatColor.YELLOW + "Ваше здоровье возросло до " + killer.getHealthScale() + '.');
            }
            JsonUtils.createPlayerInfo(killer.getName(), selectedClass, lvl, exp);
        }
    }
}

