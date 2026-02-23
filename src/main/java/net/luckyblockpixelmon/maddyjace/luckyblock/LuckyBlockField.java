package net.luckyblockpixelmon.maddyjace.luckyblock;

import java.util.Map;

public class LuckyBlockField {
    public String blockName;              // 方块名称
    public Map<String, Object> legBonus;  // 传说传说
    public Map<String, Object> epicBonus; // 史诗保底
    public Map<String, Object> legendary; // 传说
    public Map<String, Object> epic;      // 史诗
    public Map<String, Object> normal;    // 普通

    public double legendaryProbability;
    public double epicProbability;
    public double normalProbability;

}
