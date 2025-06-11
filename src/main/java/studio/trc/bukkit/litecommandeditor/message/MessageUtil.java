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

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
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
        sendMessage(sender, message, defaultPlaceholders, null);
    }
    
    public static void sendMessage(CommandSender sender, String message, Map<String, String> placeholders) {
        sendMessage(sender, message, placeholders, null);
    }
    
    public static void sendMessage(CommandSender sender, String message, Map<String, String> placeholders, Map<String, BaseComponent> additionalComponents) {
        if (sender == null) return;
        String sample = replacePlaceholders(sender, message, placeholders);
        Map<String, BaseComponent> components = JSONComponentManager.getDefaultJSONComponents().getPlaceholderAbout(sample).entrySet()
            .stream().collect(Collectors.toMap(Map.Entry::getKey, value -> value.getValue().getComponent(placeholders)));
        if (additionalComponents != null && !additionalComponents.isEmpty()) {
            components.putAll(additionalComponents);
        }
        if (!components.isEmpty()) {
            sendJSONMessage(sender, createJSONMessage(sender, sample, components));
        } else {
            sender.sendMessage(sample);
        }
    }
    
    public static void sendMessage(CommandSender sender, String message, Map<String, String> placeholders, Map<String, JSONComponent> additionalComponents, Map<String, String> additionalPlaceholders) {
        if (sender == null) return;
        String sample = replacePlaceholders(sender, message, placeholders);
        Map<String, BaseComponent> components = JSONComponentManager.getDefaultJSONComponents().getPlaceholderAbout(sample).entrySet()
            .stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getComponent(additionalPlaceholders)));
        if (additionalComponents != null && !additionalComponents.isEmpty()) {
            components.putAll(additionalComponents.entrySet()
                .stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getComponent(additionalPlaceholders))));
        }
        if (!components.isEmpty()) {
            sendJSONMessage(sender, createJSONMessage(sender, sample, components));
        } else {
            sender.sendMessage(sample);
        }
    }
    
    public static void sendMessage(CommandSender sender, List<String> messages) {
        messages.stream().forEach(rawMessage -> sendMessage(sender, rawMessage));
    }
    
    public static void sendMessage(CommandSender sender, List<String> messages, Map<String, String> placeholders) {
        messages.stream().forEach(rawMessage -> sendMessage(sender, rawMessage, placeholders));
    }
    
    public static void sendMessage(CommandSender sender, List<String> messages, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        messages.stream().forEach(rawMessage -> sendMessage(sender, rawMessage, placeholders, jsonComponents));
    }
    
    public static void sendMessage(CommandSender sender, List<String> messages, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        messages.stream().forEach(rawMessage -> sendMessage(sender, rawMessage, placeholders, jsonComponents, jsonComponentPlaceholders));
    }
    
    public static void sendMessage(CommandSender sender, RobustConfiguration configuration, String configPath) {
        sendMessage(sender, configuration, configPath, defaultPlaceholders, null);
    }
    
    public static void sendMessage(CommandSender sender, RobustConfiguration configuration, String configPath, Map<String, String> placeholders) {
        sendMessage(sender, configuration, configPath, placeholders, null);
    }
    
    public static void sendMessage(CommandSender sender, RobustConfiguration configuration, String configPath, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        List<String> messages = configuration.getStringList(getLanguage() + "." + configPath);
        if (messages.isEmpty() && !ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + "." + configPath).equals("[]")) {
            sendMessage(sender, configuration.getString(getLanguage() + "." + configPath), placeholders, jsonComponents);
        } else {
            sendMessage(sender, messages, placeholders, jsonComponents);
        }
    }
    
    public static void sendMessage(CommandSender sender, RobustConfiguration configuration, String configPath, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        List<String> messages = configuration.getStringList(getLanguage() + "." + configPath);
        if (messages.isEmpty() && !ConfigurationType.MESSAGES.getRobustConfig().getString(getLanguage() + "." + configPath).equals("[]")) {
            sendMessage(sender, configuration.getString(getLanguage() + "." + configPath), placeholders, jsonComponents, jsonComponentPlaceholders);
        } else {
            sendMessage(sender, messages, placeholders, jsonComponents, jsonComponentPlaceholders);
        }
    }
    
    public static void sendJSONMessage(CommandSender sender, List<BaseComponent> components) {
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(components.toArray(new BaseComponent[] {}));
        } else {
            StringBuilder builder = new StringBuilder();
            components.stream().map(component -> component.toPlainText()).forEach(builder::append);
            sender.sendMessage(builder.toString());
        }
    }
    
    public static void sendConsoleMessage(String configPath, ConfigurationType type) {
        sendMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, defaultPlaceholders, null);
    }
    
    public static void sendConsoleMessage(String configPath, ConfigurationType type, Map<String, String> placeholders) {
        sendMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, placeholders, null);
    }
    
    public static void sendConsoleMessage(String configPath, ConfigurationType type, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        sendMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, placeholders, jsonComponents);
    }
    
    public static void sendConsoleMessage(String configPath, ConfigurationType type, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        sendMessage(Bukkit.getConsoleSender(), type.getRobustConfig(), configPath, placeholders, jsonComponents, jsonComponentPlaceholders);
    }
    
    public static void sendCommandMessage(CommandSender sender, String configPath) {
        sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, defaultPlaceholders, null);
    }
    
    public static void sendCommandMessage(CommandSender sender, String configPath, Map<String, String> placeholders) {
        sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, placeholders, null);
    }
    
    public static void sendCommandMessage(CommandSender sender, String configPath, Map<String, String> placeholders, Map<String, BaseComponent> jsonComponents) {
        sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, placeholders, jsonComponents);
    }
    
    public static void sendCommandMessage(CommandSender sender, String configPath, Map<String, String> placeholders, Map<String, JSONComponent> jsonComponents, Map<String, String> jsonComponentPlaceholders) {
        sendMessage(sender, ConfigurationType.MESSAGES.getRobustConfig(), "Command-Messages." + configPath, placeholders, jsonComponents, jsonComponentPlaceholders);
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
            List<TextParagraph> splitedTexts = splitIntoParagraphs(message, placeholders);
            for (TextParagraph paragraph : splitedTexts) {
                if (paragraph.isPlaceholder()) {
                    builder.append(placeholders.get(paragraph.getText()));
                } else {
                    builder.append(message.substring(paragraph.startsWith, paragraph.endsWith).replace("/n", "\n"));
                }
            }

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
                List<TextParagraph> splitedTexts = splitIntoParagraphs(message, placeholders);
                for (TextParagraph paragraph : splitedTexts) {
                    if (paragraph.isPlaceholder()) {
                        builder.append(placeholders.get(paragraph.getText()));
                    } else {
                        builder.append(sender != null ? 
                            toPlaceholderAPIResult(message.substring(paragraph.startsWith, paragraph.endsWith), sender).replace("/n", "\n") : 
                            message.substring(paragraph.startsWith, paragraph.endsWith).replace("/n", "\n"));
                    }
                }
            
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
    
    public static List<BaseComponent> createJSONMessage(CommandSender sender, String message, Map<String, BaseComponent> baseComponents) {
        return createJSONMessage(sender, message, baseComponents, true);
    }
    
    public static List<BaseComponent> createJSONMessage(CommandSender sender, String message, Map<String, BaseComponent> baseComponents, boolean toColor) {
        List<TextParagraph> splitedTexts = splitIntoComponentParagraphs(message, baseComponents);
        List<BaseComponent> components = new ArrayList<>();
        splitedTexts.stream().forEach(paragraph -> {
            if (paragraph.isPlaceholder()) {
                components.add(paragraph.getComponent());
            } else {
                components.add(new TextComponent(toPlaceholderAPIResult(message.substring(paragraph.startsWith, paragraph.endsWith), sender).replace("/n", "\n")));
            }
        });
        return components;
    }
    
    public static List<TextParagraph> splitIntoParagraphs(String message, Map<String, String> placeholders) {
        List<TextParagraph> resolvedParagraphs = new ArrayList<>();
        resolvedParagraphs.add(new TextParagraph(0, message.length(), message));
        placeholders.keySet().stream().filter(placeholder -> placeholder != null && placeholders.get(placeholder) != null).forEach(placeholder -> {
            List<TextParagraph> array = new ArrayList<>();
            resolvedParagraphs.stream().forEach(paragraph -> {
                String text = paragraph.getText().toLowerCase();
                String placeholderName = placeholder.toLowerCase();
                if (text.contains(placeholderName)) {
                    String[] splitText = text.split(escape(placeholderName), -1);
                    int last = paragraph.getStartsWith();
                    for (String subText : splitText) {
                        int next = last + subText.length();
                        if (last != next) {
                            TextParagraph subParagraph = new TextParagraph(last, next, subText);
                            last = last + subText.length();
                            array.add(subParagraph);
                        }
                        if (last < paragraph.getEndsWith()) {
                            TextParagraph insertPlaceholder = new TextParagraph(last, last + placeholder.length(), placeholder, placeholder);
                            last = last + placeholder.length();
                            array.add(insertPlaceholder);
                        }
                    }
                } else {
                    array.add(paragraph);
                }
            });
            resolvedParagraphs.clear();
            resolvedParagraphs.addAll(array);
        });
        return resolvedParagraphs;
    }
    
    public static List<TextParagraph> splitIntoComponentParagraphs(String message, Map<String, BaseComponent> baseComponents) {
        List<TextParagraph> splitedTexts = new ArrayList<>();
        splitedTexts.add(new TextParagraph(0, message.length(), new TextComponent(message)));
        baseComponents.keySet().stream().filter(placeholder -> placeholder != null && baseComponents.get(placeholder) != null).map(placeholder -> {
            List<TextParagraph> newArray = new ArrayList<>();
            splitedTexts.stream().forEach(textParagraphs -> {
                String message_lowerCase = textParagraphs.getComponent().toPlainText().toLowerCase();
                String placeholder_lowerCase = placeholder.toLowerCase();
                if (message_lowerCase.contains(placeholder_lowerCase)) {
                    String[] splitText = message_lowerCase.split(escape(placeholder_lowerCase), -1);
                    int last = textParagraphs.startsWith;
                    for (String paragraph : splitText) {
                        int next = last + paragraph.length();
                        if (last != next) {
                            TextParagraph subParagraph = new TextParagraph(last, next, new TextComponent(paragraph));
                            last = last + paragraph.length();
                            newArray.add(subParagraph);
                        }
                        if (last < textParagraphs.endsWith) {
                            TextParagraph insertComponent = new TextParagraph(last, last + placeholder.length(), baseComponents.get(placeholder), placeholder);
                            last = last + placeholder.length();
                            newArray.add(insertComponent);
                        }
                    }
                } else {
                    newArray.add(textParagraphs);
                }
            });
            return newArray;
        }).forEach(newArray -> {
            splitedTexts.clear();
            splitedTexts.addAll(newArray);
        });
        return splitedTexts;
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
    
    public static class TextParagraph {
        
        @Getter
        private final int startsWith;
        @Getter
        private final int endsWith;
        @Getter
        private final BaseComponent component;
        @Getter
        private final String text;
        @Getter
        private final String placeholder;
        
        public TextParagraph(int startsWith, int endsWith, BaseComponent component, String placeholder) {
            this.startsWith = startsWith;
            this.endsWith = endsWith;
            this.component = component;
            this.placeholder = placeholder;
            this.text = component.toPlainText();
        }
        
        public TextParagraph(int startsWith, int endsWith, BaseComponent component) {
            this.startsWith = startsWith;
            this.endsWith = endsWith;
            this.component = component;
            this.placeholder = null;
            this.text = component.toPlainText();
        }
        
        public TextParagraph(int startsWith, int endsWith, String text, String placeholder) {
            this.startsWith = startsWith;
            this.endsWith = endsWith;
            this.component = null;
            this.placeholder = placeholder;
            this.text = text;
        }
        
        public TextParagraph(int startsWith, int endsWith, String text) {
            this.startsWith = startsWith;
            this.endsWith = endsWith;
            this.component = null;
            this.placeholder = null;
            this.text = text;
        }
        
        public boolean isPlaceholder() {
            return placeholder != null;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
