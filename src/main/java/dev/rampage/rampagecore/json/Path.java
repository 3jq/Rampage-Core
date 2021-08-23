package dev.rampage.rampagecore.json;

public class Path {
    public static String getFileName() {
        String path = System.getProperty("user.dir");
        path = path.charAt(0) == '/' ? path + "/plugins/RampageCore/playersInfo.json" : path + "\\plugins\\RampageCore\\playersInfo.json";
        return path;
    }
}

