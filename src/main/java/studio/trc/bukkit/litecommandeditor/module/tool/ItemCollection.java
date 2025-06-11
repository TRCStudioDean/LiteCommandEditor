package studio.trc.bukkit.litecommandeditor.module.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandLoader;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ItemCollection
{
    @Getter
    private final String fileName;
    @Getter
    private final String configPath;
    @Getter
    private final YamlConfiguration config;
    @Getter
    private final CommandConfiguration commandConfig;
    
    @Getter
    private final Map<String, ItemStack> itemStacks = new HashMap<>();
    @Getter
    private final List<ItemBuilder> itemBuilders = new ArrayList<>();
    
    public ItemCollection(CommandConfiguration commandConfig, String fileName, YamlConfiguration config, String configPath) {
        this.fileName = fileName;
        this.config = config;
        this.configPath = configPath;
        this.commandConfig = commandConfig;
    }
    
    public ItemInfo getItem(String itemName) {
        if (Material.getMaterial(itemName.toUpperCase()) != null) {
            return new ItemInfo(new ItemStack(Material.getMaterial(itemName)));
        } else if (commandConfig.getItemCollection().getItemBuilders().stream().anyMatch(item -> item.getItemName().equals(itemName))) {
            return new ItemInfo(commandConfig.getItemCollection().getItemBuilders().stream().filter(item -> item.getItemName().equals(itemName)).findFirst().get());
        } else if (commandConfig.getItemCollection().getItemStacks().get(itemName) != null) {
            return new ItemInfo(commandConfig.getItemCollection().getItemStacks().get(itemName));
        } else if (itemName.contains("-")) {
            String[] details = itemName.toUpperCase().split("-");
            ItemStack item = new ItemStack(Material.getMaterial(details[0]));
            if (LiteCommandEditorUtils.isByte(details[1])) {
                item.getData().setData(Byte.valueOf(details[1]));
            }
            return new ItemInfo(item);
        }
        return null;
    }
    
    public Map<String, ItemInfo> getItems() {
        Map<String, ItemInfo> items = new HashMap<>();
        commandConfig.getItemCollection().getItemBuilders().stream().forEach(item -> items.put(item.getItemName(), new ItemInfo(item)));
        commandConfig.getItemCollection().getItemStacks().keySet().stream().forEach(item -> items.put(item, new ItemInfo(commandConfig.getItemCollection().getItemStacks().get(item))));
        return items;
    }
    
    public boolean addItem(ItemStack item, String itemName) {
        try {
            if (config.contains("Item-Collection." + itemName)) {
                return false;
            }
            config.set("Item-Collection." + itemName, item);
            config.save(new File("plugins/LiteCommandEditor/Commands", fileName));
            itemStacks.put(itemName, item);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public boolean removeItem(String itemName) {
        try {
            if (!config.contains("Item-Collection." + itemName)) {
                return false;
            }
            config.set("Item-Collection." + itemName, null);
            config.save(new File("plugins/LiteCommandEditor/Commands", fileName));
            itemStacks.remove(itemName);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public static ItemCollection build(CommandConfiguration commandConfig, String fileName, YamlConfiguration config, String configPath) {
        ItemCollection itemCollection = new ItemCollection(commandConfig, fileName, config, configPath);
        if (!config.contains(configPath)) return itemCollection;
        config.getConfigurationSection(configPath).getKeys(false).stream().forEach(item -> {
            try {
                if (config.get(configPath + "."  + item) instanceof ItemStack) {
                    itemCollection.itemStacks.put(item, config.getItemStack(configPath + "." + item));
                } else {
                    itemCollection.itemBuilders.add(new ItemBuilder(config.getConfigurationSection(configPath + "." + item), fileName, configPath + "." + item, item));
                }
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{configPath}", fileName + ": " + configPath);
                LiteCommandEditorProperties.sendOperationMessage("LoadingItemCollectionFailed", placeholders);
                ex.printStackTrace();
            }
        });
        return itemCollection;
    }
    
    public static ItemCollection getCollection(String commandName) {
        CommandConfiguration commandConfig = CommandLoader.getCache().values().stream().filter(command -> command.getCommandName().equalsIgnoreCase(commandName) || command.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(commandName))).findFirst().orElse(null);
        return commandConfig != null ? commandConfig.getItemCollection() : null;
    }
}
