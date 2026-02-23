package net.luckyblockpixelmon.maddyjace.util;

import java.io.File;
import java.util.function.BiConsumer;

public class Folder {

    /** 检查 {@code ./plugins/LuckyBlockPixelmon} 目录中是否有XXX文件夹没有就创建并初始化！ */
    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static File getConfigFolder(String folderName) {
        File pluginFolder = Get.plugin().getDataFolder();
        if (!pluginFolder.isDirectory()) {
            throw new IllegalStateException();
        }
        File taskFolder = new File(pluginFolder, folderName);
        if (!taskFolder.exists()) {
            taskFolder.mkdirs();
            if (folderName.equals("luckyBlock")) {
                Get.plugin().saveResource("luckyBlock/娘化方块.yml", false);
                Get.plugin().saveResource("luckyBlock/幸运方块.yml", false);
                Get.plugin().saveResource("luckyBlock/异色方块.yml", false);
                Get.plugin().saveResource("luckyBlock/配置示例.yml", false);
            }
        }
        return taskFolder;
    }

    /**
     * 读取指定文件夹中的全部{@code XXX.YAML}文件，并单独执行逻辑。<p>
     * 注意：该方法只查找根目录文件夹，不会递归查找子文件夹中的{@code .YAML}
     */
    public static void getAllFiles(File folder, BiConsumer<String, File> runnable) {
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.isFile()) continue;
            if (!Language.isYamlFile(file.getName())) continue;

            String noFileExtension = Language.removeSuffix(file.getName());
            if (noFileExtension == null) continue;

            runnable.accept(noFileExtension, file);
        }
    }

}
