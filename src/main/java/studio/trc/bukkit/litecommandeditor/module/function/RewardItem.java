package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemInfo;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class RewardItem
    implements CommandFunctionTask
{
    @Getter
    private final String expression;
    @Getter
    private final String configPath;
    @Getter
    private final CommandFunction function;
    @Getter
    private final String identifier = "RewardItem";
    
    public RewardItem(CommandFunction function, String expression, String configPath) {
        this.function = function;
        this.expression = expression;
        this.configPath = configPath;
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        String[] parameters = MessageUtil.splitStringBySymbol(MessageUtil.replacePlaceholders(sender, expression, placeholders), ':');
        String target;
        int amount;
        if (parameters.length > 1) {
            if (LiteCommandEditorUtils.isInteger(parameters[1])) {
                amount = Integer.valueOf(parameters[1]);
            } else if (parameters[1].contains("-")) {
                String[] details = parameters[1].split("-");
                if (details.length > 1 && LiteCommandEditorUtils.isInteger(details[0]) && LiteCommandEditorUtils.isInteger(details[1])) {
                    amount = LiteCommandEditorUtils.getRandomNumber(Integer.valueOf(details[0]), Integer.valueOf(details[1]));
                } else {
                    amount = -1;
                }
            } else {
                amount = -1;
            }
        } else {
            amount = -1;
        }
        if (parameters.length > 2) {
            target = parameters[2];
        } else {
            target = null;
        }
        ItemInfo item = function.getCommandConfig().getItemCollection().getItem(parameters[0]);
        if (item != null) {
            item.give(sender, placeholders, target, amount);
        }
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Expression=" + expression;
    }
    
    public static RewardItem build(CommandFunction function, Map map, String configPath) {
        if (map.get("Reward-Item") != null) {
            return new RewardItem(function, map.get("Reward-Item").toString(), configPath);
        }
        return null;
    }
    
    public static List<RewardItem> build(CommandFunction function, List<String> itemList, String configPath) {
        return itemList.stream().map(item -> new RewardItem(function, item, configPath)).collect(Collectors.toList());
    }
}
