package studio.trc.bukkit.litecommandeditor.module.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.module.tab.TabRecipe;

public class ItemRecipe
    extends TabRecipe
{
    public ItemRecipe(String expression) {
        super(keyWordsReplace(expression));
    }
    
    @Override
    public List<String> getRecipes(CommandSender sender, Map<String, String> placeholders) {
        List<String> names = new ArrayList<>();
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(getExpression());
        } catch (Throwable t) {}
        for (Material material : Material.values()) {
            if (getExpression().equalsIgnoreCase("[all]")) {
                names.add(material.name());
            } else if (pattern != null) {
                Matcher matcher = pattern.matcher(material.name());
                if (matcher.find()) {
                    names.add(material.name());
                }
            }
        }
        return names;
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("items:")) {
            expression = expression.substring("items:".length());
        }
        return expression.replace(" ", "");
    }
}
