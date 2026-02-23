package net.luckyblockpixelmon.maddyjace.userdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.luckyblockpixelmon.maddyjace.luckyblock.LuckyBlockDataLoad;
import net.luckyblockpixelmon.maddyjace.util.Folder;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum UserData {
    INSTANCE;

    private Gson gson = new Gson();
    private File userDataFolder;
    public static final  Map<UUID, String> generatePixelName = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, PlayerField>> userDataMap = new ConcurrentHashMap<>();

    /** 启动时逻辑 */
    public void onEnable() {
        gson = new Gson();
        userDataFolder = Folder.getConfigFolder("userData");
        userDataMap.clear();
        generatePixelName.clear();
    }

    /** 关闭时逻辑 */
    public void onDisable() {
        saveUserData();
        userDataMap.clear();
        generatePixelName.clear();
        if (userDataFolder != null) { userDataFolder = null; }
        if (gson != null) { gson = null; }
    }

    /** 获取 {@code userDataMap} 对象 */
    public Map<String, PlayerField> userDataMap(UUID uuid) {
        return userDataMap.get(uuid);
    }

    /** 保存指定用户数据 */
    public void saveUserData(UUID uuid) {
        File file = new File(userDataFolder, uuid.toString() + ".json");
        Map<String, PlayerField> data = userDataMap.get(uuid);
        try { saveMapAsJson(data, file); } catch (IOException ignored) {}
    }

    /** 保存所有用户数据 */
    public void saveUserData() {
        for (UUID uuid : userDataMap.keySet()) {
            File file = new File(userDataFolder, uuid.toString() + ".json");
            Map<String, PlayerField> data = userDataMap.get(uuid);
            try { saveMapAsJson(data, file); } catch (IOException ignored) {}
        }
    }

    /** 删除指定用户数据 */
    public void removeUserData(UUID uuid, boolean isSave) {
        if (isSave) { saveUserData(uuid); }
        userDataMap.remove(uuid);
    }

    /** 删除所有用户数据 */
    public void removeUserData(boolean isSave) {
        if (isSave) { saveUserData(); }
        userDataMap.clear();
    }

    /** 加载用户数据 */
    public void loadUserData(UUID uuid) {
        try {
            // 用户文件数据目录
            File file = new File(userDataFolder, uuid.toString() + ".json");

            // 用户数据不存在就初始化
            if (!file.exists()) {
                Map<String, PlayerField> boxes = defaultInitialization();
                saveMapAsJson(boxes, file);
                userDataMap.put(uuid, boxes);
                return;
            }

            // 优先保存用户缓存数据
            if (userDataMap.containsKey(uuid)) {
                saveMapAsJson(userDataMap.get(uuid), file);
            }

            /*
              从磁盘中获取用户Json数据，遍历检查用户数据结构是否与getLuckyBlockMap(Key)对应，
              如果发现用户数据结构没有对应的getLuckyBlockMap(Key)就初始化用户的boxes(Key)。
             */
            Map<String, PlayerField> boxes = getMapAsJson(file);
            if (boxes == null || boxes.isEmpty()) {
                boxes = defaultInitialization();
                saveMapAsJson(boxes, file);
                userDataMap.put(uuid, boxes);
                return;
            }
            for (String str : LuckyBlockDataLoad.INSTANCE.getLuckyBlockMap().keySet()) {
                if (!boxes.containsKey(str)) {
                    boxes.put(str, new PlayerField(0,0,0,0, 0, 0, 0));
                }
            }

            // 数据结构优化
            boxes.keySet().removeIf(str -> !LuckyBlockDataLoad.INSTANCE.getLuckyBlockMap().containsKey(str));

            userDataMap.put(uuid, boxes);
            saveMapAsJson(userDataMap.get(uuid), file);
        } catch (IOException ignored) { }
    }

    private Map<String, PlayerField> defaultInitialization() {
        Map<String, PlayerField> boxes = new HashMap<>();
        for (String str : LuckyBlockDataLoad.INSTANCE.getLuckyBlockMap().keySet()) {
            boxes.put(str, new PlayerField(0,0,0,0, 0, 0, 0));
        }
        return boxes;
    }

    /** 保存 Map 回 JSON 文件 */
    public void saveMapAsJson(Map<String, PlayerField> boxes, File jsonFile) throws IOException {
        try (Writer writer = new OutputStreamWriter(
                Files.newOutputStream(jsonFile.toPath()), StandardCharsets.UTF_8)) {
            gson.toJson(boxes, writer);
        }
    }

    /** 读取 JSON 文件并返回 Map<String, PlayerField> */
    public Map<String, PlayerField> getMapAsJson(File jsonFile) throws IOException {

        try (Reader reader = new InputStreamReader(
                Files.newInputStream(jsonFile.toPath()), StandardCharsets.UTF_8)) {

            Type type = new TypeToken<Map<String, PlayerField>>() {}.getType();
            return gson.fromJson(reader, type);
        }
    }

}
