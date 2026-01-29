package net.luckyblockpixelmon.maddyjace;

import net.luckyblockpixelmon.maddyjace.listener.BlockBreak;
import net.luckyblockpixelmon.maddyjace.luckyblock.LuckyBlockDataLoad;
import net.luckyblockpixelmon.maddyjace.userdata.PlayerJoinAndQuit;
import net.luckyblockpixelmon.maddyjace.userdata.UserData;
import net.luckyblockpixelmon.maddyjace.util.Config;
import net.luckyblockpixelmon.maddyjace.util.Get;
import net.luckyblockpixelmon.maddyjace.util.Language;
import net.luckyblockpixelmon.maddyjace.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

public enum LuckyBlockPixelmon {
    INSTANCE; // 单例实例

    private BukkitTask autoSaveTask;

    /** 启动时逻辑 */
    public void onEnable() {
        // 初始化 config
        Config.INSTANCE.onEnable();
        // 初始化 Language
        Language.Get.onEnable();
        // 初始化 luckyBlock
        LuckyBlockDataLoad.INSTANCE.onEnable();
        // 初始化 userData
        UserData.INSTANCE.onEnable();
        // 热加载插件数据
        PlayerUtil.forEachOnlinePlayer(player -> UserData.INSTANCE.loadUserData(player.getUniqueId()));
        // 注册监听器
        registerListeners();
        // 定时保存数据
        scheduledSave();

    }

    /** 关闭时逻辑 */
    public void onDisable() {
        // 关闭定时保存
        if (autoSaveTask != null && !autoSaveTask.isCancelled()) {
            autoSaveTask.cancel();
        }
        // 注销监听器
        unregisterListeners();
        LuckyBlockDataLoad.INSTANCE.onDisable();
        UserData.INSTANCE.onDisable();
    }


    /** 定时保存用户数据 */
    private void scheduledSave() {
        if (Config.INSTANCE.autoSaveEnable) {
            if (autoSaveTask != null && !autoSaveTask.isCancelled()) {
                autoSaveTask.cancel();
            }
            autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                    Get.plugin(), UserData.INSTANCE::saveUserData, 0L, 20 * Config.INSTANCE.autoSavePeriod);
        }
    }

    private PlayerJoinAndQuit playerJoinAndQuit;
    private BlockBreak blockBreak;

    /** 注册监听器 */
    public void registerListeners() {
        playerJoinAndQuit = new PlayerJoinAndQuit();
        Get.plugin().getServer().getPluginManager().registerEvents(playerJoinAndQuit, Get.plugin());
        blockBreak = new BlockBreak();
        Get.plugin().getServer().getPluginManager().registerEvents(blockBreak, Get.plugin());
    }

    /** 注销监听器 */
    public void unregisterListeners() {
        if (playerJoinAndQuit != null) { HandlerList.unregisterAll(playerJoinAndQuit); playerJoinAndQuit = null; }
        if (blockBreak        != null) { HandlerList.unregisterAll(blockBreak);        blockBreak = null; }
    }

}
