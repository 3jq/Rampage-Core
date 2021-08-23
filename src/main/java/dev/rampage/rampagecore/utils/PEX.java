package dev.rampage.rampagecore.utils;

import java.util.UUID;
import dev.rampage.rampagecore.json.JsonUtils;
import org.bukkit.entity.Player;

public class PEX {
    public static boolean inGroup(Player p, String group) {
        UUID id = p.getUniqueId();
        String klass = JsonUtils.getPlayerInfoName(p.getName()).getKlass();
        return group.equals(klass);
    }
}

