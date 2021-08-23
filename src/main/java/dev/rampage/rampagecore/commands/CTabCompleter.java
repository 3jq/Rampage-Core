package dev.rampage.rampagecore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CTabCompleter
implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> list = new ArrayList<String>(Arrays.asList("choose", "skills", "stats"));
            return list;
        }
        if (args.length == 2 && (args[0].equals("choose") || args[0].equals("skills"))) {
            ArrayList<String> list = new ArrayList<String>(Arrays.asList("archer", "assassin", "healer", "tank", "warrior"));
            return list;
        }
        return null;
    }
}

