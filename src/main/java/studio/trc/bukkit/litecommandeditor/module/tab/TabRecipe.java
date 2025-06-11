package studio.trc.bukkit.litecommandeditor.module.tab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.event.TabRecipesResetEvent;
import studio.trc.bukkit.litecommandeditor.module.recipe.*;

public abstract class TabRecipe
{
    public static final Map<String, Class<? extends TabRecipe>> recipes = new HashMap<>();
    @Getter
    private final String expression;

    public TabRecipe(String expression) {
        this.expression = expression;
    }
    
    public abstract List<String> getRecipes(CommandSender sender, Map<String, String> placeholders);
    
    public static void resetTabRecipes() {
        recipes.clear();
        recipes.put("text:", TextRecipe.class);
        recipes.put("players:", PlayerRecipe.class);
        recipes.put("items:", ItemRecipe.class);
        recipes.put("sounds:", SoundRecipe.class);
        recipes.put("command:", CommandRecipe.class);
        Bukkit.getPluginManager().callEvent(new TabRecipesResetEvent(recipes));
    }
    
    public static TabRecipe getTabRecipe(String expression) {
        String conditionName = recipes.keySet().stream().filter(prefix -> expression.toLowerCase().startsWith(prefix.toLowerCase())).findFirst().orElse(null);
        if  (conditionName != null) {
            Class<? extends TabRecipe> condition = recipes.get(conditionName);
            try {
                return (TabRecipe) condition.getConstructor(String.class).newInstance(expression);
            } catch (Exception ex) {
                return new TextRecipe(expression);
            }
        } else {
            return new TextRecipe(expression);
        }
    }
}
