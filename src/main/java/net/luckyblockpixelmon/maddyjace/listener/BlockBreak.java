package net.luckyblockpixelmon.maddyjace.listener;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.luckyblockpixelmon.maddyjace.api.ForgeWorld;
import net.luckyblockpixelmon.maddyjace.luckyblock.LuckyBlockDataLoad;
import net.luckyblockpixelmon.maddyjace.userdata.UserData;
import net.luckyblockpixelmon.maddyjace.util.Get;
import net.luckyblockpixelmon.maddyjace.util.RunCommands;
import net.luckyblockpixelmon.maddyjace.util.WeightedRandom;
import net.minecraft.world.WorldServer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;


public class BlockBreak implements Listener {

    public static final UserData ud = UserData.INSTANCE;
    public static final LuckyBlockDataLoad lbd = LuckyBlockDataLoad.INSTANCE;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        // 被其他插件拦截了，不算破坏
        if (e.isCancelled()) {
            return;
        }

        Player player = e.getPlayer();                    // 玩家对象
        if (player == null) return;
        String world = e.getBlock().getWorld().getName(); // 世界名称
        String block = e.getBlock().getType().name();     // 方块名称
        Location location = e.getBlock().getLocation();   // 坐标位置

        // 循环查找所有配置(判断方块是否与配置文件匹配)
        for (String config : lbd.getLuckyBlockMap().keySet()) {
            if (block.equalsIgnoreCase(lbd.getLuckyBlockMap().get(config).blockName)) {

                // 传说保底
                if (legBonus(config, player, world, location, e)) return;
                // 史诗保底
                if (epicBonus(config, player, world, location, e)) return;

                // 抽奖
                Map<String, Object> legendary  = lbd.getLuckyBlockMap().get(config).legendary; // 传说
                Map<String, Object> epic       = lbd.getLuckyBlockMap().get(config).epic;      // 史诗
                Map<String, Object> normal     = lbd.getLuckyBlockMap().get(config).normal;    // 普通
                double lp = lbd.getLuckyBlockMap().get(config).legendaryProbability; // 传说概率
                double ep = lbd.getLuckyBlockMap().get(config).epicProbability;      // 史诗概率
                double np = lbd.getLuckyBlockMap().get(config).normalProbability;    // 普通概率
                String wonThePrize = WeightedRandom.startPrizeLottery(lp, ep, np);
                switch (wonThePrize) {
                    // 传说逻辑
                    case "LEGENDARY":
                        if (wonTheLegendary(config, legendary, world, location, player, epic, ep, np, lp, normal, e)) return;
                    // 史诗逻辑
                    case "EPIC":
                        if (wonTheEpic(config, epic, world, location, player, legendary, np, ep, lp, normal, e)) return;
                    // 普通逻辑
                    case "NORMAL":
                        // 玩家没有抽到传说和史诗，就增加的传说和史诗保底次数
                        normal(config, world, location, normal, player, e);
                        return;
                }
            }
        }
    }


    /**
     *  如果玩家抽中了传说奖品就询问传说奖品是否开启，开启就给予传说奖品，<p>
     *    关闭就继续询问史诗奖品是否开启，开启就综合概率(比如:传说3%, 史诗5%, 普通92%。<p>
     *    综合计算: 史诗(5), 普通(传说3+普通92=95))在史诗和普通之间抽奖，抽中史诗就给予史诗奖品否则为普通给予奖品，关闭就给予普通抽奖奖品。
     *
     * @param config    配置文件(这里是遍历所有配置文件中的Kye)
     * @param legendary 传说奖品(配置文件Map的Key)
     * @param world     世界名称(字符串)
     * @param location  坐标位置(BukkitAPI)
     * @param player    玩家对象(BukkitAPI)
     * @param epic      史诗奖品(配置文件Map的Key)
     * @param ep        史诗概率
     * @param np        普通概率
     * @param lp        传说概率
     * @param normal    普通奖品(配置文件Map的Key)
     * @param event     事件对象本身
     * @return true和false
     */
    private boolean wonTheLegendary(
            String config, Map<String, Object> legendary, String world,
            Location location, Player player, Map<String, Object> epic,
            double ep, double np, double lp, Map<String, Object> normal,
            BlockBreakEvent event
    ) {
        /*
         1. 如果玩家抽到了传说就询问传说奖品是否开启?
         2. 传说奖品没有开启，继续询问史诗奖项有没有开启，如果开启就就综合概率(普通加传说的概率)，
            继续史诗和普通之间抽奖，如果是史诗就走史诗抽奖是普通就走普通抽奖。
         3. 抽中了传说，传说奖品没有开启并且史诗奖品也没有开启，就给玩家开启普通抽奖。
         */
        if ((boolean) legendary.get("enable")) {
            legendary(config, world, location, legendary, player, event);
            return true;
        } else if ((boolean) epic.get("enable")) {
            String wtp = WeightedRandom.noLegendaryLottery(ep, (np + lp));
            switch (wtp) {
                // 史诗逻辑
                case "EPIC":  epic(config, world, location, epic, player, event);
                    return true;
                // 普通逻辑
                case "NORMAL": normal(config, world, location, normal, player, event);
                    return true;
            }
        } else {
            normal(config, world, location, normal, player, event);
            return true;
        }
        return false;
    }

    /**
     *  如果玩家抽中了史诗奖品就询问史诗奖品是否开启，开启就给予史诗奖品，<p>
     *    关闭就继续询问传说奖品是否开启，开启就综合概率(比如:传说3%, 史诗5%, 普通92%。<p>
     *    综合计算: 传说(3), 普通(史诗5+普通92=97))在传说和普通之间抽奖，抽中传说就给予传说奖品否则为普通给予奖品，关闭就给予普通抽奖奖品。
     *
     * @param config    配置文件(这里是遍历所有配置文件中的Kye)
     * @param epic      史诗奖品(配置文件Map的Key)
     * @param world     世界名称(字符串)
     * @param location  坐标位置(BukkitAPI)
     * @param player    玩家对象(BukkitAPI)
     * @param legendary 传说奖品(配置文件Map的Key)
     * @param np        普通概率
     * @param ep        史诗概率
     * @param lp        传说概率
     * @param normal    普通奖品(配置文件Map的Key)
     * @param event     事件对象本身
     * @return true和false
     */
    private boolean wonTheEpic(
            String config, Map<String, Object> epic, String world,
            Location location, Player player, Map<String, Object> legendary,
            double np, double ep, double lp, Map<String, Object> normal, BlockBreakEvent event
    ) {
        /*
         1. 如果玩家抽到了史诗就询问史诗奖品是否开启?
         2. 史诗奖品没有开启，继续询问传说奖品有没有开启，如果开启就就综合概率(普通加史诗的概率)，
            继续传说和普通之间抽奖，如果是传说就走传说抽奖是普通就走普通抽奖。
         3. 抽中了史诗，史诗奖品没有开启并且传说奖品也没有开启，就给玩家开启普通抽奖。
         */
        if ((boolean) epic.get("enable")) {
            epic(config, world, location, epic, player, event);
            return true;
        } else if ((boolean) legendary.get("enable")) {
            String wtp = WeightedRandom.noEpicLottery(np, (ep + lp));
            switch (wtp) {
                // 传说逻辑
                case "LEGENDARY":
                    legendary(config, world, location, legendary, player, event);
                    return true;
                // 普通逻辑
                case "NORMAL":
                    normal(config, world, location, normal, player, event);
                    return true;
            }
        } else {
            // 普通抽奖
            normal(config, world, location, normal, player, event);
            return true;
        }
        return false;
    }

    /**
     * 检查传说保底数是否达到指定上限，如果是清空传说保底统计，并退出方法。
     *
     * @param config   配置文件(这里是遍历所有配置文件中的Kye)
     * @param player   玩家对象
     * @param world    世界名称(字符串)
     * @param location 坐标位置(Bukkit API)
     * @param event    事件对象本身
     * @return true和false
     */
    private boolean legBonus(String config, Player player, String world, Location location, BlockBreakEvent event) {
        // 传说保底(触发保底就清空保底记录，并累计吃满保底次数)
        Map<String, Object> legBonus = lbd.getLuckyBlockMap().get(config).legBonus;
        if ((boolean) legBonus.get("enable") && (boolean) legBonus.get("isBonus")) {
            long configCount       = (long) legBonus.get("count");
            long userLegBonus      = ud.userDataMap(player.getUniqueId()).get(config).getLegBonus();
            if (userLegBonus >= configCount) {
                handleLottery(config, world, location, legBonus, player, event);
                // 清空传说保底
                ud.userDataMap(player.getUniqueId()).get(config).setLegBonus(0);
                // 统计吃满传说保底次数
                long userLegBonusCount = ud.userDataMap(player.getUniqueId()).get(config).getLegBonusCount();
                ud.userDataMap(player.getUniqueId()).get(config).setLegBonusCount(userLegBonusCount + 1);
                return true;
            }
        }
        return false;
    }

    /**
     * 检查史诗保底数是否达到指定上限，如果是清空史诗保底统计，并退出方法。
     *
     * @param config   配置文件(这里是遍历所有配置文件中的Kye)
     * @param player   玩家对象(Bukkit API)
     * @param world    世界名称(字符串)
     * @param location 坐标位置(Bukkit API)
     * @param event    事件对象本身
     * @return true和false
     */
    private boolean epicBonus(String config, Player player, String world, Location location, BlockBreakEvent event) {
        // 史诗保底(触发保底就清空保底记录，并累计吃满保底次数)
        Map<String, Object> epicBonus = lbd.getLuckyBlockMap().get(config).epicBonus;
        if ((boolean) epicBonus.get("enable") && (boolean) epicBonus.get("isBonus")) {
            long configCount        = (long) epicBonus.get("count");
            long userEpicBonus      = ud.userDataMap(player.getUniqueId()).get(config).getEpicBonus();
            if (userEpicBonus >= configCount) {
                handleLottery(config, world, location, epicBonus, player, event);
                // 清空史诗保底
                ud.userDataMap(player.getUniqueId()).get(config).setEpicBonus(0);
                // 统计吃满史诗保底次数
                long userEpicBonusCount = ud.userDataMap(player.getUniqueId()).get(config).getEpicBonusCount();
                ud.userDataMap(player.getUniqueId()).get(config).setEpicBonus(userEpicBonusCount + 1);
                return true;
            }
        }
        return false;
    }

    /** 触发史诗抽奖 */
    private void epic(String config, String world, Location location, Map<String, Object> epic, Player player, BlockBreakEvent event) {
        if ((boolean) epic.get("enable")) {
            handleLottery(config, world, location, epic, player, event);

            // 清空史诗保底
            ud.userDataMap(player.getUniqueId()).get(config).setEpicBonus(0);
            // 统计出过史诗次数
            long userEpic = ud.userDataMap(player.getUniqueId()).get(config).getEpic();
            ud.userDataMap(player.getUniqueId()).get(config).setEpic(userEpic + 1);
        }
    }

    /** 触发传说抽奖 */
    private void legendary(String config, String world, Location location, Map<String, Object> legendary, Player player, BlockBreakEvent event) {
        if ((boolean) legendary.get("enable")) {
            handleLottery(config, world, location, legendary, player, event);
            // 清空传说保底
            ud.userDataMap(player.getUniqueId()).get(config).setLegBonus(0);
            // 统计出过传说次数
            long userLegendary = ud.userDataMap(player.getUniqueId()).get(config).getLegendary();
            ud.userDataMap(player.getUniqueId()).get(config).setLegendary(userLegendary + 1);
        }
    }

    /** 触发普通抽奖 */
    private void normal(String config, String world, Location location, Map<String, Object> normal, Player player, BlockBreakEvent event) {
        handleLottery(config, world, location, normal, player, event);
        // 传说
        long userLegBonus = ud.userDataMap(player.getUniqueId()).get(config).getLegBonus();
        ud.userDataMap(player.getUniqueId()).get(config).setLegBonus(userLegBonus + 1);
        // 史诗
        long myUserEpicBonus = ud.userDataMap(player.getUniqueId()).get(config).getEpicBonus();
        ud.userDataMap(player.getUniqueId()).get(config).setEpicBonus(myUserEpicBonus + 1);
        // 普通
        long userNormal = ud.userDataMap(player.getUniqueId()).get(config).getNormal();
        ud.userDataMap(player.getUniqueId()).get(config).setNormal(userNormal + 1);
    }


    /** 命令执行器和随机宝可梦生成器 */
    @SuppressWarnings("unchecked")
    public void handleLottery(String configKye, String world, Location location, Map<String, Object> configMap, Player player, BlockBreakEvent event) {
        // 阻止掉落物
        event.setDropItems(false);

        // 生成精灵(随机一个)
        List<String> pokemon = (List<String>) configMap.get("pokemon");
        pokemonGenerate(configKye, world, location, player.getUniqueId(), pokemon, (boolean) configMap.get("isShiny"));

        // 执行命令
        List<String> commands = (List<String>) configMap.get("commands");
        RunCommands.runCommands(commands, player);
    }

    /**
     * 生成宝可梦
     * @param config    配置文件名称(用于报错日志)
     * @param worldName 世界名称
     * @param location  坐标位置(BukkitAPi)
     * @param uuid      玩家UUID
     * @param pokemon   精灵名称
     * @param isShiny   是否异色
     */
    public void pokemonGenerate(String config, String worldName, Location location, UUID uuid, List<String> pokemon, boolean isShiny) {
        if (pokemon == null || pokemon.isEmpty()) return;
        WorldServer world = ForgeWorld.getWorld(worldName);
        if (world != null) {
            try {
                String randomPokemon = pokemon.get(ThreadLocalRandom.current().nextInt(pokemon.size()));
                String species = EnumSpecies.valueOf(randomPokemon).name;
                PokemonSpec pixelmon = PokemonSpec.from(species);
                if (isShiny) {
                    pixelmon.shiny = true;
                }
                EntityPixelmon entityPixelmon = pixelmon.create(world);
                UserData.generatePixelName.put(uuid, entityPixelmon.getLocalizedName());
                double x = location.getX() + 0.500;
                double y = location.getY() + 0.500;
                double z = location.getZ() + 0.500;
                entityPixelmon.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
                entityPixelmon.onInitialSpawn(world.getDifficultyForLocation(entityPixelmon.getPosition()), null);
                world.spawnEntity(entityPixelmon);
            } catch (IllegalArgumentException e) {
                info("严重错不存在的 " + pokemon + " 宝可梦，请检查 " + config + ".yml 配置！Logger: " + e.getMessage());
            }
        }
    }

    // forEachLuckyBlock(config -> {});
    public static void forEachLuckyBlock(Consumer<String> runnable) {
        lbd.getLuckyBlockMap().keySet().forEach(runnable);
    }

    /** 向控制台发送 */
    private static void info(String message) {
        // Get.plugin().getLogger().info(message);
        Get.plugin().getLogger().severe(message);
    }
}
