package dev.rampage.rampagecore.api.utils;

public class Cooldown {
    public static int secLeft(Long getId, int cooldownTime) {
        return (int) (getId / 1000L + (long) cooldownTime - System.currentTimeMillis() / 1000L);
    }
}

