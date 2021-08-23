package dev.rampage.rampagecore.utils;

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

public class ExpGaining
        implements Listener {

    public static double global_buster = 1.0;

    public ExpGaining(RampageCore plugin) {
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
            int amount = (int) ((double) event.getDroppedExp() * global_buster);
            int exp = playerInfo.getExp() + amount;
            int lvl = playerInfo.getLvl();
            int new_lvl_exp = ExpGaining.calcNewLvl(lvl);
            String selectedClass = playerInfo.getSelectedClass();
            while (exp >= new_lvl_exp) {
                exp -= new_lvl_exp;
                new_lvl_exp = ExpGaining.calcNewLvl(++lvl);
                killer.sendMessage(ChatColor.YELLOW + "\u0412\u044b \u043f\u043e\u0432\u044b\u0441\u0438\u043b\u0438 \u0441\u0432\u043e\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0434\u043e " + lvl + '!');
                if (selectedClass.equals("archer") && lvl >= 30) {
                    killer.addPotionEffect(PotionEffectType.SPEED.createEffect(6000000, 0));
                } else if (selectedClass.equals("assassin") && lvl >= 30) {
                    killer.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(6000000, 0));
                }

                if (lvl % 20 == 0) {
                    killer.setHealthScale(killer.getHealthScale() + 2.0);
                    killer.sendMessage(ChatColor.YELLOW + "\u0412\u0430\u0448\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0432\u043e\u0437\u0440\u043e\u0441\u043b\u043e \u0434\u043e " + killer.getHealthScale() + '.');
                    continue;
                }

                if (!selectedClass.equals("tank") || lvl % 10 != 0) continue;
                killer.setHealthScale(killer.getHealthScale() + 2.0);
                killer.sendMessage(ChatColor.YELLOW + "\u0412\u0430\u0448\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0432\u043e\u0437\u0440\u043e\u0441\u043b\u043e \u0434\u043e " + killer.getHealthScale() + '.');
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
            int amount = (int) ((double) Math.min(event.getDroppedExp(), 100) * global_buster);
            int lvl = playerInfo.getLvl();
            int new_lvl_exp = ExpGaining.calcNewLvl(lvl);
            String selectedClass = playerInfo.getSelectedClass();
            killer.sendMessage("\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 " + amount + " \u043e\u043f\u044b\u0442\u0430 \u0437\u0430 \u0443\u0431\u0438\u0439\u0441\u0442\u0432\u043e \u0438\u0433\u0440\u043e\u043a\u0430 " + event.getEntity().getPlayer().getName());
            for (exp = playerInfo.getExp() + amount; exp >= new_lvl_exp; exp -= new_lvl_exp) {
                killer.sendMessage(ChatColor.YELLOW + "\u0412\u044b \u043f\u043e\u0432\u044b\u0441\u0438\u043b\u0438 \u0441\u0432\u043e\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0434\u043e " + ++lvl + '!');
                if (lvl % 20 != 0) continue;
                killer.setHealthScale(killer.getHealthScale() + 2.0);
                killer.sendMessage(ChatColor.YELLOW + "\u0412\u0430\u0448\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0432\u043e\u0437\u0440\u043e\u0441\u043b\u043e \u0434\u043e " + killer.getHealthScale() + '.');
            }
            JsonUtils.createPlayerInfo(killer.getName(), selectedClass, lvl, exp);
        }
    }
}

