package dev.rampage.rampagecore.utils;

import java.util.UUID;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LP {
    public static void addPermission(UUID id, String permission) {
        RegisteredServiceProvider provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms lp = (LuckPerms)provider.getProvider();
            User user = lp.getUserManager().getUser(id);
            user.data().add(Node.builder(permission).build());
            lp.getUserManager().saveUser(user);
        }
    }

    public static void removePermission(UUID id, String permission) {
        RegisteredServiceProvider provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms lp = (LuckPerms)provider.getProvider();
            User user = lp.getUserManager().getUser(id);
            user.data().remove(Node.builder(permission).build());
            lp.getUserManager().saveUser(user);
        }
    }
}

