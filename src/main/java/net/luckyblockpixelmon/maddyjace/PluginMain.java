package net.luckyblockpixelmon.maddyjace;

import net.luckyblockpixelmon.maddyjace.commands.Commands;
import net.luckyblockpixelmon.maddyjace.util.AutoReload;
import net.luckyblockpixelmon.maddyjace.util.Config;
import net.luckyblockpixelmon.maddyjace.util.Get;
import net.luckyblockpixelmon.maddyjace.util.PokemonSpecies;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginMain extends JavaPlugin {

    private static AutoReload autoReload;

	@Override public void onEnable() {

        Get.initialize(this);

        // 注册 命令 和 Tab键 监听器
        Commands commandHandler = new Commands();
        this.getCommand("luckyblockpixelmon").setExecutor(commandHandler);     // 命令
        this.getCommand("luckyblockpixelmon").setTabCompleter(commandHandler); // Tab

        LuckyBlockPixelmon.INSTANCE.onEnable();
        autoReloadStop();
        autoReloadStart();

        if (Config.INSTANCE.generatePokemonList) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                PokemonSpecies pokemonSpecies = new PokemonSpecies();
                pokemonSpecies.onEnable();
            }); // 异步
        }

        // 颜色
        final String RESET = "\u001B[0m";
        final String BLUE = "\u001B[34m";
        final String YELLOW = "\u001B[33m";
        final String GREEN = "\u001B[32m";
        Get.plugin().getLogger().info("\n" + "\n" +
                BLUE + " _                _          _____      _                 \n" +
                BLUE + "| |              | |        |  __ \\    | |               \n" +
                BLUE + "| |    _   _  ___| | ___   _| |__) |__ | | _____          \n" +
                BLUE + "| |   | | | |/ __| |/ / | | |  ___/ _ \\| |/ / _ \\       \n" +
                BLUE + "| |___| |_| | (__|   <| |_| | |  | (_) |   <  __/         \n" +
                BLUE + "|______\\__,_|\\___|_|\\_\\\\__, |_|   \\___/|_|\\_\\___| \n" +
                BLUE + "                        __/ |                             \n" +
                BLUE + "                       |___/                              \n\n" +
                YELLOW + "Our community address\n" +
                RESET + "QQ: https://qm.qq.com/q/HIm1eqh08S\n" +
                GREEN + "Author: MaddyJace  FeedbackEmail: dixiaomai@qq.com  Version: 1.0\n" +
                RESET + "-------------------------------------------------------------------------------------"
        );
    }

    @Override public void onDisable() {
        LuckyBlockPixelmon.INSTANCE.onDisable();
    }

    public static void autoReloadStart() {
        if (Config.INSTANCE.autoReloadEnable) {
            long second = Config.INSTANCE.autoReloadPeriod * 1000L;
            autoReload = new AutoReload(Get.plugin().getDataFolder().getPath(), second, true,".yml", ".yaml");
            autoReload.start();
        }
    }

    public static void autoReloadStop() {
        if (autoReload != null) {
            autoReload.stop();
            autoReload = null;
        }
    }

}
