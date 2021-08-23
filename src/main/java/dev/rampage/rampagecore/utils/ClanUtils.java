package dev.rampage.rampagecore.utils;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.entity.Player;

public class ClanUtils {
    public static boolean sameClan(Player p1, Player p2) {
        FPlayer fp1 = FPlayers.getInstance().getByPlayer(p1);
        FPlayer fp2 = FPlayers.getInstance().getByPlayer(p2);
        return fp1.hasFaction() && fp1.getFaction().equals(fp2.getFaction());
    }
}

