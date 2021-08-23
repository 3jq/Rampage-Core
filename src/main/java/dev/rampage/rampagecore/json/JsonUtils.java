package dev.rampage.rampagecore.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class JsonUtils {
    public static void createPlayerInfo(String nickname, String klass, int lvl, int exp) {
        Root root = JacksonParser.parse();
        List<PlayerInfo> list = root.getListPlayerInfo();
        if (null != list) {
            list.removeIf(playerInfo -> playerInfo.getNickname().equals(nickname));
            list.add(new PlayerInfo(nickname, klass, lvl, exp));
        }
        root.setListPlayerInfo(list);
        File file = new File(Path.getFileName());
        if (file.delete()) {
            System.out.println("Deleted");
        }
        JsonUtils.createJSON();
        JsonUtils.updateJSON(root);
    }

    public static PlayerInfo getPlayerInfoName(String name) {
        Root root = JacksonParser.parse();
        List<PlayerInfo> list = root.getListPlayerInfo();
        if (null == list) {
            return null;
        }
        for (PlayerInfo playerInfo : list) {
            if (!playerInfo.getNickname().equals(name)) continue;
            return playerInfo;
        }
        return null;
    }

    public static void createJSON() {
        File file = new File(Path.getFileName());
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateJSON(Root root) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        root.setName("playersInfo");
        String newJson = gson.toJson((Object)root);
        String fileName = Path.getFileName();
        FileWriter fw = null;
        try {
            fw = new FileWriter(fileName, true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println(newJson);
        pw.flush();
        pw.close();
        try {
            fw.close();
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

