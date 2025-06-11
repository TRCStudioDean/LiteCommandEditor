package studio.trc.bukkit.litecommandeditor.module.recipe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.tab.TabRecipe;

public class TextRecipe
    extends TabRecipe
{
    public TextRecipe(String expression) {
        super(keyWordsReplace(expression));
    }
    
    @Override
    public List<String> getRecipes(CommandSender sender, Map<String, String> placeholders) {
        return Arrays.stream(getExpression().split(";", -1)).map(text -> MessageUtil.replacePlaceholders(sender, text, placeholders, true, false)).collect(Collectors.toList());
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("text:")) {
            expression = expression.substring("text:".length());
        }
        return expression.replace(" ", "");
    }
}
