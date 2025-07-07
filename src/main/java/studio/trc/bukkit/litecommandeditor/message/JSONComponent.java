package studio.trc.bukkit.litecommandeditor.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.util.AdventureUtils;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public class JSONComponent
{
    @Getter
    private final YamlConfiguration config;
    @Getter
    private final String fileName;
    @Getter
    private final String configPath;
    
    private BaseComponent bungeeComponent = null;
    private Object adventureComponent = null;
    private String text = null;

    public JSONComponent(String fileName, YamlConfiguration config, String configPath) {
        this.fileName = fileName;
        this.config = config;
        this.configPath = configPath;
    }
    
    public BaseComponent getBungeeComponent() {
        if (bungeeComponent == null) {
            net.md_5.bungee.api.chat.HoverEvent hoverEvent = null;
            net.md_5.bungee.api.chat.ClickEvent clickEvent = null;
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
                    hoverEvent = new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.valueOf(config.getString(configPath + ".HoverEvent.Action").toUpperCase()), hoverText.toArray(new BaseComponent[0]));
                }
                if (config.contains(configPath + ".ClickEvent")) {
                    clickEvent = new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(config.getString(configPath + ".ClickEvent.Action").toUpperCase()), MessageUtil.doBasicProcessing(config.getString(configPath + ".ClickEvent.Value")));
                }
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{component}", configPath);
                placeholders.put("{file}", fileName);
                LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
                ex.printStackTrace();
            }
            text = config.getString(configPath + ".Text");
            bungeeComponent = new TextComponent(MessageUtil.doBasicProcessing(text));
            if (hoverEvent != null) bungeeComponent.setHoverEvent(hoverEvent);
            if (clickEvent != null) bungeeComponent.setClickEvent(clickEvent);
        }
        return bungeeComponent;
    }
    
    public Object getAdventureComponent() {
        if (adventureComponent == null) {
            HoverEvent hoverEvent = null;
            ClickEvent clickEvent = null;
            try {
                if (config.contains(configPath + ".HoverEvent")) {
                    hoverEvent = AdventureUtils.showText(String.join("\n", config.getStringList(configPath + ".HoverEvent.Hover-Values").stream().map(hover -> MessageUtil.doBasicProcessing(hover)).collect(Collectors.toList())));
                }
                if (config.contains(configPath + ".ClickEvent")) {
                    clickEvent = AdventureUtils.getClickEvent(config.getString(configPath + ".ClickEvent.Action"), MessageUtil.doBasicProcessing(config.getString(configPath + ".ClickEvent.Value")));
                }
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{component}", configPath);
                placeholders.put("{file}", fileName);
                LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
                ex.printStackTrace();
            }
            text = config.getString(configPath + ".Text");
            Component component = AdventureUtils.serializeText(MessageUtil.doBasicProcessing(text));
            if (hoverEvent != null) component = AdventureUtils.setHoverEvent(component, hoverEvent);
            if (clickEvent != null) component = AdventureUtils.setClickEvent(component, clickEvent);
            adventureComponent = component;
        }
        return adventureComponent;
    }
    
    public BaseComponent getBungeeComponent(Map<String, String> placeholders) {
        try {
            net.md_5.bungee.api.chat.HoverEvent hoverEvent = null;
            net.md_5.bungee.api.chat.ClickEvent clickEvent = null;
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
                hoverEvent = new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.valueOf(config.getString(configPath + ".HoverEvent.Action").toUpperCase()), hoverText.toArray(new BaseComponent[0]));
            }
            if (config.contains(configPath + ".ClickEvent")) {
                clickEvent = new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(config.getString(configPath + ".ClickEvent.Action").toUpperCase()), MessageUtil.replacePlaceholders(config.getString(configPath + ".ClickEvent.Value"), placeholders));
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
    
    public Object getAdventureComponent(Map<String, String> placeholders) {
        try {
            HoverEvent hoverEvent = null;
            ClickEvent clickEvent = null;
            try {
                if (config.contains(configPath + ".HoverEvent")) {
                    hoverEvent = AdventureUtils.showText(String.join("\n", config.getStringList(configPath + ".HoverEvent.Hover-Values").stream().map(hover -> MessageUtil.replacePlaceholders(hover, placeholders)).collect(Collectors.toList())));
                }
                if (config.contains(configPath + ".ClickEvent")) {
                    clickEvent = AdventureUtils.getClickEvent(config.getString(configPath + ".ClickEvent.Action"), MessageUtil.replacePlaceholders(config.getString(configPath + ".ClickEvent.Value"), placeholders));
                }
            } catch (Exception ex) {
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{component}", configPath);
                placeholders.put("{file}", fileName);
                LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
                ex.printStackTrace();
            }
            Component component = AdventureUtils.serializeText(MessageUtil.doBasicProcessing(config.getString(configPath + ".Text")));
            if (hoverEvent != null) component = AdventureUtils.setHoverEvent(component, hoverEvent);
            if (clickEvent != null) component = AdventureUtils.setClickEvent(component, clickEvent);
            return component;
        } catch (Exception ex) {
            placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
            placeholders.put("{component}", configPath);
            placeholders.put("{file}", fileName);
            LiteCommandEditorProperties.sendOperationMessage("LoadingJSONComponentFailed", placeholders);
            ex.printStackTrace();
        }
        return null;
    }
}
