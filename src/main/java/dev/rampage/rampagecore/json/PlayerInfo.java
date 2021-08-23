package dev.rampage.rampagecore.json;

public class PlayerInfo {
    private String nickname;
    private String klass;
    private int lvl;
    private int exp;

    public PlayerInfo(String nickname, String klass, int lvl, int exp) {
        this.nickname = nickname;
        this.klass = klass;
        this.lvl = lvl;
        this.exp = exp;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getKlass() {
        return this.klass;
    }

    public int getExp() {
        return this.exp;
    }

    public int getLvl() {
        return this.lvl;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setKlass(String klass) {
        this.klass = klass;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String toString() {
        return "PlayerInfo{nickname=" + this.nickname + ", klass=" + this.klass + ", lvl=" + this.lvl + ", exp=" + this.exp + '}';
    }
}

