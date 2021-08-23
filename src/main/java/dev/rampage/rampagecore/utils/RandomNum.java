package dev.rampage.rampagecore.utils;

public class RandomNum {
    public static double getRandomIntegerBetweenRange(double min, double max) {
        double x = (double) ((int) (Math.random() * (max - min + 1.0))) + min;
        return x;
    }
}

