package net.luckyblockpixelmon.maddyjace.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerUtil {

    /** 为在线的玩家执行逻辑 */
    public static void forEachOnlinePlayer(Consumer<Player> action) {
        Bukkit.getScheduler().runTaskAsynchronously(Get.plugin(), () -> {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                return; // 在线玩家数 <= 0，直接跳过
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                action.accept(player);
            }
        });
    }

}
