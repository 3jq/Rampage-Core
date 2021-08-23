package dev.rampage.rampagecore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseTabCompleter
        implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> classes = new ArrayList<String>(Arrays.asList("archer", "assassin", "healer", "tank", "warrior"));
            return classes;
        }
        return null;
    }
}

