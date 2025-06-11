package studio.trc.bukkit.litecommandeditor.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public class JSONComponent
{
    @Getter
    private final BaseComponent component;
    @Getter
    private final YamlConfiguration config;
    @Getter
    private final String fileName;
    @Getter
    private final String configPath;
    
    public JSONComponent(String fileName, YamlConfiguration config, String configPath) {
        this.fileName = fileName;
        this.config = config;
        this.configPath = configPath;
        HoverEvent hoverEvent = null;
        ClickEvent clickEvent = null;
        try {
            if (config.contains(configPath + ".HoverEvent")) {
                List<BaseComponent> hoverText = new ArrayList<>();
                int end = 0;
                List<String> array = config.getStringList(configPath + ".HoverEvent.Hover-Values");
                for (String hover : array) {
                    end++;
                    hoverText.add(new TextComponent(MessageUtil.doBasicProcessing(hover)));
                    if (end != array.size()) {
                        hoverText.add(new TextComponent("\n"));
                    }
                }
                hoverEvent = new HoverEvent(HoverEvent.Action.valueOf(config.getString(configPath + ".HoverEvent.Action").toUpperCase()), hoverText.toArray(new BaseComponent[0]));
            }
            if (config.contains(configPath + ".ClickEvent")) {
                clickEvent = new ClickEvent(ClickEvent.Action.valueOf(config.getString(configPath + ".ClickEvent.Action").toUpperCase()), MessageUtil.doBasicProcessing(config.getString(configPath + ".ClickEvent.Value")));
            }
        } catch (Exception ex) {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
            placeholders.put("{component}", configPath);
            placeholders.put("{file}", fileName);
            LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
            ex.printStackTrace();
        }
        component = new TextComponent(MessageUtil.doBasicProcessing(config.getString(configPath + ".Text")));
        if (hoverEvent != null) component.setHoverEvent(hoverEvent);
        if (clickEvent != null) component.setClickEvent(clickEvent);
    }
    
    public BaseComponent getComponent(Map<String, String> placeholders) {
        try {
            HoverEvent hoverEvent = null;
            ClickEvent clickEvent = null;
            if (config.contains(configPath + ".HoverEvent")) {
                List<BaseComponent> hoverText = new ArrayList<>();
                int end = 0;
                List<String> array = config.getStringList(configPath + ".HoverEvent.Hover-Values");
                for (String hover : array) {
                    end++;
                    hoverText.add(new TextComponent(MessageUtil.replacePlaceholders(hover, placeholders)));
                    if (end != array.size()) {
                        hoverText.add(new TextComponent("\n"));
                    }
                }
                hoverEvent = new HoverEvent(HoverEvent.Action.valueOf(config.getString(configPath + ".HoverEvent.Action").toUpperCase()), hoverText.toArray(new BaseComponent[0]));
            }
            if (config.contains(configPath + ".ClickEvent")) {
                clickEvent = new ClickEvent(ClickEvent.Action.valueOf(config.getString(configPath + ".ClickEvent.Action").toUpperCase()), MessageUtil.replacePlaceholders(config.getString(configPath + ".ClickEvent.Value"), placeholders));
            }
            BaseComponent processedComponent = new TextComponent(MessageUtil.replacePlaceholders(config.getString(configPath + ".Text"), placeholders));
            if (hoverEvent != null) processedComponent.setHoverEvent(hoverEvent);
            if (clickEvent != null) processedComponent.setClickEvent(clickEvent);
            return processedComponent;
        } catch (Exception ex) {
            placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
            placeholders.put("{component}", configPath);
            placeholders.put("{file}", fileName);
            LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
            ex.printStackTrace();
            return null;
        }
    }
}
