package studio.trc.bukkit.litecommandeditor.module.condition;

import java.util.Map;

import lombok.Getter;
import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;

import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConditionType;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemInfo;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ItemCondition
    extends CommandCondition
{
    @Getter
    private final CommandConditionType conditionType = CommandConditionType.ITEM;
    @Getter
    private final Function function;
    
    public ItemCondition(Function function, String expression) {
        super(keyWordsReplace(expression));
        this.function = function;
    }

    //Format: "HasItem:[ItemName]:[Amount]:[TargetPlayer]" or "HasItem:[ItemName-Data]:[Amount]:[TargetPlayer]"
    @Override
    public boolean matchCondition(CommandConfiguration commandConfig, String configPath, CommandSender sender, Map<String, String> placeholders) {
        String[] parameters;
        if (getExpression().startsWith("!")) {
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression().substring(1), sender, placeholders);
        } else {
            parameters = LiteCommandEditorUtils.getConditionParameters(getExpression(), sender, placeholders);
        }
        int amount;
        String targetPlayer;
        if (parameters.length > 1 && LiteCommandEditorUtils.isInteger(parameters[1])) {
            amount = Integer.valueOf(parameters[1]);
        } else {
            amount = -1;
        }
        if (parameters.length > 2) {
            targetPlayer = parameters[2];
        } else {
            targetPlayer = null;
        }
        if (parameters.length > 4) {
            placeholders.put("{conditionType}", MessageUtil.getMessage(ConfigurationType.MESSAGES, "Condition-Messages.Conditions-Type.Item"));
            placeholders.put("{fileName}", commandConfig.getFileName());
            placeholders.put("{configPath}", configPath);
            placeholders.put("{expression}", getExpression().startsWith("!") ? "!HasItem:" + getExpression().substring(1) : "HasItem:" + getExpression());
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), ConfigurationType.MESSAGES.getRobustConfig(), "Condition-Messages.Incorrect-Parameters", placeholders);
            return getExpression().startsWith("!");
        }
        boolean inItemBuilders = commandConfig.getItemCollection().getItemBuilders().stream().anyMatch(item -> {
            if (Material.getMaterial(parameters[0]) != null) {
                return new ItemInfo(new ItemStack(Material.getMaterial(parameters[0]))).hasItem(sender, placeholders, targetPlayer, amount);
            } else if (item.getItemName().equals(parameters[0])) {
                return new ItemInfo(item).hasItem(sender, placeholders, targetPlayer, amount);
            } else if (parameters[0].contains("-")) {
                String[] details = parameters[0].toUpperCase().split("-");
                ItemStack target = new ItemStack(Material.getMaterial(details[0]));
                if (LiteCommandEditorUtils.isByte(details[1])) {
                    target.getData().setData(Byte.valueOf(details[1]));
                }
                return new ItemInfo(target).hasItem(sender, placeholders, targetPlayer, amount);
            }
            return false;
        });
        boolean inItemStacks = commandConfig.getItemCollection().getItemStacks().containsKey(parameters[0]);
        return getExpression().startsWith("!") ? inItemBuilders || inItemStacks : !inItemBuilders && !inItemStacks;
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("hasitem:")) {
            expression = expression.substring("hasitem:".length());
        } else if (expression.toLowerCase().startsWith("!hasitem:")) {
            expression = "!" + expression.substring("!hasitem:".length());
        }
        return expression.replace(" ", "");
    }
}
