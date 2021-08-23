package dev.rampage.rampagecore.json;

public class PlayerInfo {
    private String nickname;
    private String selectedClass;
    private int lvl;
    private int exp;

    public PlayerInfo(String nickname, String selectedClass, int lvl, int exp) {
        this.nickname = nickname;
        this.selectedClass = selectedClass;
        this.lvl = lvl;
        this.exp = exp;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSelectedClass() {
        return this.selectedClass;
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getLvl() {
        return this.lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public String toString() {
        return "PlayerInfo{nickname=" + this.nickname + ", selectedClass=" + this.selectedClass + ", lvl=" + this.lvl + ", exp=" + this.exp + '}';
    }
}

