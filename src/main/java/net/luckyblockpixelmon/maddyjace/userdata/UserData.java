package net.luckyblockpixelmon.maddyjace.userdata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.luckyblockpixelmon.maddyjace.luckyblock.LuckyBlockDataLoad;
import net.luckyblockpixelmon.maddyjace.util.Folder;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum UserData implements Listener {
    INSTANCE;

    private Gson gson = new Gson();
    private File userDataFolder;
    private final Map<UUID, Map<String, PlayerField>> userDataMap = new ConcurrentHashMap<>();

    /** 启动时逻辑 */
    public void onEnable() {
        gson = new Gson();
        userDataFolder = Folder.getConfigFolder("userData");
        userDataMap.clear();
    }

    /** 关闭时逻辑 */
    public void onDisable() {
        saveUserData();
        userDataMap.clear();
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
        userDataMap.clear();
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

    private @NotNull Map<String, PlayerField> defaultInitialization() {
        Map<String, PlayerField> boxes = new HashMap<>();
        for (String str : LuckyBlockDataLoad.INSTANCE.getLuckyBlockMap().keySet()) {
            boxes.put(str, new PlayerField(0,0,0,0, 0, 0, 0));
        }
        return boxes;
    }

    /** 保存 Map 回 JSON 文件 */
    public void saveMapAsJson(Map<String, PlayerField> boxes, File jsonFile) throws IOException {
        FileWriter writer = new FileWriter(jsonFile);
        gson.toJson(boxes, writer);
        writer.flush();
        writer.close();
    }

    /** 读取 JSON 文件并返回 Map<String, PlayerField> */
    public Map<String, PlayerField> getMapAsJson(File jsonFile) throws IOException {
        FileReader reader = new FileReader(jsonFile);
        Type type = new TypeToken<Map<String, PlayerField>>() {}.getType();
        Map<String, PlayerField> boxes = gson.fromJson(reader, type);
        saveMapAsJson(boxes, jsonFile);
        reader.close();
        return boxes;
    }

    public Map<String, Map<String, Object>> getMapAsJsonGeneric(File jsonFile) throws IOException {
        FileReader reader = new FileReader(jsonFile);
        Type type = new TypeToken<Map<String, Map<String, Object>>>() {}.getType();
        Map<String, Map<String, Object>> boxes = gson.fromJson(reader, type);
        reader.close();
        return boxes;
    }

    /*
        Map<String, Map<String, Object>> linkedBoxes = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : boxes.entrySet()) {
            Map<String, Object> fieldMap = entry.getValue();
            Map<String, Object> orderedMap = new LinkedHashMap<>();
            orderedMap.put("guaranteedCount", fieldMap.get("guaranteedCount"));
            orderedMap.put("legendaryCount",  fieldMap.get("legendaryCount"));
            orderedMap.put("epicCount",       fieldMap.get("epicCount"));
            orderedMap.put("normalCount",     fieldMap.get("normalCount"));

            linkedBoxes.put(entry.getKey(), orderedMap);
        }
     */

}
