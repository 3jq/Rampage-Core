package dev.rampage.rampagecore.utils;

public class Cooldown {
    public static int SecLeft(Long get_id, int cooldownTime) {
        return (int) (get_id / 1000L + (long)cooldownTime - System.currentTimeMillis() / 1000L);
    }
}

