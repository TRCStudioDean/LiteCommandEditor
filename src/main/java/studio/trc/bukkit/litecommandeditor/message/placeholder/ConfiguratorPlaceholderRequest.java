package studio.trc.bukkit.litecommandeditor.message.placeholder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.function.Configurator;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ConfiguratorPlaceholderRequest 
{
    @Getter
    private static final Map<String, String> cachePlaceholders = new HashMap<>();
    
    // {[Table]:[Path]} or {[Table]:[Path]:[Index]} or {[table]_jsondata}
    public static void configPlaceholderRequest(Map<String, String> placeholders, String placeholder) {
        if (placeholders.containsKey(placeholder)) return;
        if (cachePlaceholders.containsKey(placeholder)) {
            placeholders.put(placeholder, cachePlaceholders.get(placeholder));
            return;
        }
        String[] content = MessageUtil.splitStringBySymbol(placeholder.substring(1, placeholder.length() - 1), ':');
        if (content.length < 2) {
            String[] sections = placeholder.substring(1, placeholder.length() - 1).split("_", 2);
            if (sections.length == 2 && Configurator.getTables().containsKey(sections[0]) && sections[1].equalsIgnoreCase("jsondata")) {
                placeholders.put(placeholder, Configurator.getTables().get(sections[0]).toJSONString());
            }
            return;
        }
        String table = content[0];
        String path = content[1];
        if (Configurator.getTables().containsKey(table) && Configurator.getTables().get(table).containsKey(path)) {
            Object object = Configurator.getTables().get(table).get(path);
            if (object instanceof List && content.length >= 3 && LiteCommandEditorUtils.isInteger(content[2]) && Integer.valueOf(content[2]) - 1 < ((List) object).size()) {
                placeholders.put(placeholder, ((List) object).get(Integer.valueOf(content[2]) - 1).toString());
            } else if (object != null) {
                placeholders.put(placeholder, Configurator.getTables().get(table).getString(path));
            }
        }
    }
    
    // %lce_configurator:[Table]:[Path]% or %lce_configurator:[Table]:[Path]:[Index]% or %lce_configurator_[table]%
    public static String configPlaceholderRequestPAPI(String identifier) {
        String[] content = MessageUtil.splitStringBySymbol(identifier, ':');
        if (content.length < 3) {
            if (content.length == 2) {
                String[] sections = identifier.split("_", 2);
                if (sections.length == 2 && Configurator.getTables().containsKey(sections[0]) && sections[1].equalsIgnoreCase("jsondata")) {
                    return Configurator.getTables().get(sections[0]).toJSONString();
                }
            }
            return null;
        }
        String table = content[1];
        String path = content[2];
        if (Configurator.getTables().containsKey(table) && Configurator.getTables().get(table).containsKey(path)) {
            Object object = Configurator.getTables().get(table).get(path);
            if (object instanceof List && content.length >= 4 && LiteCommandEditorUtils.isInteger(content[3]) && Integer.valueOf(content[3]) - 1 < ((List) object).size()) {
                return ((List) object).get(Integer.valueOf(content[3]) - 1).toString();
            } else if (object != null) {
                return object.toString();
            }
        }
        return null;
    }
}
