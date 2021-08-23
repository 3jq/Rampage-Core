package dev.rampage.rampagecore.utils;

import dev.rampage.rampagecore.json.JsonUtils;
import org.bukkit.entity.Player;

public class PEX {
    public static boolean inGroup(Player p, String group) {
        String klass = JsonUtils.getPlayerInfoName(p.getName()).getSelectedClass();
        return group.equals(klass);
    }
}

