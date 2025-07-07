package studio.trc.bukkit.litecommandeditor.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.util.AdventureUtils;

public class MessageEditor
{
    public static List<BaseComponent> createBungeeJSONMessage(CommandSender sender, String message, Map<String, BaseComponent> baseComponents) {
        return createBungeeJSONMessage(sender, message, baseComponents, true);
    }
    
    public static List<BaseComponent> createBungeeJSONMessage(CommandSender sender, String message, Map<String, BaseComponent> baseComponents, boolean toColor) {
        List<MessageSection> sections = parse(message, baseComponents);
        List<BaseComponent> components = new ArrayList<>();
        sections.stream().forEach(section -> {
            if (section.isPlaceholder()) {
                components.add(section.getBungeeComponent());
            } else {
                components.add(new TextComponent(MessageUtil.toPlaceholderAPIResult(section.getText(), sender).replace("/n", "\n")));
            }
        });
        return components;
    }
    
    public static Object createAdventureJSONMessage(CommandSender sender, String message, Map<String, Component> components) {
        return createAdventureJSONMessage(sender, message, components, true);
    }
    
    public static Object createAdventureJSONMessage(CommandSender sender, String message, Map<String, Component> components, boolean toColor) {
        List<MessageSection> sections = parse(message, components);
        Component component = null;
        for (MessageSection section : sections) {
            if (section.isPlaceholder()) {
                component = component == null ? AdventureUtils.toComponent(section.getAdventureComponent()) : component.append(AdventureUtils.toComponent(section.getAdventureComponent()));
            } else {
                String text = MessageUtil.toPlaceholderAPIResult(section.getText(), sender).replace("/n", "\n");
                component = component == null ? AdventureUtils.serializeText(text) : component.append(AdventureUtils.serializeText(text));
            }
        }
        return component;
    }
    
    public static <T> List<MessageSection> parse(String message, Map<String, T> placeholders) {
        //把占位符原文全部转为小写（目的是下面的代码要忽略大小写匹配）
        Map<String, T> normalizedMap = new HashMap<>();
        placeholders.entrySet().stream().forEach(entry -> normalizedMap.put(entry.getKey().toLowerCase(), entry.getValue()));
        
        //按长度降序排序占位符（避免短占位符匹配长占位符的前缀）
        List<String> sortedKeys = new ArrayList<>(normalizedMap.keySet());
        sortedKeys.sort((s1, s2) -> Integer.compare(s2.length(), s1.length()));
        
        List<MessageSection> result = new ArrayList<>();
        StringBuilder currentText = new StringBuilder();
        int index = 0;
        int messageLength = message.length();
        int textStart = 0; 
        
        while (index < messageLength) {
            boolean matched = false;
            //扫描每一个占位符进行匹配
            for (String keyLower : sortedKeys) {
                int keyLength = keyLower.length();
                int endIndex = index + keyLength;
                if (endIndex > messageLength) continue;
                String paragraph = message.substring(index, endIndex);
                if (paragraph.toLowerCase().equals(keyLower)) {
                    //匹配成功先将累积的文本加入结果
                    if (currentText.length() > 0) {
                        result.add(new MessageSection(
                            currentText.toString(), 
                            null, 
                            textStart, 
                            textStart + currentText.length()
                        ));
                        currentText.setLength(0);
                    }
                    T replacement = normalizedMap.get(keyLower);
                    if (replacement instanceof BaseComponent) {
                        result.add(new MessageSection(
                            (BaseComponent) replacement,
                            paragraph,
                            index,
                            index + keyLength
                        ));
                    } else if (replacement instanceof String) {
                        result.add(new MessageSection(
                            replacement.toString(),
                            paragraph,
                            index,
                            index + keyLength
                        ));
                    } else if (replacement instanceof Component) {
                        result.add(new MessageSection(
                            (Component) replacement,
                            paragraph,
                            index,
                            index + keyLength
                        ));
                    }
                    //将指针指向占位符之后以跳过当前占位符的位置
                    index = endIndex;
                    textStart = index;
                    matched = true;
                    break;
                }
            }
            //未匹配到占位符，则进入下一个字符
            if (!matched) {
                if (currentText.length() == 0) {
                    textStart = index;
                }
                currentText.append(message.charAt(index));
                index++;
            }
        }
        
        // 处理剩余的文本
        if (currentText.length() > 0) {
            result.add(new MessageSection(
                currentText.toString(), 
                null, 
                textStart, 
                textStart + currentText.length()
            ));
        }
        return result;
    }
}
