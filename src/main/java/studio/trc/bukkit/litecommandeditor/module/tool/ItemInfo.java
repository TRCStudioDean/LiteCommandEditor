package studio.trc.bukkit.litecommandeditor.module.tool;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ItemInfo
{
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final ItemBuilder builder;
    
    public ItemInfo(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.builder = null;
    }
    
    public ItemInfo(ItemBuilder builder) {
        this.itemStack = null;
        this.builder = builder;
    }
    
    public ItemStack give(CommandSender sender, Map<String, String> placeholders, String target, int amount) {
        String targetPlayer;
        if (target == null) {
            targetPlayer = sender.getName();
        } else {
            targetPlayer = MessageUtil.replacePlaceholders(sender, target, placeholders);
        }
        Player player;
        if (LiteCommandEditorUtils.isUUID(targetPlayer)) {
            player = Bukkit.getPlayer(UUID.fromString(targetPlayer));
        } else {
            player = Bukkit.getPlayer(targetPlayer);
        }
        return give(sender, placeholders, player, amount);
    }
    
    public ItemStack give(CommandSender sender, Map<String, String> placeholders, Player player, int amount) {
        if (player != null) {
            ItemStack result;
            if (itemStack != null) {
                result = itemStack.clone();
                if (amount != -1) {
                    result.setAmount(amount);
                }
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), result);
                } else {
                    player.getInventory().addItem(result);
                }
                return result;
            } else {
                result = builder.build(player, placeholders);
                if (amount != -1) {
                    result.setAmount(amount);
                }
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), result);
                } else {
                    player.getInventory().addItem(result);
                }
                return result;
            }
        }
        return null;
    }
    
    public ItemStack take(CommandSender sender, Map<String, String> placeholders, String target, int amount) {
        String targetPlayer;
        if (target == null) {
            targetPlayer = sender.getName();
        } else {
            targetPlayer = MessageUtil.replacePlaceholders(sender, target, placeholders);
        }
        Player player;
        if (LiteCommandEditorUtils.isUUID(targetPlayer)) {
            player = Bukkit.getPlayer(UUID.fromString(targetPlayer));
        } else {
            player = Bukkit.getPlayer(targetPlayer);
        }
        if (player != null) {
            ItemStack[] contents = player.getInventory().getContents();
            ItemStack item;
            if (itemStack != null) {
                item = itemStack.clone();
            } else {
                item = builder.build(player, placeholders);
            }
            int amountRequired = amount == -1 ? item.getAmount() : amount;
            for (int sort = 0; sort < contents.length;sort++) {
                ItemStack targetItem = contents[sort];
                if (targetItem == null) continue;
                if (targetItem.isSimilar(item)) {
                    if (amountRequired > targetItem.getAmount()) {
                        amountRequired -= targetItem.getAmount();
                        removeItemStack(targetItem);
                        player.getInventory().setItem(sort, new ItemStack(Material.AIR));
                    } else if (amountRequired == targetItem.getAmount()) {
                        amountRequired -= targetItem.getAmount();
                        removeItemStack(targetItem);
                        player.getInventory().setItem(sort, new ItemStack(Material.AIR));
                        break;
                    } else {
                        targetItem.setAmount(targetItem.getAmount() - amountRequired);
                        break;
                    }
                }
            }
            return item;
        }
        return null;
    }
    
    public boolean hasItem(CommandSender sender, Map<String, String> placeholders, String target, int amount) {
        String targetPlayer;
        if (target == null) {
            targetPlayer = sender.getName();
        } else {
            targetPlayer = MessageUtil.replacePlaceholders(sender, target, placeholders);
        }
        Player player;
        if (LiteCommandEditorUtils.isUUID(targetPlayer)) {
            player = Bukkit.getPlayer(UUID.fromString(targetPlayer));
        } else {
            player = Bukkit.getPlayer(targetPlayer);
        }
        if (player != null) {
            int total = 0;
            ItemStack item;
            if (itemStack != null) {
                item = itemStack.clone();
            } else {
                item = builder.build(player, placeholders);
            }
            for (ItemStack targetItem : player.getInventory().getContents()) {
                if (targetItem == null) continue;
                if (targetItem.isSimilar(item)) {
                    total += targetItem.getAmount();
                }
            }
            return total >= (amount == -1 ? item.getAmount() : amount);
        }
        return false;
    }
    
    private void removeItemStack(ItemStack item) {
        if (Bukkit.getBukkitVersion().startsWith("1.7") || Bukkit.getBukkitVersion().startsWith("1.8") || Bukkit.getBukkitVersion().startsWith("1.9") || Bukkit.getBukkitVersion().startsWith("1.10")) {
            try {
                Field handle = item.getClass().getDeclaredField("handle");
                handle.setAccessible(true);
                Object nmsItemStack = handle.get(item);
                nmsItemStack.getClass().getField("count").set(nmsItemStack, 0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        item.setAmount(0);
    }
    
    public ItemStack getItem(CommandSender sender, Map<String, String> placeholders) {
        if (itemStack != null) {
            return itemStack.clone();
        } else {
            return builder.build(sender, placeholders);
        }
    }
}
