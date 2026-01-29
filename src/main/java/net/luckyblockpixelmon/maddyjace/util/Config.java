package net.luckyblockpixelmon.maddyjace.util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Config {
    INSTANCE;

    // language
    public String language = "zh_CN";

    // autoReload
    public boolean autoReloadEnable = true;
    public long autoReloadPeriod = 5;
    // autoSave
    public boolean autoSaveEnable = true;
    public long autoSavePeriod = 5;

    // generatePokemonList
    public boolean generatePokemonList = false;

    public void onEnable() {
        File file = new File(Get.plugin().getDataFolder(), "config.yml");
        if (!file.exists()) {
            Get.plugin().saveResource("config.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // language
        language = config.getString("language");

        // autoReload
        autoReloadEnable = config.getBoolean("autoReload.enable", true);
        autoReloadPeriod = config.getLong("autoReload.period", 5);

        // autoSave
        autoSaveEnable = config.getBoolean("autoSave.enable", true);
        autoSavePeriod = config.getLong("autoSave.period", 5);

        // generatePokemonList
        generatePokemonList = config.getBoolean("generatePokemonList", false);

    }
}
