package studio.trc.bukkit.litecommandeditor.module.tool;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ItemBuilder
{
    @Getter
    private final ConfigurationSection config;
    @Getter
    private final String fileName;
    @Getter
    private final String configPath;
    @Getter
    private final String itemName;
    
    public ItemBuilder(ConfigurationSection config, String fileName, String configPath, String itemName) {
        this.config = config;
        this.fileName = fileName;
        this.configPath = configPath;
        this.itemName = itemName;
    }
    
    /**
     * Build item;
     * @param sender command sender.
     * @param placeholders
     * @return ItemStack instance.
     */
    public ItemStack build(CommandSender sender, Map<String, String> placeholders) {
        String name = config.getString("Item");
        Material material = Material.valueOf(name.toUpperCase());
        ItemStack item;
        if (material != null) {
            if (config.get("Data") == null) {
                item = new ItemStack(material);
            } else {
                item = new ItemStack(material, config.getInt("Amount", 1), Short.valueOf(config.getString("Data")));
            }
        } else {
            placeholders.put("{item}", name);
            placeholders.put("{fileName}", fileName);
            placeholders.put("{configPath}", configPath + ".Item");
            LiteCommandEditorProperties.sendOperationMessage("InvalidItem", placeholders);
            return null;
        }
        setHead(item, sender, placeholders);
        setName(item, sender, placeholders);
        setLores(item, sender, placeholders);
        setAmount(item, sender, placeholders);
        setEnchantments(item);
        setUnbreakable(item);
        setOptions(item);
        setHeadTextures(item, sender, placeholders);
        setCustomModelData(item);
        setItemModel(item);
        return item;
    }
    
    /**
     * Set item name.
     * @param item item
     * @param sender command sender.
     * @param placeholders
     * @return 
     */
    public ItemBuilder setName(ItemStack item, CommandSender sender, Map<String, String> placeholders) {
        ItemMeta im = item.getItemMeta();
        String name = MessageUtil.replacePlaceholders(sender, config.getString("Name"), placeholders);
        if (name == null) return this;
        im.setDisplayName(name);
        item.setItemMeta(im);
        return this;
    }
    
    /**
     * Set item lores.
     * @param item item
     * @param sender command sender.
     * @param placeholders
     * @return 
     */
    public ItemBuilder setLores(ItemStack item, CommandSender sender, Map<String, String> placeholders) {
        ItemMeta im = item.getItemMeta();
        if (im == null || config.get("Lores") == null) return this;
        List<String> lores = config.getStringList("Lores").stream().map(lore -> MessageUtil.replacePlaceholders(sender, lore, placeholders)).collect(Collectors.toList());
        if (lores.isEmpty()) return this;
        im.setLore(lores);
        item.setItemMeta(im);
        return this;
    }
    
    /**
     * Set item amount.
     * @param item item
     * @param sender command sender.
     * @param placeholders
     * @return 
     */
    public ItemBuilder setAmount(ItemStack item, CommandSender sender, Map<String, String> placeholders) {
        String stringAmount = MessageUtil.replacePlaceholders(sender, config.getString("Amount"), placeholders);
        if (stringAmount == null) return this;
        if (LiteCommandEditorUtils.isInteger(stringAmount)) {
            item.setAmount(Integer.valueOf(stringAmount));
        } else {
            placeholders.put("{number}", stringAmount);
            placeholders.put("{fileName}", fileName);
            placeholders.put("{configPath}", configPath + ".Amount");
            LiteCommandEditorProperties.sendOperationMessage("InvalidNumber", placeholders);
            item.setAmount(1);
        }
        return this;
    }
    
    /**
     * Set item custom model data.
     * @param item item
     * @return 
     */
    public ItemBuilder setCustomModelData(ItemStack item) {
        if (Bukkit.getBukkitVersion().startsWith("1.7") ||
            Bukkit.getBukkitVersion().startsWith("1.8") ||
            Bukkit.getBukkitVersion().startsWith("1.9") ||
            Bukkit.getBukkitVersion().startsWith("1.10") ||
            Bukkit.getBukkitVersion().startsWith("1.11") ||
            Bukkit.getBukkitVersion().startsWith("1.12") || 
            Bukkit.getBukkitVersion().startsWith("1.13")) return this;
        ItemMeta im = item.getItemMeta();
        String value = config.getString("Custom-Model-Data");
        if (im == null || value == null) return this;
        if (LiteCommandEditorUtils.isInteger(value)) {
            im.setCustomModelData(Integer.valueOf(value));
        } else {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{number}", value);
            placeholders.put("{fileName}", fileName);
            placeholders.put("{configPath}", configPath + ".Custom-Model-Data");
            LiteCommandEditorProperties.sendOperationMessage("InvalidNumber", placeholders);
        }
        item.setItemMeta(im);
        return this;
    }
    
    /**
     * Set item's model.
     * @param item item
     * @return 
     */
    public ItemBuilder setItemModel(ItemStack item) {
        if (Bukkit.getBukkitVersion().startsWith("1.7") ||
            Bukkit.getBukkitVersion().startsWith("1.8") ||
            Bukkit.getBukkitVersion().startsWith("1.9") ||
            Bukkit.getBukkitVersion().startsWith("1.10") ||
            Bukkit.getBukkitVersion().startsWith("1.11") ||
            Bukkit.getBukkitVersion().startsWith("1.12") || 
            Bukkit.getBukkitVersion().startsWith("1.13") || 
            Bukkit.getBukkitVersion().startsWith("1.14") || 
            Bukkit.getBukkitVersion().startsWith("1.15") || 
            Bukkit.getBukkitVersion().startsWith("1.16") || 
            Bukkit.getBukkitVersion().startsWith("1.17") || 
            Bukkit.getBukkitVersion().startsWith("1.18") || 
            Bukkit.getBukkitVersion().startsWith("1.19")) {
            return this;
        }
        ItemMeta im = item.getItemMeta();
        String name = config.getString("Item-Model");
        if (im == null || name == null) return this;
        String[] modelInfo = name.split(":");
        try {
            Method method = im.getClass().getMethod("setItemModel", NamespacedKey.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            if (modelInfo.length == 2) {
                method.invoke(im, new NamespacedKey(modelInfo[0], modelInfo[1]));
            } else {
                method.invoke(im, NamespacedKey.minecraft(modelInfo[0]));
            }
        } catch (Exception ex) {}
        return this;
    }
    
    /**
     * Set item enchantments.
     * @param item item
     * @return 
     */
    public ItemBuilder setEnchantments(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        List<String> enchantmentNames = config.getStringList("Enchantments");
        if (im == null || enchantmentNames == null || enchantmentNames.isEmpty()) return this;
        for (String section : enchantmentNames) {
            try {
                String[] data = section.split(":");
                boolean invalid = true;
                if (!Bukkit.getBukkitVersion().startsWith("1.7") && !Bukkit.getBukkitVersion().startsWith("1.8") && !Bukkit.getBukkitVersion().startsWith("1.9") && !Bukkit.getBukkitVersion().startsWith("1.10") && !Bukkit.getBukkitVersion().startsWith("1.11") && !Bukkit.getBukkitVersion().startsWith("1.12")) {
                    for (Enchantment enchant : Enchantment.values()) {
                        if (enchant.getKey().getKey().equalsIgnoreCase(data[0])) {
                            try {
                                im.addEnchant(enchant, Integer.valueOf(data[1]), true);
                                invalid = false;
                                break;
                            } catch (Exception ex) {
                                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                                placeholders.put("{fileName}", fileName);
                                placeholders.put("{configPath}", configPath + "." + section);
                                LiteCommandEditorProperties.sendOperationMessage("InvalidEnchantmentSetting", placeholders);
                            }
                        }
                    }
                } else {
                    for (Enchantment enchant : Enchantment.values()) {
                        if (enchant.getName().equalsIgnoreCase(data[0])) {
                            try {
                                im.addEnchant(enchant, Integer.valueOf(data[1]), true);
                                invalid = false;
                                break;
                            } catch (Exception ex) {
                                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                                placeholders.put("{fileName}", fileName);
                                placeholders.put("{configPath}", configPath + "." + section);
                                LiteCommandEditorProperties.sendOperationMessage("InvalidEnchantmentSetting", placeholders);
                            }
                        }
                    }
                }
                if (invalid) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{enchantment}", data[0]);
                    placeholders.put("{fileName}", fileName);
                    placeholders.put("{configPath}", configPath + ".Enchantments." + section);
                    LiteCommandEditorProperties.sendOperationMessage("InvalidEnchantment", placeholders);
                }
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{fileName}", fileName);
                placeholders.put("{configPath}", configPath + ".Enchantments." + section);
                LiteCommandEditorProperties.sendOperationMessage("InvalidEnchantmentSetting", placeholders);
            }
        }
        item.setItemMeta(im);
        return this;
    }
    
    /**
     * Set item options (flags).
     * @param item item
     * @return 
     */
    public ItemBuilder setOptions(ItemStack item) {
        if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8")) return this;
        ItemMeta im = item.getItemMeta();
        List<String> options = config.getStringList("Options");
        if (im == null || options == null || options.isEmpty()) return this;
        options.stream().forEach(option -> {
            try {
                ItemFlag flag = ItemFlag.valueOf(option.toUpperCase());
                if (flag == null) {
                    Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                    placeholders.put("{option}", option);
                    placeholders.put("{fileName}", fileName);
                    placeholders.put("{configPath}", configPath + ".Options." + option);
                    LiteCommandEditorProperties.sendOperationMessage("InvalidOption", placeholders);
                }
                im.addItemFlags(flag);
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{fileName}", fileName);
                placeholders.put("{configPath}", configPath + ".Options." + option);
                LiteCommandEditorProperties.sendOperationMessage("InvalidOptionSetting", placeholders);
            }
        });
        item.setItemMeta(im);
        return this;
    }
    
    /**
     * Set item head textures.
     * @param item item
     * @param sender command sender.
     * @param placeholders
     * @return 
     */
    public ItemBuilder setHeadTextures(ItemStack item, CommandSender sender, Map<String, String> placeholders) {
        if (Bukkit.getBukkitVersion().startsWith("1.7")) return this;
        ItemMeta im = item.getItemMeta();
        String textures = MessageUtil.replacePlaceholders(sender, config.getString("Head-Textures"), placeholders);
        if (im == null || textures == null) return this;
        if (item.getType().equals(Material.valueOf("SKULL_ITEM")) && item.getData().getData() == 3) {
            SkullMeta skull = (SkullMeta) im;
            GameProfile profile = new GameProfile(UUID.randomUUID(), "Skull");
            profile.getProperties().put("textures", new Property("textures", textures));
            Field profileField;
            try {
                profileField = skull.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skull, profile);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
            item.setItemMeta(skull);
        }
        return this;
    }
    
    @Getter
    private static final Map<String, ItemMeta> headOwnerCache = new HashMap<>();
    
    /**
     * Set item head owner.
     * @param item item
     * @param sender command sender.
     * @param placeholders
     * @return 
     */
    public ItemBuilder setHead(ItemStack item, CommandSender sender, Map<String, String> placeholders) {
        if (Bukkit.getBukkitVersion().startsWith("1.7")) return this;
        String name = MessageUtil.replacePlaceholders(sender, config.getString("Head-Owner"), placeholders);
        if (name == null) return this;
        if (!Bukkit.getBukkitVersion().startsWith("1.8") && !Bukkit.getBukkitVersion().startsWith("1.9") && !Bukkit.getBukkitVersion().startsWith("1.10") && !Bukkit.getBukkitVersion().startsWith("1.11") && !Bukkit.getBukkitVersion().startsWith("1.12")) {
            if (item.getType().equals(Material.PLAYER_HEAD)) {
                if (headOwnerCache.containsKey(name)) {
                    item.setItemMeta(headOwnerCache.get(name));
                } else {
                    SkullMeta sm = (SkullMeta) item.getItemMeta();
                    sm.setOwningPlayer(Bukkit.getOfflinePlayer(name));
                    headOwnerCache.put(name, sm);
                    item.setItemMeta(sm);
                }
            }
        } else {
            if (item.getType().equals(Material.valueOf("SKULL_ITEM")) && item.getData().getData() == 3) {
                if (headOwnerCache.containsKey(name)) {
                    item.setItemMeta(headOwnerCache.get(name));
                } else {
                    SkullMeta sm = (SkullMeta) item.getItemMeta();
                    sm.setOwner(name);
                    headOwnerCache.put(name, sm);
                    item.setItemMeta(sm);
                }
            }
        }
        return this;
    }
    
    public ItemBuilder setUnbreakable(ItemStack item) {
        if (Bukkit.getBukkitVersion().startsWith("1.7") ||
            Bukkit.getBukkitVersion().startsWith("1.8") ||
            Bukkit.getBukkitVersion().startsWith("1.9") ||
            Bukkit.getBukkitVersion().startsWith("1.10")) return this;
        ItemMeta im = item.getItemMeta();
        im.setUnbreakable(config.getBoolean("Unbreakable"));
        item.setItemMeta(im);
        return this;
    }
}
