package net.luckyblockpixelmon.maddyjace.commands;

import net.luckyblockpixelmon.maddyjace.LuckyBlockPixelmon;
import net.luckyblockpixelmon.maddyjace.PluginMain;
import net.luckyblockpixelmon.maddyjace.util.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            args = new String[] { "help" };
        }

        String pluginName = Language.Get.translate(Language.getServerLanguage(), "pluginName");
        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("reload")) {
            LuckyBlockPixelmon.INSTANCE.onDisable();
            LuckyBlockPixelmon.INSTANCE.onEnable();
            PluginMain.autoReloadStop();
            PluginMain.autoReloadStart();
            sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "reload", pluginName));
            return true;
        }
        sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "commandCorrectUsage", pluginName));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("luckyblockpixelmon.admin")) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return Stream.of("help", "reload")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
