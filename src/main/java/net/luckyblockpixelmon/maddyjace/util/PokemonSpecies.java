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
        categories.put("allPokemon", new HashSet<>(getDexSortedList()));
        categories.put("pokemon", new HashSet<>(getFilteredSpecies()));
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

        // 遍历每个分类生成单独文件
        for (Map.Entry<String, Set<EnumSpecies>> entry : categories.entrySet()) {
            File file = new File(folder, entry.getKey() + ".yaml");
            YamlConfiguration yaml = new YamlConfiguration();
            List<String> names = entry.getValue().stream().map(EnumSpecies::name).collect(Collectors.toList());
            yaml.set("List", names);
            try {
                yaml.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 不含神兽和特殊非法宝可梦 */
    public static List<EnumSpecies> getFilteredSpecies() {
        return Arrays.stream(EnumSpecies.values())
                .filter(species -> !EnumSpecies.ultrabeasts.contains(species))
                .filter(species -> !EnumSpecies.illegalShinies.contains(species))
                .filter(species -> !EnumSpecies.illegalShiniesGalarian.contains(species))
                .collect(Collectors.toList());
    }

    public static List<EnumSpecies> getDexSortedList() {
        return Arrays.stream(EnumSpecies.values())
                .filter(s -> Integer.parseInt(s.getNationalPokedexNumber()) > 0)
                .sorted(Comparator.comparingInt(
                        s -> Integer.parseInt(s.getNationalPokedexNumber())
                ))
                .collect(Collectors.toList());
    }

}
