package dev.rampage.rampagecore;

import dev.rampage.rampagecore.classes.*;
import dev.rampage.rampagecore.commands.C;
import dev.rampage.rampagecore.commands.CTabCompleter;
import dev.rampage.rampagecore.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RampageCore
        extends JavaPlugin {

    public static Logger logger;

    static {
        logger = (Logger) LogManager.getLogger("rampagecore");
    }

    public void onEnable() {
        new Healer(this);
        new Tank(this);
        new Warrior(this);
        new Archer(this);
        new Assassin(this);
        new GUIClickEvent(this);
        new RestorePotionEffects(this);
        new EnderPearl(this);
        new ExpGaining(this);
        this.getCommand("c").setExecutor(new C());
        Objects.requireNonNull(this.getCommand("c")).setTabCompleter(new CTabCompleter());
        PlayerJumpEvent.register(this);
        logger.info("Rampage Core successfully initialized!");
        logger.info("ChatColor.YELLOW" + "Authors: Ergo, Aviatickets");
    }

    public void onDisable() {
        System.out.println(ChatColor.YELLOW + "ClanWar Classes is disable");
    }
}

