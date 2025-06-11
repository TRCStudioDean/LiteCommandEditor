package studio.trc.bukkit.litecommandeditor.module.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.module.tab.TabRecipe;

public class SoundRecipe
    extends TabRecipe
{
    public SoundRecipe(String expression) {
        super(keyWordsReplace(expression));
    }
    
    @Override
    public List<String> getRecipes(CommandSender sender, Map<String, String> placeholders) {
        List<String> names = new ArrayList<>();
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(getExpression());
        } catch (Throwable t) {}
        for (Sound sound : Sound.values()) {
            if (getExpression().equalsIgnoreCase("[all]")) {
                names.add(sound.name());
            } else if (pattern != null) {
                Matcher matcher = pattern.matcher(sound.name());
                if (matcher.find()) {
                    names.add(sound.name());
                }
            }
        }
        return names;
    }
    
    private static String keyWordsReplace(String expression) {
        if (expression.toLowerCase().startsWith("sounds:")) {
            expression = expression.substring("sounds:".length());
        }
        return expression.replace(" ", "");
    }
}
