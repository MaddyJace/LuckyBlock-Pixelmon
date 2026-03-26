package net.luckyblockpixelmon.maddyjace.commands;

import net.luckyblockpixelmon.maddyjace.LuckyBlockPixelmon;
import net.luckyblockpixelmon.maddyjace.PluginMain;
import net.luckyblockpixelmon.maddyjace.luckyblock.LuckyBlockDataLoad;
import net.luckyblockpixelmon.maddyjace.util.Language;
import net.luckyblockpixelmon.maddyjace.util.RunCommands;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("luckyblockpixelmon.admin")) return true;

        if(args.length == 0) {
            args = new String[] { "help" };
        }

        String pluginName = Language.Get.translate(Language.getServerLanguage(), "pluginName");
        String subCommand = args[0].toLowerCase();

        // /lbp reload
        if (subCommand.equals("reload") && sender.hasPermission("luckyblockpixelmon.reload")) {
            LuckyBlockPixelmon.INSTANCE.onDisable();
            LuckyBlockPixelmon.INSTANCE.onEnable();
            PluginMain.autoReloadStop();
            PluginMain.autoReloadStart();
            sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "reload", pluginName));
            return true;
        }

        // /lbp give <玩家> <幸运方块> 64 0
        if (args.length >= 5 && subCommand.equals("give") && sender.hasPermission("luckyblockpixelmon.give")) {
            Player player = getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "playersAreOffline", args[1]));
                return true;
            }
            LuckyBlockDataLoad lbd = LuckyBlockDataLoad.INSTANCE;
            if (lbd.getLuckyBlockMap().containsKey(args[2])) {
                String itemId = lbd.getLuckyBlockMap().get(args[2]).blockName;
                Material material = Material.matchMaterial(itemId);
                if (material == null) {
                    sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "itemDoesNotExist", args[2], itemId));
                    return true;
                }

                int itemNumber;
                try {
                    itemNumber = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "itemStackError", args[2], itemId));
                    return true;
                }
                short dataValue;
                try {
                    dataValue = Short.parseShort(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "itemDataError", args[2], itemId));
                    return true;
                }

                ItemStack item = new ItemStack(material, itemNumber, dataValue);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    // 物品名称
                    String itemName = lbd.getLuckyBlockMap().get(args[2]).itemName;
                    if (itemName != null) {
                        itemName = RunCommands.parse(player, itemName.replace("&","§"));
                        meta.setDisplayName(itemName);
                    }
                    // 物品描述
                    List<String> itemLore = lbd.getLuckyBlockMap().get(args[2]).itemLore;
                    if (itemLore != null) {
                        List<String> coloredLore = itemLore.stream()
                                .map(line -> RunCommands.parse(player, ChatColor.translateAlternateColorCodes('&', line)))
                                .collect(Collectors.toList());
                        meta.setLore(coloredLore);
                    }
                    item.setItemMeta(meta);
                }
                player.getInventory().addItem(item);
                String getMessage = lbd.getLuckyBlockMap().get(args[2]).getMessage;
                if (getMessage != null) {
                    getMessage = getMessage.replace("&","§");
                    getMessage = RunCommands.parse(player, getMessage);
                    getMessage = getMessage.replaceAll("(?i)\\{stack}", String.valueOf(itemNumber));
                    player.sendMessage(getMessage);
                }
            } else {
                sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "itemDoesNotExist", args[2]));
            }
            return true;
        }

        sender.sendMessage(Language.Get.translate(Language.getServerLanguage(), "noConfigExists", pluginName));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("luckyblockpixelmon.admin")) {
            return Collections.emptyList();
        }

        LuckyBlockDataLoad lbd = LuckyBlockDataLoad.INSTANCE;

        if (args.length == 1) {
            return Stream.of("help", "give", "reload")
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args[0].equalsIgnoreCase("give") && sender.hasPermission("luckyblockpixelmon.give")) {
            switch (args.length) {
                case 2: // /lbp give <玩家>
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .sorted()
                            .collect(Collectors.toList());
                case 3: // /lbp give <玩家> <幸运方块ID>
                    return lbd.getLuckyBlockMap().keySet().stream()
                            .filter(id -> id.toLowerCase().startsWith(args[2].toLowerCase()))
                            .sorted()
                            .collect(Collectors.toList());

                case 4: // /lbp give <玩家> <ID> [数量]
                    return Stream.of("1", "16", "32", "64")
                            .filter(num -> num.startsWith(args[3]))
                            .collect(Collectors.toList());

                case 5: // /lbp give <玩家> <ID> <数量> [数据值]
                    return Collections.singletonList("0"); // 通常默认为 0
            }
        }

        return Collections.emptyList();
    }

    // 获取在线玩家
    public static Player getPlayer(String playerName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerName.equalsIgnoreCase(player.getName())) {
                return player;
            }
        }
        return null;
    }

}
