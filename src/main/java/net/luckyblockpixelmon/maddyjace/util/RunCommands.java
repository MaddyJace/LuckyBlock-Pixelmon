package net.luckyblockpixelmon.maddyjace.util;

// import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunCommands {

    /** 执行 List 中的所有命令 */
    public static void runCommands(List<String> str, Player player) {
        if ( str == null || str.isEmpty() || player == null ) return;
        for (String command : str) {
            hasPrefix(command, player);
        }
    }

    private static void hasPrefix(String command, Player player) {
        String[] result = parseCommand(command);
        if (result == null || result.length != 2) return;

        switch (result[0].toUpperCase()) {
            case "COMMAND"   : runAsPlayer   (parse(player, result[1]), player); break;
            case "OP"        : runAsOp       (parse(player, result[1]), player); break;
            case "CONSOLE"   : runAsConsole  (parse(player, result[1])); break;
            case "TELL"      : sendTell      (parse(player, result[1]), player); break;
            case "TITLE"     : sendTitle     (parse(player, result[1]), player); break;
            case "ACTIONBAR" : sendActionBar (parse(player, result[1]), player); break;

            case "COMMANDALL"   :
                PlayerUtil.forEachOnlinePlayer(
                        myPlayer -> runAsPlayer(parse(myPlayer, result[1]), player)); break;
            case "OPALL"        :
                PlayerUtil.forEachOnlinePlayer(
                        myPlayer -> runAsOp(parse(player, replacePlayer(result[1], player.getName())), player));
                break;
            case "CONSOLEALL"   :
                PlayerUtil.forEachOnlinePlayer(
                        myPlayer -> runAsConsole(parse(player, replacePlayer(result[1], player.getName()))));
                break;
            case "TELLALL"      :
                PlayerUtil.forEachOnlinePlayer(
                        myPlayer -> sendTell(parse(player, replacePlayer(result[1], player.getName())), player));
                break;
            case "TITLEALL"     :
                PlayerUtil.forEachOnlinePlayer(
                        myPlayer -> sendTitle(parse(player, replacePlayer(result[1], player.getName())), player));
                break;
            case "ACTIONBARALL" :
                PlayerUtil.forEachOnlinePlayer(
                        myPlayer -> sendActionBar(parse(player, replacePlayer(result[1], player.getName())), player));
                break;
        }
    }

    /**
     * 解析输入字符串的命令前缀及命令内容。
     * <p>
     * 支持的前缀包括："command"、"op"、"console"、"title"、"tell"（不区分大小写）。
     *
     * @param input 要解析的字符串，例如：command: say Hello
     * @return 长度为 2 的数组，index 0 为前缀，index 1 为命令内容；格式不合法返回 null
     */
    private static String[] parseCommand(String input) {
        if (input == null) return null;

        Pattern pattern = Pattern.compile("^(command|op|console|tell|title|actionbar|" +
                "consoleAll|commandAll|opAll|tellAll|titleAll|actionbarAll):\\s*(.+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            String prefix = matcher.group(1).toLowerCase(); // 前缀统一小写
            String content = matcher.group(2);              // 命令内容
            return new String[]{prefix, content};
        }

        return null; // 不符合格式
    }

    /**
     * 向玩家发送聊天栏消息（支持颜色符号 & → §）
     *
     * @param player 玩家
     * @param message 消息内容
     */
    private static void sendTell(String message, Player player) {
        if (player != null && message != null) {
            player.sendMessage(message);
        }
    }

    /**
     * 发送动作栏消息（物品栏上方）
     *
     * @param player 目标玩家
     * @param message 消息内容
     */
    private static void sendActionBar(String message, Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent(message));
    }

    /**
     * 发送 Title 消息给玩家，自动从格式字符串中解析标题、子标题和时间参数。
     * <pre>{@code
     * 输入格式：
     *    "`主标题` `副标题` 渐入时间 停留时间 渐出时间"
     * 输出提示：
     *    "`主标题` `副标题` 10 200 10"
     * }</pre>
     *
     * @param player 玩家
     * @param input  格式化的字符串
     */
    private static void sendTitle(String input, Player player) {
        if (player == null || input == null || input.trim().isEmpty()) return;

        String title, subTitle = "";
        int fadeIn = 10, stay = 100, fadeOut = 10;

        Pattern pattern = Pattern.compile(
                "`([^`]*)`" +                         // title
                "(?:\\s+`([^`]*)`)?" +                // optional subtitle
                "(?:\\s+(\\d+)\\s+(\\d+)\\s+(\\d+))?" // optional numbers
        );
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            title = matcher.group(1);
            if (matcher.group(2) != null) {
                subTitle = matcher.group(2);
            }
            if (matcher.group(3) != null) {
                try { fadeIn = Integer.parseInt(matcher.group(3)); } catch (Exception ignored) {}
                try { stay   = Integer.parseInt(matcher.group(4)); } catch (Exception ignored) {}
                try { fadeOut= Integer.parseInt(matcher.group(5)); } catch (Exception ignored) {}
            }
            player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
    }


    /**
     * 以玩家身份执行命令
     *
     * @param player  玩家实例
     * @param command 要执行的命令（无需前缀 /）
     */
    private static void runAsPlayer(String command, Player player) {
        if (player != null && command != null && !command.trim().isEmpty()) {
            player.performCommand(stripSlash(command));
        }
    }

    /**
     * 以 OP 身份执行命令（临时给予 OP 权限）
     *
     * @param player  玩家实例
     * @param command 要执行的命令（无需前缀 /）
     */
    private static void runAsOp(String command, Player player) {
        if (player == null || command == null || command.trim().isEmpty()) return;
        boolean wasOp = player.isOp();
        try {
            if (!wasOp) player.setOp(true);
            player.performCommand(stripSlash(command));

        } finally {
            if (!wasOp) player.setOp(false);
        }
    }

    /**
     * 以控制台身份执行命令
     *
     * @param command 要执行的命令（无需前缀 /）
     */
    private static void runAsConsole(String command) {
        if (command != null && !command.trim().isEmpty()) {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, stripSlash(command));
        }
    }

    /** 通过 PlaceholderAPI 解析字符串占位符  */
    private static String parse(Player player, String str) {
        try {
            Class<?> papiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            java.lang.reflect.Method method = papiClass.getMethod("setPlaceholders", Player.class, String.class);

            // PlaceholderAPI.setPlaceholders(player, str);
            return (String) method.invoke(null, player, str);
        } catch (Exception e) {
            return str;
        }
    }

    /**
     * 移除命令的 "/" 如果存在
     *
     * @param command 命令字符串
     * @return 无斜杠命令。
     */
    private static String stripSlash(String command) {
        return command.startsWith("/") ? command.substring(1) : command;
    }

    /**
     * 替换字符串里的 {lbp_Player} 占位符（大小写不敏感）
     *
     * @param input 原字符串
     * @param replacement 替换内容
     * @return 替换后的字符串
     */
    public static String replacePlayer(String input, String replacement) {
        if (input == null) return null;
        // (?i) 表示忽略大小写
        return input.replaceAll("(?i)\\{lbp_allPlayer}", replacement);
    }

}
