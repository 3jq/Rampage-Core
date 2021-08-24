package dev.rampage.rampagecore.api.selectable;

import dev.rampage.rampagecore.RampageCore;
import org.bukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Selectable implements Listener {
    private final String name;
    protected final RampageCore plugin;

    public Selectable(RampageCore plugin) {
        Manifest manifest = getClass().getAnnotation(Manifest.class);
        this.name = manifest.name();
        this.plugin = plugin;

        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public String getName() { return name; }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Manifest {
        String name();
    }
}
