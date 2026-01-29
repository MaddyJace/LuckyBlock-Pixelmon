package net.luckyblockpixelmon.maddyjace.userdata;

import net.luckyblockpixelmon.maddyjace.util.Get;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerJoinAndQuit implements Listener {

    /** 当玩家进入时 */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(Get.plugin(), () -> {
            UserData.INSTANCE.loadUserData(uuid);
        }); // 异步
    }

    /** 当玩家退出时 */
    @EventHandler public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(Get.plugin(), () -> {
            UserData.INSTANCE.removeUserData(uuid, true);
        }); // 异步
    }

}
