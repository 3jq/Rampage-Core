package dev.rampage.rampagecore;

import java.util.Objects;

import dev.rampage.rampagecore.classes.Assassin;
import dev.rampage.rampagecore.commands.C;
import dev.rampage.rampagecore.commands.CTabCompleter;
import dev.rampage.rampagecore.classes.Archer;
import dev.rampage.rampagecore.classes.Healer;
import dev.rampage.rampagecore.classes.Tank;
import dev.rampage.rampagecore.classes.Warrior;
import dev.rampage.rampagecore.utils.EnderPerl;
import dev.rampage.rampagecore.utils.ExpGaining;
import dev.rampage.rampagecore.utils.GUIClickEvent;
import dev.rampage.rampagecore.utils.PlayerJumpEvent;
import dev.rampage.rampagecore.utils.RestorePotionEffects;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClanWarClasses
extends JavaPlugin {
    public void onEnable() {
        new Healer(this);
        new Tank(this);
        new Warrior(this);
        new Archer(this);
        new Assassin(this);
        new GUIClickEvent(this);
        new RestorePotionEffects(this);
        new EnderPerl(this);
        new ExpGaining(this);
        this.getCommand("c").setExecutor((CommandExecutor)new C());
        Objects.requireNonNull(this.getCommand("c")).setTabCompleter((TabCompleter)new CTabCompleter());
        PlayerJumpEvent.register(this);
        System.out.println((Object)ChatColor.YELLOW + "ClanWarClasses is enable");
        System.out.println((Object)ChatColor.YELLOW + "Author: Ergo");
    }

    public void onDisable() {
        System.out.println((Object)ChatColor.YELLOW + "ClanWar Classes is disable");
    }
}

