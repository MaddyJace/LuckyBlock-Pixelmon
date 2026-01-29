package net.luckyblockpixelmon.maddyjace.util;

import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class PokemonSpecies {

    public void onEnable() {
        // 创建文件夹
        File folder = new File(Get.plugin().getDataFolder(), "pixelmonList");
        if (!folder.exists()) folder.mkdirs();

        // 所有分类
        Map<String, Set<EnumSpecies>> categories = new LinkedHashMap<>();
        categories.put("全部普通宝可梦[不含神兽和异兽含特皮，特别特殊的特批不包含]", new HashSet<>(PokemonSpecies.getFilteredSpecies()));
        categories.put("全部普通宝可梦[不含神兽含和异兽不含特皮，特别特殊的特批不包含]", new HashSet<>(PokemonSpecies.getFilteredSpecies1()));
        categories.put("全部宝可梦", new HashSet<>(Arrays.asList(EnumSpecies.values())));
        categories.put("传说宝可梦2", new HashSet<>(Arrays.asList(EnumSpecies.LEGENDARY_ENUMS)));
        categories.put("传说宝可梦1", EnumSpecies.legendaries);
        categories.put("超梦异兽", EnumSpecies.ultrabeasts);
        categories.put("在线皮肤", EnumSpecies.onlineTextured);
        categories.put("僵尸皮肤", EnumSpecies.zombieTextured);
        categories.put("淹死皮肤", EnumSpecies.drownedTextured);
        categories.put("情人节皮肤", EnumSpecies.valentineTextured);
        categories.put("彩虹皮肤", EnumSpecies.rainbowTextured);
        categories.put("外星皮肤", EnumSpecies.alienTextured);
        categories.put("另类皮肤", EnumSpecies.alterTextured);
        categories.put("水晶皮肤", EnumSpecies.crystalTextured);
        categories.put("粉色皮肤", EnumSpecies.pinkTextured);
        categories.put("夏日皮肤", EnumSpecies.summerTextured);
        categories.put("瓦伦西亚皮肤", EnumSpecies.valencianTextured);
        categories.put("创作者皮肤", EnumSpecies.creatorTextured);
        categories.put("打击皮肤", EnumSpecies.strikeTextured);
        categories.put("灰烬皮肤", EnumSpecies.ashenTextured);
        categories.put("灵魂皮肤", EnumSpecies.spiritTextured);
        categories.put("万圣节造型", EnumSpecies.halloweenForm);
        categories.put("MF 皮肤", EnumSpecies.mfTextured);
        categories.put("MF 精灵", EnumSpecies.mfSprite);
        categories.put("性别形态", EnumSpecies.genderForm);
        categories.put("不能超级进化", EnumSpecies.cannotDynamax);
        categories.put("非法闪光", EnumSpecies.illegalShinies);
        categories.put("伽勒尔非法闪光", EnumSpecies.illegalShiniesGalarian);
        /*
        categories.put("legendaries", EnumSpecies.legendaries);
        categories.put("ultrabeasts", EnumSpecies.ultrabeasts);
        categories.put("onlineTextured", EnumSpecies.onlineTextured);
        categories.put("zombieTextured", EnumSpecies.zombieTextured);
        categories.put("drownedTextured", EnumSpecies.drownedTextured);
        categories.put("valentineTextured", EnumSpecies.valentineTextured);
        categories.put("rainbowTextured", EnumSpecies.rainbowTextured);
        categories.put("alienTextured", EnumSpecies.alienTextured);
        categories.put("alterTextured", EnumSpecies.alterTextured);
        categories.put("crystalTextured", EnumSpecies.crystalTextured);
        categories.put("pinkTextured", EnumSpecies.pinkTextured);
        categories.put("summerTextured", EnumSpecies.summerTextured);
        categories.put("valencianTextured", EnumSpecies.valencianTextured);
        categories.put("creatorTextured", EnumSpecies.creatorTextured);
        categories.put("strikeTextured", EnumSpecies.strikeTextured);
        categories.put("ashenTextured", EnumSpecies.ashenTextured);
        categories.put("spiritTextured", EnumSpecies.spiritTextured);
        categories.put("halloweenForm", EnumSpecies.halloweenForm);
        categories.put("mfTextured", EnumSpecies.mfTextured);
        categories.put("mfSprite", EnumSpecies.mfSprite);
        categories.put("genderForm", EnumSpecies.genderForm);
        categories.put("cannotDynamax", EnumSpecies.cannotDynamax);
        categories.put("illegalShinies", EnumSpecies.illegalShinies);
        categories.put("illegalShiniesGalarian", EnumSpecies.illegalShiniesGalarian);
         */

        // 遍历每个分类生成单独文件
        for (Map.Entry<String, Set<EnumSpecies>> entry : categories.entrySet()) {
            File file = new File(folder, entry.getKey() + ".yaml");
            YamlConfiguration yaml = new YamlConfiguration();
            List<String> names = entry.getValue().stream().map(EnumSpecies::name).collect(Collectors.toList());
            yaml.set(entry.getKey(), names);
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /** 不含神兽不含特殊皮肤 */
    public static List<EnumSpecies> getFilteredSpecies() {
        return Arrays.stream(EnumSpecies.values())
                .filter(species -> !EnumSpecies.ultrabeasts.contains(species))
                .filter(species -> !EnumSpecies.zombieTextured.contains(species))
                .filter(species -> !EnumSpecies.drownedTextured.contains(species))
                .filter(species -> !EnumSpecies.valentineTextured.contains(species))
                .filter(species -> !EnumSpecies.rainbowTextured.contains(species))
                .filter(species -> !EnumSpecies.alienTextured.contains(species))
                .filter(species -> !EnumSpecies.crystalTextured.contains(species))
                .filter(species -> !EnumSpecies.pinkTextured.contains(species))
                .filter(species -> !EnumSpecies.summerTextured.contains(species))
                .filter(species -> !EnumSpecies.valencianTextured.contains(species))
                .filter(species -> !EnumSpecies.creatorTextured.contains(species))
                .filter(species -> !EnumSpecies.strikeTextured.contains(species))
                .filter(species -> ! EnumSpecies.ashenTextured.contains(species))
                .filter(species -> !EnumSpecies.spiritTextured.contains(species))
                .filter(species -> !EnumSpecies.halloweenForm.contains(species))
                .filter(species -> !EnumSpecies.mfTextured.contains(species))
                .filter(species -> ! EnumSpecies.mfSprite.contains(species))
                .filter(species -> !EnumSpecies.genderForm.contains(species))
                .filter(species -> !EnumSpecies.cannotDynamax.contains(species))
                .collect(Collectors.toList());
    }

    /** 不含神兽 */
    public static List<EnumSpecies> getFilteredSpecies1() {
        return Arrays.stream(EnumSpecies.values())
                .filter(species -> !EnumSpecies.legendaries.contains(species))
                .filter(species -> !EnumSpecies.ultrabeasts.contains(species))
                .collect(Collectors.toList());
    }

}
