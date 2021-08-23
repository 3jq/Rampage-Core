package dev.rampage.rampagecore.json;

import java.util.List;

public class Root {
    private String name;
    private List<PlayerInfo> playersInfo;

    public List<PlayerInfo> getListPlayerInfo() {
        return this.playersInfo;
    }

    public void setListPlayerInfo(List<PlayerInfo> newList) {
        this.playersInfo = newList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "Root{name='" + this.name + '\'' + ", playersInfo=" + this.playersInfo + '}';
    }
}

