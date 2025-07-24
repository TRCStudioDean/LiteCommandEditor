package studio.trc.bukkit.litecommandeditor.itemmanager;

import com.pa_project.lib.json.JSONObject;
import com.pa_project.lib.json.parser.JSONParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import studio.trc.bukkit.litecommandeditor.command.subcommand.ToolsCommand;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
import studio.trc.bukkit.litecommandeditor.util.AdventureUtils;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.util.NMSUtils;

/**
 * @author Dean
 */
public class ItemUtil 
{
    @Getter
    static JSONObject languageJSONObject = null;
    @Getter
    static Properties languageProperties = null;
    
    @Getter
    private static JSONObject manifestJSON = null;
    @Getter
    private static JSONObject resourcesJSON = null;
    @Getter
    private static JSONObject assetIndex = null;
    @Getter
    @Setter
    private static boolean downloading = false;
    
    public static long updateItemNames(CommandSender sender, String path) {
        // 1.7.10 - 1.12.2
        if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10") || Bukkit.getBukkitVersion().startsWith("1.11") || Bukkit.getBukkitVersion().startsWith("1.12")) {
            if (languageProperties == null) return 0;
            RobustConfiguration config = ConfigurationType.ITEMS.getRobustConfig();
            Map<String, String> materials = LegacyItemUtil.updateLegacyItemNames();
            materials.keySet().stream().sorted().forEach(name -> config.set(path + "." + name, materials.get(name)));
            config.getType().saveConfig();
            return materials.keySet().stream().filter(key -> !key.contains(":")).count();
        } else {
        //1.13 - latest version
            if (languageJSONObject == null) return 0;
            RobustConfiguration config = ConfigurationType.ITEMS.getRobustConfig();
            Map<String, String> materials = new HashMap<>();
            Arrays.stream(Material.values()).forEach(material -> {
                try {
                    String itemID = material.name();
                    if (itemID.endsWith("_WALL_BANNER")) {
                        materials.put(itemID, languageJSONObject.getString("block.minecraft." + itemID.replace("_WALL_BANNER", "_BANNER").toLowerCase()));
                    } else if (itemID.endsWith("_UPGRADE_SMITHING_TEMPLATE")) {
                        materials.put(itemID, languageJSONObject.getString("upgrade.minecraft." + itemID.toLowerCase().substring(0, itemID.length() - "_smithing_template".length())));
                    } else if (itemID.endsWith("_ARMOR_TRIM_SMITHING_TEMPLATE")) {
                        materials.put(itemID, languageJSONObject.getString("trim_pattern.minecraft." + itemID.toLowerCase().substring(0, itemID.length() - "_armor_trim_smithing_template".length())));
                    } else if (itemID.startsWith("MUSIC_DISC") || itemID.endsWith("_BANNER_PATTERN")) {
                        if (languageJSONObject.containsKey("item.minecraft." + itemID.toLowerCase() + ".desc")) {
                            materials.put(itemID, languageJSONObject.getString("item.minecraft." + itemID.toLowerCase() + ".desc"));
                        } else {
                            materials.put(itemID, languageJSONObject.getString("item.minecraft." + itemID.toLowerCase()));
                        }
                    } else if (languageJSONObject.containsKey("item.minecraft." + itemID.toLowerCase())) {
                        materials.put(itemID, languageJSONObject.getString("item.minecraft." + itemID.toLowerCase()));
                    } else if (languageJSONObject.containsKey("block.minecraft." + itemID.toLowerCase())) {
                        materials.put(itemID, languageJSONObject.getString("block.minecraft." + itemID.toLowerCase()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            materials.keySet().stream().sorted().forEach(name -> config.set(path + "." + name, materials.get(name)));
            config.getType().saveConfig();
            return materials.size();
        }
    }
    
    public static boolean downloadLanguage(CommandSender sender, String language) {
        downloading = true;
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        // 1.7.10 - 1.12.2
        if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10") || Bukkit.getBukkitVersion().startsWith("1.11") || Bukkit.getBukkitVersion().startsWith("1.12")) {
            try {
                switch (language) {
                    // 1.11 - 1.12.2
                    case "en_us": {
                        languageProperties = getURLToProperties(Bukkit.class.getResource("/assets/minecraft/lang/en_us.lang"));
                        break;
                    }
                    // 1.7.10 - 1.10.2
                    case "en_US": {
                        languageProperties = getURLToProperties(Bukkit.class.getResource("/assets/minecraft/lang/en_US.lang"));
                        break;
                    }
                    // Another languages
                    default: {
                        //Download manifest
                        if (manifestJSON == null) {
                            String url = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
                            placeholders.put("{manifestLink}", url);
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-Versions", placeholders);
                            manifestJSON = getURLToJSONObject(new URL(url));
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-Versions", placeholders);
                        }
                        placeholders.put("{version}", getServerVersion());
                        JSONObject targetVersion = (JSONObject) manifestJSON.getJSONList("versions").stream().filter(version -> version instanceof JSONObject).filter(version -> ((JSONObject) version).getString("id").equals(getServerVersion())).findFirst().orElse(null);
                        //Check version
                        if (targetVersion != null) {
                            //Download resources index
                            if (resourcesJSON == null) {
                                placeholders.put("{resourceLink}", targetVersion.getString("url"));
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-URL", placeholders);
                                resourcesJSON = getURLToJSONObject(new URL(targetVersion.getString("url")));
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-URL", placeholders);
                            }
                            //Download asset index
                            if (assetIndex == null) {
                                placeholders.put("{assetIndexLink}", resourcesJSON.getString("assetIndex.url"));
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-Asset-Index", placeholders);
                                assetIndex = getURLToJSONObject(new URL(resourcesJSON.getString("assetIndex.url")));
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-Asset-Index", placeholders);
                            }
                            JSONObject objects = assetIndex.getJSONObject("objects");
                            placeholders.put("{languageCode}", language);
                            //Download language file
                            if (objects.containsKey("minecraft/lang/" + language + ".lang")) {
                                String hash = objects.getJSONObject("minecraft/lang/" + language + ".lang").getString("hash");
                                placeholders.put("{hash}", hash);
                                String hashLink = "https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash;
                                placeholders.put("{hashLink}", hashLink);
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-Language-Resource", placeholders);
                                languageProperties = getURLToProperties(new URL(hashLink));
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-Language-Resource", placeholders);
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Language-Code-Not-Found", placeholders);
                                downloading = false;
                                return false;
                            }
                        } else {
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Version-Not-Found", placeholders);
                            downloading = false;
                            return false;
                        }
                        break;
                    }
                }
            } catch (Exception ex) {
                placeholders.put("{exception}", ex.getLocalizedMessage());
                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Failed", placeholders);
                downloading = false;
                return false;
            }
            downloading = false;
            return true;
        } else {
        // 1.13 - latest version
            try {
                if (language.equals("en_us")) {
                    languageJSONObject = getURLToJSONObject(Bukkit.class.getResource("/assets/minecraft/lang/en_us.json"));
                } else {
                    //Download manifest
                    if (manifestJSON == null) {
                        String url = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
                        placeholders.put("{manifestLink}", url);
                        MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-Versions", placeholders);
                        manifestJSON = getURLToJSONObject(new URL(url));
                        MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-Versions", placeholders);
                    }
                    placeholders.put("{version}", getServerVersion());
                    JSONObject targetVersion = (JSONObject) manifestJSON.getJSONList("versions").stream().filter(version -> version instanceof JSONObject).filter(version -> ((JSONObject) version).getString("id").equals(getServerVersion())).findFirst().orElse(null);
                    //Check version
                    if (targetVersion != null) {
                        //Download resources index
                        if (resourcesJSON == null) {
                            placeholders.put("{resourceLink}", targetVersion.getString("url"));
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-URL", placeholders);
                            resourcesJSON = getURLToJSONObject(new URL(targetVersion.getString("url")));
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-URL", placeholders);
                        }
                        //Download asset index
                        if (assetIndex == null) {
                            placeholders.put("{assetIndexLink}", resourcesJSON.getString("assetIndex.url"));
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-Asset-Index", placeholders);
                            assetIndex = getURLToJSONObject(new URL(resourcesJSON.getString("assetIndex.url")));
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-Asset-Index", placeholders);
                        }
                        JSONObject objects = assetIndex.getJSONObject("objects");
                        placeholders.put("{languageCode}", language);
                        //Download language file
                        if (objects.containsKey("minecraft/lang/" + language + ".json")) {
                            String hash = objects.getJSONObject("minecraft/lang/" + language + ".json").getString("hash");
                            placeholders.put("{hash}", hash);
                            String hashLink = "https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash;
                            placeholders.put("{hashLink}", hashLink);
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloading-Language-Resource", placeholders);
                            languageJSONObject = getURLToJSONObject(new URL(hashLink));
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Downloaded-Language-Resource", placeholders);
                        } else {
                            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Language-Code-Not-Found", placeholders);
                            downloading = false;
                            return false;
                        }
                    } else {
                        MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Version-Not-Found", placeholders);
                        downloading = false;
                        return false;
                    }
                }
            } catch (Exception ex) {
                placeholders.put("{languageCode}", language);
                placeholders.put("{exception}", ex.getLocalizedMessage());
                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Failed", placeholders);
                downloading = false;
                return false;
            }
            downloading = false;
            return true;
        }
    }
    
    private static String getServerVersion() {
        Matcher matcher = Pattern.compile("\\(MC: .+?\\)").matcher(Bukkit.getServer().getVersion());
        if (matcher.find()) {
            return Bukkit.getServer().getVersion().substring(matcher.start() + "(MC: ".length(), matcher.end() - 1);
        }
        return null;
    }
    
    public static String getLanguageCode() {
        String using = MessageUtil.getLanguage();
        String result = null;
        switch (using) {
            case "English": {
                result = "en_us";
                break;
            }
            case "Simplified-Chinese": {
                result = "zh_cn";
                break;
            }
            case "Traditional-Chinese": {
                result = "zh_tw";
                break;
            }
        }
        if (result == null) {
            String language = System.getProperty("user.language");
            String country = System.getProperty("user.country");
            result = language + (country != null && !country.isEmpty() ? "_" + country.toLowerCase() : "");
        }
        // 1.7.10 - 1.10.2
        if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10")) {
            String[] locationAndCode = result.split("_");
            if (locationAndCode.length == 2) {
                return locationAndCode[0] + "_" + locationAndCode[1].toUpperCase();
            } else {
                return result;
            }
        } else {
            return result;
        }
    }
    
    public static String toLanguageCode(String languageName) {
        // 1.7.10 - 1.10.2
        if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10")) {
            if (languageName == null) {
                languageName = MessageUtil.getLanguage();
            }
            switch (languageName) {
                case "English": {
                    return "en_US";
                }
                case "Simplified-Chinese": {
                    return "zh_CN";
                }
                case "Traditional-Chinese": {
                    return "zh_TW";
                }
            }
            String[] locationAndCode = languageName.split("_");
            if (locationAndCode.length == 2) {
                return locationAndCode[0] + "_" + locationAndCode[1].toUpperCase();
            } else {
                return languageName;
            }
        } else {
        //1.11 - latest version
            if (languageName == null) {
                languageName = MessageUtil.getLanguage();
            }
            switch (languageName) {
                case "English": {
                    return "en_us";
                }
                case "Simplified-Chinese": {
                    return "zh_cn";
                }
                case "Traditional-Chinese": {
                    return "zh_tw";
                }
            }
            return languageName;
        }
    }
    
    public static String getItemDisplayName(ItemStack is) {
        if (is == null) return null;
        String text;
        if (is.getItemMeta() != null && is.getItemMeta().hasDisplayName()) {
            text = is.getItemMeta().getDisplayName();
        } else {
            if (!Bukkit.getBukkitVersion().startsWith("1.7") && !Bukkit.getBukkitVersion().startsWith("1.8") && !Bukkit.getBukkitVersion().startsWith("1.9") && !Bukkit.getBukkitVersion().startsWith("1.10") && !Bukkit.getBukkitVersion().startsWith("1.11") && !Bukkit.getBukkitVersion().startsWith("1.12")) {
                ConfigurationType config = ConfigurationType.ITEMS;
                if (config.getRobustConfig().getConfig().contains(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name())) {
                    text = config.getRobustConfig().getConfig().getString(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name());
                } else {
                    if (remindToUpdateItemDisplayList() && config.getRobustConfig().getConfig().contains(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name())) {
                        text = config.getRobustConfig().getConfig().getString(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name());
                    } else {
                        text = toDisplayName(is.getType().name());
                    }
                }
            } else {
                ConfigurationType config = ConfigurationType.ITEMS;
                if (is.getData().getData() == 0) {
                    if (config.getRobustConfig().getConfig().contains(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name())) {
                        text = config.getRobustConfig().getConfig().getString(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name());
                    } else {
                        if (remindToUpdateItemDisplayList() && config.getRobustConfig().getConfig().contains(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name())) {
                            text = config.getRobustConfig().getConfig().getString(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name());
                        } else {
                            text = toDisplayName(is.getType().name());
                        }
                    }
                } else {
                    if (config.getRobustConfig().getConfig().contains(MessageUtil.getLanguage() + "." + is.getType().name() + ":" + is.getData().getData())) {
                        text = config.getRobustConfig().getConfig().getString(MessageUtil.getLanguage() + "." + is.getType().name() + ":" + is.getData().getData());
                    } else if (config.getRobustConfig().getConfig().contains(MessageUtil.getLanguage() + "." + is.getType().name())) {
                        text = config.getRobustConfig().getConfig().getString(MessageUtil.getLanguage() + "." + is.getType().name());
                    } else {
                        if (remindToUpdateItemDisplayList() && config.getRobustConfig().getConfig().contains(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name() + ":" + is.getData().getData())) {
                            text = config.getRobustConfig().getConfig().getString(MessageUtil.getItemDisplayLanguagePath() + "." + is.getType().name() + ":" + is.getData().getData());
                        } else {
                            text = toDisplayName(is.getType().name());
                        }
                    }
                }
            }
        }
        return text;
    }
    
    public static Object getAdventureJSONItemStack(ItemStack item) {
        if (item != null && !item.getType().equals(Material.AIR)) {
            try {
                if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
                    return NMSUtils.JSONItem.setItemHover(item, AdventureUtils.serializeText(item.getItemMeta().getDisplayName()));
                } else {
                    String translationKey = Material.class.getMethod("translationKey").invoke(item.getType()).toString();
                    return NMSUtils.JSONItem.setItemHover(item, Component.translatable(translationKey));
                }
            } catch (Exception ex) {
                return NMSUtils.JSONItem.setItemHover(item, AdventureUtils.serializeText(getItemDisplayName(item)));
            }
        }
        return Component.text("");
    }
    
    public static TextComponent getBungeeJSONItemStack(ItemStack item) {
        if (item != null && !item.getType().equals(Material.AIR)) {
            TextComponent component = new TextComponent(getItemDisplayName(item));
            NMSUtils.JSONItem.setItemHover(item, component);
            return component;
        }
        return new TextComponent();
    }
    
    private static JSONObject getURLToJSONObject(URL url) throws IOException, JSONParseException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), LiteCommandEditorProperties.getMessage("Charset")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }
        return JSONObject.toJSONObject(builder.toString());
    }
    
    private static Properties getURLToProperties(URL url) throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(url.openStream(), LiteCommandEditorProperties.getMessage("Charset")));
        return properties;
    }
    
    /**
     * Remind the operator to update the list of item display names
     */
    private static boolean reminded = false;
    private static boolean autoUpdate = true;
    
    /**
     * @return Is the automatic update complete (English only)
     */
    private static boolean remindToUpdateItemDisplayList() {
        if (!reminded) {
            if (autoUpdate && ConfigurationType.CONFIG.getRobustConfig().getBoolean("Auto-Update-Item-Display-Name") && !ConfigurationType.ITEMS.getConfig().contains(MessageUtil.getLanguage())) {
                if (!ItemUtil.isDownloading()) {
                    LiteCommandEditorThread.runTask(() -> {
                        if (downloadLanguage(Bukkit.getConsoleSender(), getLanguageCode())) {
                            long loaded = ItemUtil.updateItemNames(Bukkit.getConsoleSender(), MessageUtil.getLanguage());
                            if (loaded > 0) {
                                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                                int total = Material.values().length;
                                placeholders.put("{languageCode}", ItemUtil.getLanguageCode());
                                placeholders.put("{path}", MessageUtil.getLanguage());
                                placeholders.put("{loaded}", String.valueOf(loaded));
                                placeholders.put("{total}", String.valueOf(total));
                                MessageUtil.sendCommandMessage(Bukkit.getConsoleSender(), "Tools.Update-Item-Display-Name.Successfully", placeholders);
                            }
                            reminded = true;
                        }
                    });
                }
                autoUpdate = false;
            } else {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{arguments}", "confirm");
                switch (MessageUtil.getLanguage()) {
                    case "English": {
                        downloadLanguage(Bukkit.getConsoleSender(), toLanguageCode("English"));
                        updateItemNames(Bukkit.getConsoleSender(), "English");
                        return true;
                    }
                    case "Simplified-Chinese":
                    case "Traditional-Chinese": {
                        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "No-Item-Display-List:Language-Code-Exist", placeholders);
                        Bukkit.getOnlinePlayers().stream()
                                .filter(player -> LiteCommandEditorUtils.hasCommandPermission(player, ToolsCommand.SubCommandType.UPDATE_ITEM_DISPLAY_NAME.getCommandPermissionPath(), false))
                                .forEach(player -> MessageUtil.sendMessage(player, ConfigurationType.MESSAGES.getRobustConfig(), "No-Item-Display-List:Language-Code-Exist", placeholders));
                        reminded = true;
                        break;
                    }
                    default: {
                        MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "No-Item-Display-List:Language-Code-Not-Exist", placeholders);
                        Bukkit.getOnlinePlayers().stream()
                                .filter(player -> LiteCommandEditorUtils.hasCommandPermission(player, ToolsCommand.SubCommandType.UPDATE_ITEM_DISPLAY_NAME.getCommandPermissionPath(), false))
                                .forEach(player -> MessageUtil.sendMessage(player, ConfigurationType.MESSAGES.getRobustConfig(), "No-Item-Display-List:Language-Code-Not-Exist", placeholders));
                        reminded = true;
                        break;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Remove the underline from the ItemID and convert the first letter of each word to uppercase.
     * @param text Input item ID
     * @return 
     */
    private static String toDisplayName(String text) {
        String[] words = text.split("_", -1);
        for (int i = 0;i < words.length;i++) {
            if (words[i].length() <= 1) continue;
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        return String.join(" ", words);
    }
}