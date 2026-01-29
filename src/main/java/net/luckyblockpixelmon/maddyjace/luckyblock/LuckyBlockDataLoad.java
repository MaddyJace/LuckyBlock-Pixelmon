package net.luckyblockpixelmon.maddyjace.luckyblock;

import net.luckyblockpixelmon.maddyjace.util.Folder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public enum LuckyBlockDataLoad {
    INSTANCE;

    private File luckyBlockFolder;
    private final Map<String, LuckyBlockField> luckyBlockMap = new LinkedHashMap<>();

    /** 启动时逻辑 */
    public void onEnable() {
        luckyBlockFolder = Folder.getConfigFolder("luckyBlock");
        luckyBlockMap.clear();
        Folder.getAllFiles(luckyBlockFolder,(noExtension, file) -> {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            luckyBlockMap.put(noExtension, parseYAMLData(noExtension, yaml));
        });
    }

    /** 关闭时逻辑 */
    public void onDisable() {
        luckyBlockMap.clear();
        if (luckyBlockFolder != null) { luckyBlockFolder = null; }
    }

    /** 获取 {@code luckyBlockMap} 对象 */
    public Map<String, LuckyBlockField> getLuckyBlockMap() {
        return luckyBlockMap;
    }

    /** 解析配置文件 */
    public LuckyBlockField parseYAMLData(String noFileExtension, ConfigurationSection YAML) {
        LuckyBlockField lbf = new LuckyBlockField();

        String lb = "luckyBlock.";

        lbf.blockName  = YAML.getString(lb + "block", null); // 方块
        lbf.legBonus = new LinkedHashMap<String, Object>() {{
            put("isBonus"  , true);
            put("enable"  , YAML.getBoolean(lb    + "legBonus.enable", false));
            put("count"   , YAML.getLong(lb       + "legBonus.count"));
            put("commands", YAML.getStringList(lb + "legBonus.commands"));
            put("pokemon" , YAML.getStringList(lb + "legBonus.pokemon"));
        }}; // 传说保底
        lbf.epicBonus = new LinkedHashMap<String, Object>() {{
            put("isBonus"  , true);
            put("enable"  , YAML.getBoolean(lb    + "epicBonus.enable", false));
            put("count"   , YAML.getLong(lb       + "epicBonus.count"));
            put("commands", YAML.getStringList(lb + "epicBonus.commands"));
            put("pokemon" , YAML.getStringList(lb + "epicBonus.pokemon"));
        }}; // 史诗保底
        lbf.legendary = new LinkedHashMap<String, Object>() {{
            put("isBonus"  , false);
            put("enable"      , YAML.getBoolean(lb    + "legendary.enable", false));
            put("probability" , YAML.getDouble(lb     + "legendary.probability"));
            put("commands"    , YAML.getStringList(lb + "legendary.commands"));
            put("pokemon"     , YAML.getStringList(lb + "legendary.pokemon"));
        }}; // 传说
        lbf.epic = new LinkedHashMap<String, Object>() {{
            put("isBonus"  , false);
            put("enable"      , YAML.getBoolean(lb    + "epic.enable", false));
            put("probability" , YAML.getDouble(lb     + "epic.probability"));
            put("commands"    , YAML.getStringList(lb + "epic.commands"));
            put("pokemon"     , YAML.getStringList(lb + "epic.pokemon"));
        }}; // 史诗
        lbf.normal = new LinkedHashMap<String, Object>() {{
            put("isBonus"  , false);
            put("enable"      , YAML.getBoolean(lb    + "normal.enable", false));
            put("probability" , YAML.getDouble(lb     + "normal.probability"));
            put("commands"    , YAML.getStringList(lb + "normal.commands"));
            put("pokemon"     , YAML.getStringList(lb + "normal.pokemon"));
        }}; // 普通

        return lbf;
    }

}
