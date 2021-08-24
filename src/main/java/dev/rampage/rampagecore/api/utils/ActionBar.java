package dev.rampage.rampagecore.api.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBar {
    public static void send(Player player, String msg) { player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg)); }
}

