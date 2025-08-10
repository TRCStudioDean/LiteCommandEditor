package studio.trc.bukkit.litecommandeditor.module.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import studio.trc.bukkit.litecommandeditor.event.TabFunctionEvent;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandExecutor;
import studio.trc.bukkit.litecommandeditor.module.tool.Function;
import studio.trc.bukkit.litecommandeditor.module.tool.Sortable;

public final class TabFunction
    extends Sortable
    implements Function
{
    /*
     Essentials
    */
    @Getter
    private final boolean breakFunction;
    @Getter
    private final int priority;
    @Getter
    private final String fileName;
    @Getter
    private final String configPath;
    @Getter
    private final YamlConfiguration config;
    @Getter
    private final CommandConfiguration commandConfig;
    @Getter
    private final List<CommandCondition.Schedule> conditions;
    
    /*
     Executables
    */
    @Getter
    private final List<TabRecipe> tabRecipes;
    @Getter
    private final List<TabRecipe> exceptions;
    @Getter
    private final List<TabFunction> functions;

    public TabFunction(CommandConfiguration commandConfig, String fileName, YamlConfiguration config, String configPath) {
        this.fileName = fileName;
        this.config = config;
        this.commandConfig = commandConfig;
        this.configPath = configPath;
        ConfigurationSection section = config.getConfigurationSection(configPath);
        breakFunction = section.getBoolean("Break", false);
        priority = section.getInt("Priority", 0);
        conditions = getStringList(section, "Conditions") != null ? getStringList(section, "Conditions").stream().map(syntax -> CommandCondition.getCommandConditions(TabFunction.this, syntax)).collect(Collectors.toList()) : new ArrayList<>();
        tabRecipes = getStringList(section, "Recipes") != null ? getStringList(section, "Recipes").stream().map(syntax -> TabRecipe.getTabRecipe(syntax)).collect(Collectors.toList()) : new ArrayList<>();
        exceptions = getStringList(section, "Exceptions") != null ? getStringList(section, "Exceptions").stream().map(syntax -> TabRecipe.getTabRecipe(syntax)).collect(Collectors.toList()) : new ArrayList<>();
        functions = section.get("Functions") != null ? build(commandConfig, fileName, config, configPath + ".Functions") : new ArrayList<>();
    }
    
    public List<String> getFunctionRecipes(CommandSender sender, Map<String, String> placeholders) {
        TabFunctionEvent event = new TabFunctionEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return null;
        }
        //Conditions match
        List results = new ArrayList<>();
        if (!conditions.stream().map(condition -> {
            results.clear();
            for (int i = 0;i < condition.getExpressions().size();i++) {
                if (condition.getExpressions().get(i) instanceof CommandCondition) {
                    results.add(((CommandCondition) condition.getExpressions().get(i)).matchCondition(commandConfig, configPath + ".Conditions", sender, placeholders));
                } else {
                    results.add(condition.getExpressions().get(i));
                }
            }
            return condition.analysis(results);
        }).allMatch(condition -> condition)) {
            return null;
        }
        //Recipes
        List<String> recipes = new ArrayList<>();
        tabRecipes.stream().forEach(recipe -> recipes.addAll(recipe.getRecipes(sender, placeholders)));
        exceptions.stream().forEach(exception -> recipes.removeAll(exception.getRecipes(sender, placeholders)));
        //Inner recipes
        for (TabFunction function : functions) {
            List<String> subRecipes = function.getFunctionRecipes(sender, placeholders);
            if (subRecipes != null) {
                recipes.addAll(subRecipes);
                if (function.breakFunction) {
                    break;
                }
            }
        }
        return recipes;
    }
    
    public List<String> getFunctionRecipesWithDebug(CommandSender sender, Map<String, String> placeholders, List<CommandExecutor.DebugRecord> record) {
        boolean isReturn = false;
        Map<CommandCondition, Boolean> conditionsInfo = new LinkedHashMap();
        List results = new ArrayList<>();
        for (CommandCondition.Schedule condition : conditions) {
            for (int i = 0;i < condition.getExpressions().size();i++) {
                if (condition.getExpressions().get(i) instanceof CommandCondition) {
                    CommandCondition commandCondition = (CommandCondition) condition.getExpressions().get(i);
                    boolean result = commandCondition.matchCondition(commandConfig, configPath + ".Conditions", sender, placeholders);
                    results.add(result);
                    conditionsInfo.put(commandCondition, result);
                }
            }
            isReturn = condition.analysis(results);
        }
        record.add(new CommandExecutor.DebugRecord(fileName, configPath, commandConfig, new HashMap<>(placeholders), conditionsInfo));
        if (isReturn) {
            return null;
        }
        List<String> recipes = new ArrayList<>();
        tabRecipes.stream().forEach(recipe -> recipes.addAll(recipe.getRecipes(sender, placeholders)));
        exceptions.stream().forEach(exception -> recipes.removeAll(exception.getRecipes(sender, placeholders)));
        for (TabFunction function : functions) {
            List<String> subRecipes = function.getFunctionRecipesWithDebug(sender, placeholders, record);
            if (subRecipes != null) {
                recipes.addAll(subRecipes);
                if (function.breakFunction) {
                    break;
                }
            }
        }
        return recipes;
    }

    @Override
    public int compareTo(Sortable sortTarget) {
        if (sortTarget instanceof TabFunction) {
            TabFunction target = (TabFunction) sortTarget;
            return target.priority <= priority ? 1 : -1;
        }
        return -1;
    }
    
    public String getConditionTypeDisplay(CommandCondition condition) {
        switch (condition.getConditionType()) {
            case COMPARISON: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Comparison");
            case NUMBER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Number");
            case ITEM: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Item");
            case PERMISSION: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Permission");
            case PLACEHOLDER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Placeholder");
            case PLAYER: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Player");
            case WORLD: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.World");
            case REGULAR_EXPRESSION: return MessageUtil.getMessage("Command-Messages.Debug.View.Conditions-Type.Regular-Expression");
        }
        return null;
    }
    
    private List<String> getStringList(ConfigurationSection section, String sectionPath) {
        if (!section.contains(sectionPath)) return null;
        List<String> list = section.getStringList(sectionPath);
        if (section.contains(sectionPath)) {
            if (list.isEmpty() && !section.getString(sectionPath).equals("[]")) {
                list.add(section.getString(sectionPath));
            }
        }
        return list;
    }
    
    public static List<TabFunction> build(CommandConfiguration commandConfig, String fileName, YamlConfiguration config, String configPath) {
        List<TabFunction> tabFunctions = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection(configPath);
        if (section != null) {
            section.getKeys(false).stream().forEach(function -> tabFunctions.add(new TabFunction(commandConfig, fileName, config, configPath + "." + function)));
        }
        return sortArray(tabFunctions);
    }
}
