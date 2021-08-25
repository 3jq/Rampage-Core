package dev.rampage.rampagecore;

import dev.rampage.rampagecore.api.listeners.ExpGainingListener;
import dev.rampage.rampagecore.api.listeners.MouseClickListener;
import dev.rampage.rampagecore.api.events.PlayerJumpEvent;
import dev.rampage.rampagecore.api.listeners.PotionListener;
import dev.rampage.rampagecore.api.patches.PhasePatch;
import dev.rampage.rampagecore.api.selectable.Selectables;
import dev.rampage.rampagecore.commands.C;
import dev.rampage.rampagecore.commands.CTabCompleter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RampageCore
        extends JavaPlugin {

    public static Logger logger;
    public static Selectables selectables;

    static {
        logger = (Logger) LogManager.getLogger("rampagecore");
    }

    public void onEnable() {
        // Тут, короче, инициализируем все классы.
        selectables = new Selectables(this);

        // Вот эти "new Shit(this)" я потом уберу, потому что они выглядят отвратительно
        new MouseClickListener(this);
        new PotionListener(this);
        new PhasePatch(this);
        new ExpGainingListener(this);
        this.getCommand("c").setExecutor(new C());
        Objects.requireNonNull(this.getCommand("c")).setTabCompleter(new CTabCompleter());
        PlayerJumpEvent.register(this);
        logger.info("Rampage Core successfully initialized!");
        logger.info(ChatColor.YELLOW + "Authors: Ergo, Aviatickets");

        //checking git
    }

    public void onDisable() { logger.info("RampageCore was disabled."); }
}

