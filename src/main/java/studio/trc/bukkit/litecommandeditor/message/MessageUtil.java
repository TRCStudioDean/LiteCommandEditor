package studio.trc.bukkit.litecommandeditor.message;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
import studio.trc.bukkit.litecommandeditor.util.AdventureUtils;
import studio.trc.bukkit.litecommandeditor.itemmanager.ItemUtil;
import studio.trc.bukkit.litecommandeditor.message.color.ColorUtils;
import studio.trc.bukkit.litecommandeditor.message.placeholder.ArgumentsPlaceholderRequest;
import studio.trc.bukkit.litecommandeditor.message.placeholder.PlaceholderRequestUtils;

public class MessageUtil
{
    private static final Map<String, String> defaultPlaceholders = new HashMap<>();
    
    @Getter
    @Setter
    private static boolean enabledPAPI = false;
    
    public static void addDefaultPlaceholder(String placeholder, String value) {
        defaultPlaceholders.put(placeholder, value);
    }
    
    public static void removeDefaultPlaceholder(String placeholder) {
        defaultPlaceholders.remove(placeholder);
    }
    
    public static void loadPlaceholders() {
        defaultPlaceholders.clear();
        defaultPlaceholders.put("{plugin_version}", Main.getInstance().getDescription().getVersion());
        defaultPlaceholders.put("{language}", getLangaugeName());
        defaultPlaceholders.put("{prefix}", getPrefix());
    }
    
    public static void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, defaultPlaceholders);
    }
    
    public static void sendMessage(CommandSender sender, String message, Map<String, String> placeholders) {
        if (useAdventure()) {
            sendAdventureMessage(sender, message, placeholders, null);
        } else {
            sendBungeeMessage(sender, message, placeholders, null);
        }
    }
    
    public static void sendBungeeMessage(CommandSender sender, String message, Map<String, String> placeholders, Map<String, BaseComponent> additionalComponents) {
        if (sender == null) return;
        String sample = replacePlaceholders(sender, message, placeholders);
        Map<String, BaseComponent> components = JSONComponentManager.getDefaultJSONComponents().getPlaceholderAbout(sample).entrySet()
            .stream().collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().getBungeeComponent(placeholders)));
        if (additionalComponents != null && !additionalComponents.isEmpty()) {
            components.putAll(additionalComponents);
        }
        if (!components.isEmpty()) {
            sendBungeeJSONMessage(sender, MessageEditor.createBungeeJSONMessage(sender, sample, components));
        } else {
            sender.sendMessage(sample);
        }
    }
    
    public static void sendAdventureMessage(CommandSender sender, String message, Map<String, String> placeholders, Map<String, Object> additionalComponents) {
        if (sender == null) return;
        String sample = replacePlaceholders(sender, message, placeholders);
        Map<String, Component> components = JSONComponentManager.getDefaultJSONComponents().getPlaceholderAbout(sample).entrySet()
            .stream().collect(Collectors.toMap(Map.Entry::getKey, value -> AdventureUtils.toComponent(value.getValue().getAdventureComponent(placeholders))));
        if (additionalComponents != null && !additionalComponents.isEmpty()) {
            components.putAll(AdventureUtils.toAdventureComponents(additionalComponents));
        }
        if (!components.isEmpty()) {
            sendAdventureJSONMessage(sender, MessageEditor.createAdventureJSONMessage(sender, sample, components));
        } else {
            sender.sendMessage(sample);
        }
    }
    
    public static void sendMixedMessage(CommandSender sender, String message, Map<String, String> placeholders, Map<String, JSONComponent> additionalComponents, Map<String, String> additionalPlaceholders) {
        if (sender == null) return;
        String sample = replacePlaceholders(sender, message, placeholders);
        if (useAdventure()) {
            Map<String, Component> components = JSONComponentManager.getDefaultJSONComponents().getPlaceholderAbout(sample).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, value -> AdventureUtils.toComponent(value.getValue().getAdventureComponent(placeholders))));
            if (additionalComponents != null && !additionalComponents.isEmpty()) {
                components.putAll(additionalComponents.entrySet()
                    .stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> AdventureUtils.toComponent(entry.getValue().getAdventureComponent(additionalPlaceholders)))));
            }
            if (!components.isEmpty()) {
                sendAdventureJSONMessage(sender, MessageEditor.createAdventureJSONMessage(sender, sample, components));
            } else {
                sender.sendMessage(sample);
            }
        } else {
            Map<String, BaseComponent> components = JSONComponentManager.getDefaultJSONComponents().getPlaceholderAbout(sample).entrySet()
                .stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getBungeeComponent(additionalPlaceholders)));
            if (additionalComponents != null && !additionalComponents.isEmpty()) {
                components.putAll(additionalComponents.entrySet()
                    .stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getBungeeComponent(additionalPlaceholders))));
            }
            if (!components.isEmpty()) {
                sendBungeeJSONMessage(sender, MessageEditor.createBungeeJSONMessage(sender, sample, components));
            } else {
                sender.sendMessage(sample);
            }
        }
    }
    
    public static void sendMessageWithItem(CommandSender sender, String message, Map<String, String> placeholders, ItemStack item) {
        if (useAdventure()) {
            Map<String, Object> json = new HashMap<>();
            json.put("%item%", ItemUtil.getAdventureJSONItemStack(item));
            sendAdventureMessage(sender, message, placeholders, json);
        } else {
            Map<String, BaseComponent> json = new HashMap<>();
            json.put("%item%", ItemUtil.getBungeeJSONItemStack(item));
            sendBungeeMessage(sender, message, placeholders, json);
        }
    }
    
    public static void sendMessage(CommandSender sender, List<String> messages) {
        messages.stream().forEach(rawMessage -> sendMessage(sender, rawMessage));
    }
    
    public static void sendMessage(CommandSender sender, List<String> messages, Map<String, String> placeholders) {
        messages.stream().forEach(rawMessage -> sendMessage(sender, rawMessage, placeholders));
    }
    
    public static void sendBungeeMessage(CommandSender sender, List<String> messages, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        messages.stream().forEach(rawMessage -> sendBungeeMessage(sender, rawMessage, placeholders, jsonComponents));
    }
    
    public static void sendAdventureMessage(CommandSender sender, List<String> messages, Map<String, String> placeholders, Map<String, Object> jsonComponents) {
        messages.stream().forEach(rawMessage -> sendAdventureMessage(sender, rawMessage, placeholders, jsonComponents));
    }
    
    public static void sendMessage(CommandSender sender, List<String> messages, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        messages.stream().forEach(rawMessage -> sendMixedMessage(sender, rawMessage, placeholders, jsonComponents, jsonComponentPlaceholders));
    }
    
    public static void sendMessage(CommandSender sender, RobustConfiguration configuration, String configPath) {
        sendMessage(sender, configuration, configPath, defaultPlaceholders);
    }
    
    public static void sendMessage(CommandSender sender, RobustConfiguration configuration, String configPath, Map<String, String> placeholders) {
        if (useAdventure()) {
            sendAdventureMessage(sender, configuration, configPath, placeholders, null);
        } else {
            sendBungeeMessage(sender, configuration, configPath, placeholders, null);
        }
    }
    
    public static void sendBungeeMessage(CommandSender sender, RobustConfiguration configuration, String configPath, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        List<String> messages = configuration.getStringList(getLanguage() + "." + configPath);
        if (messages.isEmpty() && !ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + "." + configPath).equals("[]")) {
            sendBungeeMessage(sender, configuration.getString(getLanguage() + "." + configPath), placeholders, jsonComponents);
        } else {
            sendBungeeMessage(sender, messages, placeholders, jsonComponents);
        }
    }
    
    public static void sendAdventureMessage(CommandSender sender, RobustConfiguration configuration, String configPath, Map<String, String> placeholders, Map<String, Object> jsonComponents) {
        List<String> messages = configuration.getStringList(getLanguage() + "." + configPath);
        if (messages.isEmpty() && !ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + "." + configPath).equals("[]")) {
            sendAdventureMessage(sender, configuration.getString(getLanguage() + "." + configPath), placeholders, jsonComponents);
        } else {
            sendAdventureMessage(sender, messages, placeholders, jsonComponents);
        }
    }
    
    public static void sendMessage(CommandSender sender, RobustConfiguration configuration, String configPath, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        List<String> messages = configuration.getStringList(getLanguage() + "." + configPath);
        if (messages.isEmpty() && !ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + "." + configPath).equals("[]")) {
            sendMixedMessage(sender, configuration.getString(getLanguage() + "." + configPath), placeholders, jsonComponents, jsonComponentPlaceholders);
        } else {
            sendMessage(sender, messages, placeholders, jsonComponents, jsonComponentPlaceholders);
        }
    }
    
    public static void sendBungeeJSONMessage(CommandSender sender, List<BaseComponent> components) {
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(components.toArray(new BaseComponent[] {}));
        } else {
            StringBuilder builder = new StringBuilder();
            components.stream().map(component -> component.toPlainText()).forEach(builder::append);
            sender.sendMessage(builder.toString());
        }
    }
    
    public static void sendAdventureJSONMessage(CommandSender sender, Object component) {
        try {
            sender.getClass().getMethod("sendMessage", adventureAPI).invoke(sender, component);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void sendConsoleMessage(String configPath, ConfigurationType type) {
        sendMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, defaultPlaceholders);
    }
    
    public static void sendConsoleMessage(String configPath, ConfigurationType type, Map<String, String> placeholders) {
        sendMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, placeholders);
    }
    
    public static void sendConsoleBungeeMessage(String configPath, ConfigurationType type, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        sendBungeeMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, placeholders, jsonComponents);
    }
    
    public static void sendConsoleAdventureMessage(String configPath, ConfigurationType type, Map<String, String> placeholders, Map<String, Object> jsonComponents) {
        sendAdventureMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, placeholders, jsonComponents);
    }
    
    public static void sendConsoleMessage(String configPath, ConfigurationType type, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        sendMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, placeholders, jsonComponents, jsonComponentPlaceholders);
    }
    
    public static void sendCommandMessage(CommandSender sender, String configPath) {
        sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, defaultPlaceholders);
    }
    
    public static void sendCommandMessage(CommandSender sender, String configPath, Map<String, String> placeholders) {
        sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, placeholders);
    }
    
    public static void sendCommandBungeeMessage(CommandSender sender, String configPath, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        sendBungeeMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, placeholders, jsonComponents);
    }
    
    public static void sendCommandAdventureMessage(CommandSender sender, String configPath, Map<String, String> placeholders, Map<String, Object> jsonComponents) {
        sendAdventureMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, placeholders, jsonComponents);
    }
    
    public static void sendCommandMessage(CommandSender sender, String configPath, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, placeholders, jsonComponents, jsonComponentPlaceholders);
    }
    
    public static void sendCommandMessageWithItem(CommandSender sender, String configPath, Map<String, String> placeholders, ItemStack item) {
        if (useAdventure()) {
            Map<String, Object> json = new HashMap<>();
            json.put("%item%", ItemUtil.getAdventureJSONItemStack(item));
            sendCommandAdventureMessage(sender, configPath, placeholders, json);
        } else {
            Map<String, BaseComponent> json = new HashMap<>();
            json.put("%item%", ItemUtil.getBungeeJSONItemStack(item));
            sendCommandBungeeMessage(sender, configPath, placeholders, json);
        }
    }
    
    public static String replacePlaceholders(String message, String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return replacePlaceholders(message, map, true);
    }
    
    public static String replacePlaceholders(String message, Map<String, String> placeholders) {
        return replacePlaceholders(message, placeholders, true);
    }
    
    /**
     * Perform placeholder replacement. (No functional placeholders)
     * @param message Message content.
     * @param placeholders Placeholder's map.
     * @param toColor Whether to coloring.
     * @return 
     */
    public static String replacePlaceholders(String message, Map<String, String> placeholders, boolean toColor) {
        if (message == null || placeholders.isEmpty()) return message;
        StringBuilder builder = new StringBuilder();
        try {
            //Execute replacements
            List<MessageSection> sections = MessageEditor.parse(message, placeholders);
            sections.stream().forEach(section -> {
                if (section.isPlaceholder()) {
                    builder.append(placeholders.getOrDefault(section.getPlaceholder(), placeholders.entrySet().stream().collect(Collectors.toMap(key -> key.getKey().toLowerCase(), Map.Entry::getValue)).get(section.getPlaceholder().toLowerCase())));
                } else {
                    builder.append(section.getText().replace("/n", "\n"));
                }
            });

            //Update result
            message = builder.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return toColor ? ColorUtils.toColor(message) : message;
    }
    
    public static String replacePlaceholders(CommandSender sender, String message, Map<String, String> placeholders) {
        return replacePlaceholders(sender, message, placeholders, true, true);
    }
    
    public static String replacePlaceholders(CommandSender sender, String message, Map<String, String> placeholders, boolean loop) {
        return replacePlaceholders(sender, message, placeholders, loop, true);
    }
    
    /**
     * Perform placeholder replacement. 
     * It is usually used for the functionality of custom commands. (Contains placeholders for some special functions)
     * @param sender Command sender (Optional). Use for PlaceholderAPI hook.
     * @param message Message content.
     * @param placeholders Placeholder's map.
     * @param loop Whether to cycle replacement.
     * @param toColor Whether to coloring.
     * @return 
     */
    public static String replacePlaceholders(CommandSender sender, String message, Map<String, String> placeholders, boolean loop, boolean toColor) {
        if (message == null) return message;
        
        //Preprocess and replace placeholders for all argument.
        Map<String, String> tempPlaceholders = PlaceholderRequestUtils.filterPlaceholders("[", "]", placeholders);
        if (!tempPlaceholders.isEmpty()) {
            PlaceholderRequestUtils.findPlaceholders('[', ']', message, argumentExpression -> ArgumentsPlaceholderRequest.argumentsPlaceholderRequest(tempPlaceholders, argumentExpression));
            message = replacePlaceholders(message, tempPlaceholders, false); 
        }
        
        //Create retention for raw text.
        Map<String, String> rawRetentions = new HashMap<>();
        tempPlaceholders.clear();
        PlaceholderRequestUtils.findPlaceholders('{', '}', message, placeholder -> PlaceholderRequestUtils.analysisRawRequest(tempPlaceholders, placeholder, rawRetentions));
        if (!tempPlaceholders.isEmpty()) {
            message = replacePlaceholders(message, tempPlaceholders, false);
        }
        
        //Cycle replacement placeholders
        StringBuilder builder = new StringBuilder();
        do {
            try {
                //Execute replacements
                if (builder.length() > 0) {
                    builder.delete(0, builder.length());
                }
                List<MessageSection> sections = MessageEditor.parse(message, placeholders);
                sections.stream().forEach(section -> {
                    if (section.isPlaceholder()) {
                        builder.append(placeholders.getOrDefault(section.getPlaceholder(), placeholders.entrySet().stream().collect(Collectors.toMap(key -> key.getKey().toLowerCase(), Map.Entry::getValue)).get(section.getPlaceholder().toLowerCase())));
                    } else {
                        builder.append(sender != null ?
                                toPlaceholderAPIResult(section.getText(), sender).replace("/n", "\n") :
                                section.getText().replace("/n", "\n"));
                    }
                });
            
                //Update result
                message = builder.toString();
                
                //Analyze placeholders for special feature and add corresponding placeholders
                PlaceholderRequestUtils.findPlaceholders('{', '}', message, placeholder -> PlaceholderRequestUtils.analysisPlaceholderRequest(placeholders, placeholder));

                //Detect placeholder replacement loop and destroy it
                if (loop) breakPlaceholdersCyclicDependencies(placeholders);
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        } while (loop && placeholders.keySet().stream().map(placeholder -> placeholder.toLowerCase()).anyMatch(placeholder -> builder.toString().toLowerCase().contains(placeholder)));
        
        //Create retention for colourless text.
        Map<String, String> colourlessRetentions = new HashMap<>();
        tempPlaceholders.clear();
        PlaceholderRequestUtils.findPlaceholders('{', '}', message, placeholder -> PlaceholderRequestUtils.analysisColourlessRequest(tempPlaceholders, placeholder, colourlessRetentions));
        if (!tempPlaceholders.isEmpty()) {
            message = replacePlaceholders(message, tempPlaceholders, false);
        }
        
        //Coloring
        if (toColor) { 
            message = ColorUtils.toColor(message);
        }
        
        //Restore colourless contents
        if (!colourlessRetentions.isEmpty()) {
            message = replacePlaceholders(message, colourlessRetentions, false);
        }
        
        //Restore raw contents
        if (!rawRetentions.isEmpty()) {
            message = replacePlaceholders(message, rawRetentions); 
        }
        return message;
    }
    
    /**
     * Convert the substitution relationship of placeholders into a graph structure.
     * Example: 
     *     Input:
     *  {
     *      "{A}": "{B} and {C}",
     *      "{B}": "valueB",
     *      "{C}": "{B} or {D}",
     *      "{D}": "valueD"
     *  }
     *      Output:
     *  {
     *      "{A}": ["{B}", "{C}"],
     *      "{B}": [],
     *      "{C}": ["{B}", "{D}"],
     *      "{D}": []
     *  }
     * @param placeholders Original placeholder mapping table
     * @return Dependency diagram (each placeholder -> collection of other placeholders it directly depends on)
     */
    public static Map<String, Set<String>> buildPlaceholdersDependencyGraph(Map<String, String> placeholders) {
        Map<String, Set<String>> graph = new HashMap<>();
        placeholders.entrySet().stream().forEach(entry -> {
            String placeholder = entry.getKey();
            String value = entry.getValue();
            Set<String> dependencies = new HashSet<>();
            placeholders.keySet().stream().filter(other -> value.toLowerCase().contains(other.toLowerCase())).forEach(dependencies::add);
            graph.put(placeholder, dependencies);
        });
        return graph;
    }
    
    /**
     * Check if there is a placeholder loop.
     * If there is a loop, block it.
     * @param placeholders Original placeholder mapping table
     * @return Successful
     */
    public static boolean breakPlaceholdersCyclicDependencies(Map<String, String> placeholders) {
        //Use DFS to detect the presence of loops in the graph.
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        Map<String, Set<String>> graph = buildPlaceholdersDependencyGraph(placeholders);
        boolean success = false;
        for (String placeholder : graph.keySet()) {
            String node = getPlaceholderCycle(placeholder, graph, visited, recursionStack);
            if (node != null) {
                placeholders.remove(node);
                if (!success) success = true;
            }
        }
        return success;
    }

    public static String getPlaceholderCycle(String node, Map<String, Set<String>> graph, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(node)) return node;
        if (visited.contains(node)) return null;
        visited.add(node);
        recursionStack.add(node);
        String result = graph.entrySet()
            .stream().filter(neighbor -> neighbor.getKey().equalsIgnoreCase(node)).map(neighbor -> graph.get(neighbor.getKey())).findFirst().orElse(new HashSet<>())
            .stream().map(neighbor -> getPlaceholderCycle(neighbor, graph, visited, recursionStack)).filter(neighbor -> neighbor != null).findFirst().orElse(null);
        if (result != null) {
            return result;
        }
        recursionStack.remove(node);
        return null;
    }
    
    public static String escape(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replace("[", "\\[").replace("]", "\\]").replace("{", "\\{").replace("}", "\\}").replace("+", "\\+").replace("*", "\\*").replace("|", "\\|").replace("?", "\\?").replace("$", "\\$").replace("^", "\\^");
    }
    
    public static String toPlaceholderAPIResult(String text, CommandSender sender) {
        return text != null && isEnabledPAPI() && sender instanceof Player ? PlaceholderAPI.setPlaceholders((Player) sender, text) : text;
    }
    
    public static String getMessage(String configPath) {
        return ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + "." + configPath);
    }
    
    public static String getMessage(ConfigurationType configType, String configPath) {
        return configType.getRobustConfig().getString(getLanguage() + "." + configPath);
    }
    
    public static String getMessage(YamlConfiguration config, String configPath) {
        return config.getString(getLanguage() + "." + configPath);
    }
    
    public static List<String> getMessageList(String path) {
        return getMessageList(ConfigurationType.MESSAGES, getLanguage() + "." + path);
    }
    
    public static List<String> getMessageList(ConfigurationType configType, String configPath) {
        List<String> messages = configType.getRobustConfig().getStringList(configPath);
        if (messages.isEmpty() && !configType.getRobustConfig().getString(configPath).equals("[]")) {
            messages.add(configType.getRobustConfig().getString(configPath));
        }
        return messages;
    }
    
    public static List<String> getMessageList(YamlConfiguration config, String configPath) {
        List<String> messages = config.getStringList(configPath);
        if (config.contains(configPath)) {
            if (messages.isEmpty() && !config.getString(configPath).equals("[]")) {
                messages.add(config.getString(configPath));
            }
        }
        return messages;
    }
    
    public static String getProtectedMessage(String configPath) {
        return ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + "." + configPath);
    }
    
    public static String getProtectedMessage(ConfigurationType configType, String configPath) {
        return configType.getRobustConfig().getString(getLanguage() + "." + configPath);
    }
    
    public static String getLanguage() {
        return ConfigurationType.CONFIG.getRobustConfig().getString("Language");
    }
    
    public static String getItemDisplayLanguagePath() {
        return ConfigurationType.CONFIG.getRobustConfig().getString("Item-Display-Language-Path");
    }
    
    public static String doBasicProcessing(String text) {
        return replacePlaceholders(text, defaultPlaceholders);
    }

    public static String getPrefix() {
        return ColorUtils.toColor(getProtectedMessage("Prefix"));
    }
    
    public static String getLangaugeName() {
        return ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + ".Language-Name");
    }
    
    public static String[] splitStringBySymbol(String text, char symbol) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escape = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (escape) {
                current.append(c);
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == symbol) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0 || escape) {
            result.add(current.toString());
        }
        return result.toArray(new String[result.size()]);
    }
    
    public static Map<String, String> getDefaultPlaceholders() {
        return new HashMap<>(defaultPlaceholders);
    }
    
    private static boolean adventureAvailable = false;
    private static Class<?> adventureAPI;
    
    public static void setAdventureAvailable() {
        try {
            adventureAPI = Class.forName("net.kyori.adventure.text.Component");
            adventureAvailable = !Bukkit.getBukkitVersion().startsWith("1.7") && !Bukkit.getBukkitVersion().startsWith("1.8") && !Bukkit.getBukkitVersion().startsWith("1.9") && !Bukkit.getBukkitVersion().startsWith("1.10") &&
                !Bukkit.getBukkitVersion().startsWith("1.11") && !Bukkit.getBukkitVersion().startsWith("1.12") && !Bukkit.getBukkitVersion().startsWith("1.13") && !Bukkit.getBukkitVersion().startsWith("1.14") &&
                !Bukkit.getBukkitVersion().startsWith("1.15") && !Bukkit.getBukkitVersion().startsWith("1.16") && !Bukkit.getBukkitVersion().startsWith("1.17");
        } catch (ClassNotFoundException ex) {
            adventureAvailable = false;
        }
    }
    
    public static boolean useAdventure() {
        return adventureAvailable;
    }
    
    public static enum Language {
        
        /**
         * Simplified Chinese
         */
        SIMPLIFIED_CHINESE("Simplified-Chinese"),
        
        /**
         * Traditional Chinese
         */
        TRADITIONAL_CHINESE("Traditional-Chinese"),
        
        /**
         * English
         */
        ENGLISH("English");
        
        public static Language getLocaleLanguage() {
            String language = System.getProperty("user.language");
            String country = System.getProperty("user.country");
            if (language.equalsIgnoreCase("zh")) {
                if (country != null && country.equalsIgnoreCase("cn")) {
                    return SIMPLIFIED_CHINESE;
                } else {
                    return TRADITIONAL_CHINESE;
                }
            } else {
                return ENGLISH;
            }
        }
        
        @Getter
        private final String folderName;
        
        private Language(String folderName) {
            this.folderName = folderName;
        }
    }
}
