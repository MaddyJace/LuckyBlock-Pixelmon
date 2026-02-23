package net.luckyblockpixelmon.maddyjace.util;

import net.luckyblockpixelmon.maddyjace.luckyblock.LuckyBlockDataLoad;
import net.luckyblockpixelmon.maddyjace.userdata.UserData;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// 该类用于监听文件发生变化时
public class AutoReload {

    private final File folder;
    private final long intervalMillis;
    private final String[] suffixFilters;
    private final boolean recursive;

    private FileAlterationMonitor monitor;

    public AutoReload(String folderPath, long intervalMillis, boolean recursive, String... suffixFilters) {
        this.folder = new File(folderPath);
        this.intervalMillis = intervalMillis;
        this.suffixFilters = suffixFilters == null ? new String[0] : suffixFilters;
        this.recursive = recursive;
    }

    // 初始化监听
    public void start() {

        // 文件过滤器（后缀）
        IOFileFilter fileFilter;
        if (suffixFilters.length == 0) {
            fileFilter = TrueFileFilter.TRUE;
        } else {
            List<IOFileFilter> filters = new ArrayList<>();
            for (String suffix : suffixFilters) {
                filters.add(FileFilterUtils.suffixFileFilter(suffix.toLowerCase()));
            }
            fileFilter = FileFilterUtils.or(filters.toArray(new IOFileFilter[0]));
        }

        // 组合过滤器
        IOFileFilter combinedFilter;

        if (recursive) {
            // 允许进入子目录（非隐藏目录）
            IOFileFilter dirFilter = FileFilterUtils.and(
                    FileFilterUtils.directoryFileFilter(),
                    HiddenFileFilter.VISIBLE
            );
            combinedFilter = FileFilterUtils.or(dirFilter, fileFilter);
        } else {
            // 不递归：只监听根目录文件
            combinedFilter = fileFilter;
        }

        // 创建监听
        FileAlterationObserver observer = getFileAlterationObserver(combinedFilter);
        monitor = new FileAlterationMonitor(intervalMillis, observer);
        try {
            monitor.start();
        } catch (Exception e) {
            String pluginName = Language.Get.translate(Language.getServerLanguage(), "pluginName");
            info(Language.Get.translate(Language.getServerLanguage(), "autoReloadError", pluginName));
        }
    }

    // 监听(创建 修改 删除)
    private FileAlterationObserver getFileAlterationObserver(IOFileFilter combinedFilter) {
        FileAlterationObserver observer = new FileAlterationObserver(folder, combinedFilter);
        observer.addListener(new FileAlterationListenerAdaptor() {

            private final LuckyBlockDataLoad lbd = LuckyBlockDataLoad.INSTANCE;

            // 创建
            @Override
            public void onFileCreate(File file) {
                // 检查 file 父级是否为luckyBlock文件夹中的文件
                if (file.getParentFile().getName().equalsIgnoreCase("luckyBlock")) {
                    String noFileExtension = Language.removeSuffix(file.getName());     // 文件名称(无扩展名)
                    YamlConfiguration YAML = YamlConfiguration.loadConfiguration(file); // 配置文件
                    try {
                        // 载入配置文件
                        lbd.getLuckyBlockMap().put(noFileExtension, lbd.parseYAMLData(noFileExtension, YAML));
                        Bukkit.getScheduler().runTask(Get.plugin(), () -> {
                            // 重新载入用户数据
                            PlayerUtil.forEachOnlinePlayer(player -> UserData.INSTANCE.loadUserData(player.getUniqueId()));
                            // 向控制台发送信息
                            String pluginName = Language.Get.translate(Language.getServerLanguage(), "pluginName");
                            info(Language.Get.translate(Language.getServerLanguage(), "autoReloadCreate", pluginName, file.getName()));
                        });
                    } catch (Exception e) {
                        String error = Language.Get.translate(Language.getServerLanguage(),
                                "parseYAMLDataError", e.getMessage());
                        Get.plugin().getLogger().severe(error);
                    }
                }
            }

            // 修改
            @Override
            public void onFileChange(File file) {
                // 检查 file 父级是否为luckyBlock文件夹中的文件
                if (file.getParentFile().getName().equalsIgnoreCase("luckyBlock")) {
                    String noFileExtension = Language.removeSuffix(file.getName());     // 文件名称(无扩展名)
                    YamlConfiguration YAML = YamlConfiguration.loadConfiguration(file); // 配置文件
                    try {
                        // 载入配置文件
                        lbd.getLuckyBlockMap().put(noFileExtension, lbd.parseYAMLData(noFileExtension, YAML));
                        Bukkit.getScheduler().runTask(Get.plugin(), () -> {
                            // 向控制台发送信息
                            String pluginName = Language.Get.translate(Language.getServerLanguage(), "pluginName");
                            info(Language.Get.translate(Language.getServerLanguage(), "autoReloadCreate", pluginName, file.getName()));
                        });
                    } catch (Exception e) {
                        lbd.getLuckyBlockMap().remove(noFileExtension);
                        String error = Language.Get.translate(Language.getServerLanguage(),
                                "parseYAMLDataError", e.getMessage());
                        Get.plugin().getLogger().severe(error);
                    }
                }
            }

            // 删除
            @Override
            public void onFileDelete(File file) {
                // 检查 file 父级是否为luckyBlock文件夹中的文件
                if (file.getParentFile().getName().equalsIgnoreCase("luckyBlock")) {
                    // 文件名称(无扩展名)
                    String noFileExtension = Language.removeSuffix(file.getName());
                    // 卸载配置文件
                    LuckyBlockDataLoad.INSTANCE.getLuckyBlockMap().remove(noFileExtension);
                    Bukkit.getScheduler().runTask(Get.plugin(), () -> {
                        // 重新载入用户数据
                        PlayerUtil.forEachOnlinePlayer(player -> UserData.INSTANCE.loadUserData(player.getUniqueId()));
                        // 向控制台发送信息
                        String pluginName = Language.Get.translate(Language.getServerLanguage(), "pluginName");
                        info(Language.Get.translate(Language.getServerLanguage(), "autoReloadDelete", pluginName, file.getName()));
                    });
                }
            }
        });

        return observer;
    }

    // 关闭监听
    public void stop() {
        if (monitor != null) {
            try {
                monitor.stop();
            } catch (Exception ignored) {}
        }
    }

    /* ------------------------------------------------------------------------------------------------------------- */

    /** 向控制台发送 "创建", "修改", "删除" 文件的信息*/
    private static void info(String fileName) {
        // Get.plugin().getLogger().info(fileName);
        Bukkit.getConsoleSender().sendMessage(fileName);
    }
}
