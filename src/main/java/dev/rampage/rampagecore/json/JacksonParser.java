package dev.rampage.rampagecore.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class JacksonParser {
    public static Root parse() {
        JsonFactory factory = new JsonFactory();
        Root root = new Root();
        File file = new File(Path.getFileName());
        if (!file.exists()) {
            return root;
        }
        try {
            ArrayList<PlayerInfo> playerInfoList = new ArrayList<PlayerInfo>();
            JsonParser parser = factory.createJsonParser(file);
            parser.nextToken();
            if (null == parser.getText()) {
                return root;
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String key = parser.getCurrentName();
                if (!key.equals("playersInfo")) continue;
                parser.nextToken();
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    String nickname = null;
                    String selectedClass = null;
                    int lvl = 0;
                    int exp = 0;
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        key = parser.getCurrentName();
                        parser.nextToken();
                        if (key.equals("nickname")) {
                            nickname = parser.getText();
                        }
                        if (key.equals("selectedClass")) {
                            selectedClass = parser.getText();
                        }
                        if (key.equals("lvl")) {
                            lvl = Integer.parseInt(parser.getText());
                        }
                        if (!key.equals("exp")) continue;
                        exp = Integer.parseInt(parser.getText());
                    }
                    PlayerInfo playerInfo = new PlayerInfo(nickname, selectedClass, lvl, exp);
                    playerInfoList.add(playerInfo);
                }
            }
            parser.close();
            root.setListPlayerInfo(playerInfoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}

