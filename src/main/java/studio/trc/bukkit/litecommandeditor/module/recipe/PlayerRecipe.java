package studio.trc.bukkit.litecommandeditor.module.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.module.tab.TabRecipe;

public class PlayerRecipe
    extends TabRecipe
{
    public PlayerRecipe(String expression) {
        super(keyWordsReplace(expression));
    }
    
    @Override
    public List<String> getRecipes(CommandSender sender, Map<String, String> placeholders) {
        List<String> names = new ArrayList<>();
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(getExpression());
        } catch (Throwable t) {}
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getExpression().equalsIgnoreCase("[all]")) {
                names.add(player.getName());
            } else if (pattern != null) {
                Matcher matcher = pattern.matcher(player.getName());
                if (matcher.find()) {
                    names.add(player.getName());
                }
            }
        }
        return names;
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("players:")) {
            expression = expression.substring("players:".length());
        }
        return expression.replace(" ", "");
    }
}
