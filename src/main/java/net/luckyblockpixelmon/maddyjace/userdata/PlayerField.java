package net.luckyblockpixelmon.maddyjace.userdata;

import com.google.gson.annotations.SerializedName;

public class PlayerField {

    @SerializedName("legBonus")       private long legBonus;       // 传说保底
    @SerializedName("legBonusCount")  private long legBonusCount;  // 传说保底
    @SerializedName("epicBonus")      private long epicBonus;      // 史诗保底
    @SerializedName("epicBonusCount") private long epicBonusCount; // 史诗保底
    @SerializedName("legendary")      private long legendary;      // 传说
    @SerializedName("epic")           private long epic;           // 史诗
    @SerializedName("normal")         private long normal;         // 普通

    public PlayerField(long legBonus, long epicBonus, long legendary,
                       long epic, long normal, long legBonusCount, long epicBonusCount) {
        this.legBonus = legBonus;
        this.legBonusCount = legBonusCount;
        this.epicBonus = epicBonus;
        this.epicBonusCount = epicBonusCount;
        this.legendary = legendary;
        this.epic = epic;
        this.normal = normal;
    }

    public long getLegBonus() {
        return legBonus;
    }

    public void setLegBonus(long legBonus) {
        this.legBonus = legBonus;
    }

    public long getEpicBonus() {
        return epicBonus;
    }

    public void setEpicBonus(long epicBonus) {
        this.epicBonus = epicBonus;
    }

    public long getLegendary() {
        return legendary;
    }

    public void setLegendary(long legendary) {
        this.legendary = legendary;
    }

    public long getEpic() {
        return epic;
    }

    public void setEpic(long epic) {
        this.epic = epic;
    }

    public long getNormal() {
        return normal;
    }

    public void setNormal(long normal) {
        this.normal = normal;
    }

    public long getLegBonusCount() {
        return legBonusCount;
    }

    public void setLegBonusCount(long legBonusCount) {
        this.legBonusCount = legBonusCount;
    }

    public long getEpicBonusCount() {
        return epicBonusCount;
    }

    @Override
    public String toString() {
        return "PlayerField{" +
                "legBonus=" + legBonus +
                ", epicBonus=" + epicBonus +
                ", legendary=" + legendary +
                ", epic=" + epic +
                ", normal=" + normal +
                ", legBonusCount=" + legBonusCount +
                ", epicBonusCount=" + epicBonusCount +
                '}';
    }
}
