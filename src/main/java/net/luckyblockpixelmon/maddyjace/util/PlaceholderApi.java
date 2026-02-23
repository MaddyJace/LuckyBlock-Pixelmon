package net.luckyblockpixelmon.maddyjace.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.luckyblockpixelmon.maddyjace.listener.BlockBreak;
import net.luckyblockpixelmon.maddyjace.userdata.UserData;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlaceholderApi extends PlaceholderExpansion {


    @Override @SuppressWarnings("all")
    public String getIdentifier() {
        return "lbp";
    }

    @Override @SuppressWarnings("all")
    public String getAuthor() {
        return "MaddyJace";
    }

    @Override @SuppressWarnings("all")
    public String getVersion() {
        return "1.0.0";
    }


    @Override @SuppressWarnings("all")
    public String onPlaceholderRequest(Player player, String identifier) {
        List<String> list = splitByDotIgnoreQuotes(identifier);
        UUID uuid = player.getUniqueId();
        UserData ud = BlockBreak.ud;

        if (list.size() == 1 && UserData.generatePixelName.containsKey(uuid)) {
            if (list.get(0).equalsIgnoreCase("PIXELNAME")) {
                return UserData.generatePixelName.get(uuid);
            } else return "null";
        }

        try {
            if (list.size() == 2 && ud.userDataMap(uuid).containsKey(list.get(0))) {
                switch (list.get(1).toUpperCase()) {
                    case "LEGBONUS":
                        long legBonus = ud.userDataMap(uuid).get(list.get(0)).getLegBonus();
                        return String.valueOf(legBonus);
                    case "LEGBONUSCOUNT":
                        long legBonusCount = ud.userDataMap(uuid).get(list.get(0)).getLegBonusCount();
                        return String.valueOf(legBonusCount);
                    case "EPICBONUS":
                        long epicBonus = ud.userDataMap(uuid).get(list.get(0)).getEpicBonus();
                        return String.valueOf(epicBonus);
                    case "EPICBONUSCOUNT":
                        long epicBonusCount = ud.userDataMap(uuid).get(list.get(0)).getEpicBonusCount();
                        return String.valueOf(epicBonusCount);
                    case "LEGENDARY":
                        long legendary = ud.userDataMap(uuid).get(list.get(0)).getLegendary();
                        return String.valueOf(legendary);
                    case "EPIC":
                        long epic = ud.userDataMap(uuid).get(list.get(0)).getEpic();
                        return String.valueOf(epic);
                    case "NORMAL":
                        long normal = ud.userDataMap(uuid).get(list.get(0)).getNormal();
                        return String.valueOf(normal);
                }
            }
        } catch (Exception e) {
            return "-1";
        }

        return "The parameter you entered does not exist.";
    }


    /**
     * 按点号分割字符串（忽略引号内的点）。
     * <p>
     * 用于解析占位符参数，例如：
     * <pre>
     * 输入: diffDays.second."HH:mm:ss".true
     * 输出: [diffDays, second, HH:mm:ss, true]
     * </pre>
     * </p>
     *
     * @param input 原始字符串
     * @return 分割后的字符串列表（已去除引号）
     */
    public static List<String> splitByDotIgnoreQuotes(String input) {
        String regex = "\\.(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
        List<String> parts = new ArrayList<>();
        for (String part : input.split(regex)) {
            // 分割之后去掉首尾引号
            if (part.startsWith("\"") && part.endsWith("\"") && part.length() >= 2) {
                part = part.substring(1, part.length() - 1);
            }
            parts.add(part);
        }
        return parts;
    }

}
