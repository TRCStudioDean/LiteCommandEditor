package studio.trc.bukkit.litecommandeditor.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.message.tag.TagContentExtractor;
import studio.trc.bukkit.litecommandeditor.message.tag.TagContentInfo;

public class LiteCommandEditorUtils
{
    public static void playerOnly() {
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Player-Only");
    }
    
    public static void notANumber(CommandSender sender, String number) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{number}", number);
        MessageUtil.sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Not-A-Number", placeholders);
    }
    
    public static void noPermission(CommandSender sender) {
        MessageUtil.sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "No-Permission");
    }
    
    public static void playerNotExist(CommandSender sender, String playerName) {
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{player}", playerName);
        MessageUtil.sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Player-Not-Exist", placeholders);
    }
    
    public static ItemStack getItemInHand(Player player) {
        if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8")) {
            return player.getItemInHand();
        } else {
            return player.getInventory().getItemInMainHand();
        }
    }
    
    public static String rebuildText(String[] parameters, int startsWith) {
        StringBuilder builder = new StringBuilder();
        for (int i = startsWith;i < parameters.length;i++) {
            builder.append(parameters[i]);
            if (i != parameters.length - 1) {
                builder.append(":");
            }
        }
        return builder.toString();
    }
    
    /**
     * Remove spaces from the string, except for string paragraphs that use double quotes.
     * Example: Hello World! -> HelloWorld!
     *          Hello "The World"! -> HelloThe World!
     * @param expression
     * @param sender
     * @param placeholders
     * @return 
     */
    public static String getConvertedCondition(String expression, CommandSender sender, Map<String, String> placeholders) {
        expression = MessageUtil.replacePlaceholders(sender, expression, placeholders.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().contains(" ") ? "\"" + entry.getValue() + "\"" : entry.getValue())), true, false);
        StringBuilder builder = new StringBuilder();
        List<TagContentInfo> tagContents = TagContentExtractor.getSections(expression, '\"', false)
            .stream().filter(tagInfo -> tagInfo.getCloseTag() != null).collect(Collectors.toList()); //Unclosed double quotes will still be extracted
        int lastPosition = 0; 
        for (TagContentInfo tagInfo : tagContents) {
            String outsideText = expression.substring(lastPosition, tagInfo.getStartPosition());
            builder.append(outsideText.replace(" ", ""));
            builder.append(tagInfo.getContent());
            lastPosition = tagInfo.getEndPosition() + tagInfo.getCloseTag().length();
        }
        String remainingText = expression.substring(lastPosition);
        builder.append(remainingText.replace(" ", ""));
        return builder.toString();
    }
    
    /**
     * Remove spaces from the string, except for string fragments using double quotes, and split the string with colons.
     * Example: Condition:Options and details:Parameters -> [Condition, Optionsanddetails, Parameters]
     *          Condition:"Options and details":Parameters -> [Condition, Options and details, Parameters]
     * @param expression
     * @param sender
     * @param placeholders
     * @return 
     */
    public static String[] getConditionParameters(String expression, CommandSender sender, Map<String, String> placeholders) {
        return MessageUtil.splitStringBySymbol(getConvertedCondition(expression, sender, placeholders), ':');
    }
    
    public static boolean isInteger(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean isLong(String value) {
        try {
            Long.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean isDouble(String value) {
        try {
            Double.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean isFloat(String value) {
        try {
            Float.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean isByte(String value) {
        try {
            Byte.valueOf(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean isUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
    
    public static int getRandomNumber(int number1, int number2) {
        if (number1 == number2) {
            return number1;
        } else if (number1 > number2) {
            return new Random().nextInt(number1 - number2 + 1) + number2;
        } else if (number2 > number1) {
            return new Random().nextInt(number2 - number1 + 1) + number1;
        }
        return 0;
    }
    
    public static boolean isPlayer(CommandSender sender, boolean report) {
        boolean value = sender instanceof Player;
        if (report && !value) {
            playerOnly();
        }
        return value;
    }
    
    public static boolean hasCommandPermission(CommandSender sender, String configPath, boolean report) {
        if (configPath == null) return true;
        boolean value = PermissionManager.hasPermission(sender, ConfigurationType.PERMISSIONS, configPath);
        if (report && !value) {
            noPermission(sender);
        }
        return value;
    }
    
    public static Object toValue(String raw) {
        Object value;
        if (LiteCommandEditorUtils.isLong(raw)) {
            value = Long.valueOf(raw);
        } else if (LiteCommandEditorUtils.isDouble(raw)) {
            value = Double.valueOf(raw);
        } else if (raw.equals("true") || raw.equals("false")) {
            value = Boolean.valueOf(raw);
        } else {
            value = raw;
        }
        return value;
    }
    
    public static File createFile(String fileName) throws IOException {
        File file = new File(fileName);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
