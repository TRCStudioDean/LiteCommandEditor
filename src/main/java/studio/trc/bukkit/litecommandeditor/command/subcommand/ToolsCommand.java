package studio.trc.bukkit.litecommandeditor.command.subcommand;

import com.pa_project.lib.json.JSONArray;
import com.pa_project.lib.json.JSONObject;
import com.pa_project.lib.json.parser.JSONParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.Getter;

import org.bukkit.Effect;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.yaml.snakeyaml.Yaml;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.configuration.RobustConfiguration;
import studio.trc.bukkit.litecommandeditor.itemmanager.ItemUtil;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.function.Configurator;
import studio.trc.bukkit.litecommandeditor.module.tool.ListNamesRecord;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;
import studio.trc.bukkit.litecommandeditor.util.NMSUtil;

public class ToolsCommand 
    implements LiteCommandEditorSubCommand
{
    private final List<String> cachedLanguageCodeList = new ArrayList<>();
    private final Map<CommandSender, ListNamesRecord> listNamesRecorder = new HashMap<>();
    
    @Override
    public void execute(CommandSender sender, String... args) {
        if (args.length == 1) {
            MessageUtil.sendCommandMessage(sender, "Tools.Help");
        } else {
            String subCommandType = args[1];
            if (subCommandType.equalsIgnoreCase("updateItemDisplayName")) {
                command_updateItemDisplayName(sender, args);
            } else if (subCommandType.equalsIgnoreCase("listNames")) {
                command_listNames(sender, args);
            } else if (subCommandType.equalsIgnoreCase("placeholder")) {
                command_placeholder(sender, args);
            } else if (subCommandType.equalsIgnoreCase("configurator")) {
                command_configurator(sender, args);
            } else {
                MessageUtil.sendCommandMessage(sender, "Tools.Help");
            }
        }
    }
    
    @Override
    public String getName() {
        return "tools";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.TOOLS;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args) {
        String subCommandType = args[1];
        if (args.length <= 2) {
            return getTabElements(args, 2, Arrays.stream(SubCommandType.values())
                    .filter(type -> LiteCommandEditorUtils.hasCommandPermission(sender, type.getCommandPermissionPath(), false))
                    .map(type -> type.getCommandName())
                    .collect(Collectors.toList()));
        } else {
            if (subCommandType.equalsIgnoreCase("placeholder") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.PLACEHOLDER.commandPermissionPath, false)) {
                return tab_placeholder(args);
            }
            if (subCommandType.equalsIgnoreCase("configurator") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.CONFIGURATOR.commandPermissionPath, false)) {
                return tab_configurator(args);
            }
            if (subCommandType.equalsIgnoreCase("updateItemDisplayName") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.UPDATE_ITEM_DISPLAY_NAME.commandPermissionPath, false)) {
                return tab_updateItemDisplayNames(args);
            }
            if (subCommandType.equalsIgnoreCase("listNames") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.LIST_NAMES.commandPermissionPath, false)) {
                return tab_listNames(args);
            }
        }
        return new ArrayList<>();
    }
    
    private void command_placeholder(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.PLACEHOLDER.commandPermissionPath, true)) {
            return;
        }
        if (args.length < 3) {
            MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Help");
        } else {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            if (args[2].equalsIgnoreCase("list")) {
                if (placeholders.isEmpty()) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.List.Empty");
                    return;
                }
                int page;
                if (args.length >= 4) {
                    if (!LiteCommandEditorUtils.isInteger(args[3])) {
                        LiteCommandEditorUtils.notANumber(sender, args[3]);
                        return;
                    }
                    page = Integer.valueOf(args[3]);
                } else {
                    page = 1;
                }
                int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getProtectedMessage("Command-Messages.Tools.Placeholder.List.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getProtectedMessage("Command-Messages.Tools.Placeholder.List.Number-Of-Single-Page")) : 9;
                int arraySize = placeholders.size();
                int maxPage = arraySize % numberOfSinglePage == 0 ? arraySize / numberOfSinglePage : arraySize / numberOfSinglePage + 1;
                if (page > maxPage) {
                    page = maxPage;
                }
                if (page < 1) {
                    page = 1;
                }
                List<String> keys = new ArrayList<>(placeholders.keySet());
                placeholders.put("!list!", "");
                placeholders.put("{total}", String.valueOf(arraySize));
                placeholders.put("{page}", String.valueOf(page));
                placeholders.put("{previousPage}", String.valueOf(page == 1 ? maxPage : page - 1));
                placeholders.put("{nextPage}", String.valueOf(page == maxPage ? 1 : page + 1));
                placeholders.put("{maxPage}", String.valueOf(maxPage));
                for (String message : MessageUtil.getMessageList("Command-Messages.Tools.Placeholder.List.List")) {
                    if (message.toLowerCase().contains("!list!")) {
                        for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                            String key = keys.get(count - 1);
                            String value = placeholders.get(key);
                            placeholders.put("{number}", String.valueOf(count));
                            Map<String, String> keyAndValue = new HashMap<>();
                            keyAndValue.put("{placeholder}", "{raw:" + key +"}");
                            keyAndValue.put("{value}", "{raw:" + value + "}");
                            MessageUtil.sendMessage(sender, MessageUtil.replacePlaceholders(message, keyAndValue, false), placeholders);
                        }
                    } else {
                        MessageUtil.sendMessage(sender, message, placeholders);
                    }
                }
            } else if (args[2].equalsIgnoreCase("add")) {
                if (args.length < 5) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Add.Usage");
                } else {
                    String placeholder = "{" + args[3] + "}";
                    String value = args[4];
                    if (placeholders.entrySet().stream().anyMatch(pl -> pl.getKey().equalsIgnoreCase(placeholder))) {
                        placeholders.put("{placeholder}", placeholder);
                        MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Add.Already-Exist", placeholders);
                    } else if (placeholder.equalsIgnoreCase(value)) {
                        MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Add.Equivalent", placeholders);
                    } else {
                        placeholders.put("{placeholder}", placeholder);
                        placeholders.put("{value}", value);
                        MessageUtil.addDefaultPlaceholder(placeholder, value);
                        MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Add.Successfully", placeholders);
                    }
                }
            } else if (args[2].equalsIgnoreCase("remove")) {
                if (args.length < 4) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Remove.Usage");
                } else {
                    String placeholder = args[3];
                    if (placeholders.entrySet().stream().anyMatch(pl -> pl.getKey().equalsIgnoreCase(placeholder))) {
                        MessageUtil.removeDefaultPlaceholder(placeholder);
                        placeholders = MessageUtil.getDefaultPlaceholders();
                        placeholders.put("{placeholder}", placeholder);
                        placeholders.put("{value}", placeholders.entrySet().stream().filter(pl -> pl.getKey().equalsIgnoreCase(placeholder)).findFirst().get().getValue());
                        MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Remove.Successfully", placeholders);
                    } else {
                        placeholders.put("{placeholder}", placeholder);
                        MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Remove.Not-Exist", placeholders);
                    }
                }
            } else {
                MessageUtil.sendCommandMessage(sender, "Tools.Placeholder.Help");
            }
        }
    }
    
    private void command_configurator(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.CONFIGURATOR.commandPermissionPath, true)) {
            return;
        }
        if (args.length < 3) {
            MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Help");
        } else {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            if (args[2].equalsIgnoreCase("create")) {
                if (args.length < 4) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Create.Usage");
                } else {
                    placeholders.put("{tableName}", args[3]);
                    if (Configurator.getTable(args[3]) != null) {
                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Create.Already-Exist", placeholders);
                    } else {
                        Configurator.getTables().put(args[3], new JSONObject());
                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Create.Successfully", placeholders);
                    }
                }
            } else if (args[2].equalsIgnoreCase("use")) {
                if (args.length < 5) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.Usage");
                } else {
                    if (Configurator.getTable(args[3]) == null) {
                        placeholders.put("{tableName}", args[3]);
                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.Not-Exist", placeholders);
                    } else {
                        String tableName = Configurator.getTables().entrySet().stream().map(table -> table.getKey()).filter(table -> table.equalsIgnoreCase(args[3])).findFirst().orElse(null);
                        JSONObject table = Configurator.getTables().get(tableName);
                        placeholders.put("{tableName}", tableName);
                        if (args[4].equalsIgnoreCase("set")) {
                            if (args.length > 6) {
                                String path = args[5];
                                String value = args[6];
                                table.set(path, LiteCommandEditorUtils.toValue(value));
                                placeholders.put("{path}", path);
                                placeholders.put("{value}", value);
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.Set.Successfully", placeholders);
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.Set.Usage");
                            }
                        } else if (args[4].equalsIgnoreCase("remove")) {
                            if (args.length > 5) {
                                String path = args[5];
                                placeholders.put("{path}", path);
                                table.set(path, null);
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.Remove.Successfully", placeholders);
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.Remove.Usage");
                            }
                        } else if (args[4].equalsIgnoreCase("listadd")) {
                            if (args.length > 6) {
                                String path = args[5];
                                String value = args[6];
                                placeholders.put("{path}", path);
                                placeholders.put("{value}", value);
                                if (table.get(path) instanceof List) {
                                    table.getRawList(path).add(LiteCommandEditorUtils.toValue(value));
                                } else {
                                    JSONArray array = new JSONArray();
                                    array.add(LiteCommandEditorUtils.toValue(value));
                                    table.set(path, array);
                                }
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListAdd.Successfully", placeholders);
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListAdd.Usage");
                            }
                        } else if (args[4].equalsIgnoreCase("listset")) {
                            if (args.length > 7 && LiteCommandEditorUtils.isInteger(args[6])) {
                                String path = args[5];
                                int index = Integer.valueOf(args[6]);
                                String value = args[7];
                                placeholders.put("{path}", path);
                                placeholders.put("{index}", args[6]);
                                placeholders.put("{value}", value);
                                if (table.get(path) instanceof List) {
                                    List list = table.getRawList(path);
                                    if (list.size() > index - 1 && index > 0) {
                                        list.set(index - 1, LiteCommandEditorUtils.toValue(value));
                                    } else {
                                        list.add(LiteCommandEditorUtils.toValue(value));
                                    }
                                } else {
                                    JSONArray array = new JSONArray();
                                    array.add(LiteCommandEditorUtils.toValue(value));
                                    table.set(path, array);
                                }
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListSet.Successfully", placeholders);
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListSet.Usage");
                            }
                        } else if (args[4].equalsIgnoreCase("listremove")) {
                            if (args.length > 6 && LiteCommandEditorUtils.isInteger(args[6])) {
                                String path = args[5];
                                int index = Integer.valueOf(args[6]);
                                placeholders.put("{path}", path);
                                placeholders.put("{index}", args[6]);
                                if (table.get(path) instanceof List) {
                                    List list = table.getRawList(path);
                                    if (list.size() > index - 1 && index > 0) {
                                        list.remove(index - 1);
                                    }
                                }
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListRemove.Successfully", placeholders);
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListRemove.Usage");
                            }
                        } else if (args[4].equalsIgnoreCase("listclear")) {
                            if (args.length > 5) {
                                String path = args[5];
                                placeholders.put("{path}", path);
                                if (table.get(path) instanceof List) {
                                    table.getRawList(path).clear();
                                }
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListClear.Successfully", placeholders);
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.ListClear.Usage");
                            }
                        } else {
                            MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Use.Usage");
                        }
                    }
                }
            } else if (args[2].equalsIgnoreCase("delete")) {
                if (args.length < 4) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Delete.Usage");
                } else {
                    String tableName = Configurator.getTables().entrySet().stream().map(table -> table.getKey()).filter(table -> table.equalsIgnoreCase(args[3])).findFirst().orElse(null);
                    if (tableName != null) {
                        placeholders.put("{tableName}", tableName);
                        Configurator.getTables().remove(tableName);
                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Delete.Successfully", placeholders);
                    } else {
                        placeholders.put("{tableName}", args[3]);
                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Delete.Not-Exist", placeholders);
                    }
                }
            } else if (args[2].equalsIgnoreCase("load")) {
                if (args.length < 6) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Usage");
                } else {
                    String type = args[3];
                    String tableName = Configurator.getTables().entrySet().stream().map(table -> table.getKey()).filter(table -> table.equalsIgnoreCase(args[4])).findFirst().orElse(args[4]);
                    String value = String.join(" ", Arrays.stream(args).skip(5).toArray(String[]::new));
                    placeholders.put("{tableName}", tableName);
                    switch (type.toLowerCase()) {
                        case "file": {
                            placeholders.put("{file}", value);
                            File file = new File(value);
                            if (file.exists()) {
                                try {
                                    Configurator.getTables().put(tableName, JSONObject.toJSONObject(file));
                                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Successfully", placeholders);
                                } catch (JSONParseException ex) {
                                    try (Reader reader = new InputStreamReader(new FileInputStream(file), LiteCommandEditorProperties.getMessage("Charset"))) {
                                        Configurator.getTables().put(tableName, JSONObject.toJSONObject((Map<?,?>) new Yaml().load(YamlConfiguration.loadConfiguration(reader).saveToString())));
                                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Successfully", placeholders);
                                    } catch (IOException | JSONParseException ex1) {
                                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Failed:File-Format", placeholders);
                                    }
                                }
                            } else {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Failed:File", placeholders);
                            }
                            break;
                        }
                        case "text": {
                            placeholders.put("{text}", value.length() > 256 ? value.substring(0, 255) + "..." : value);
                            try {
                                Configurator.getTables().put(tableName, JSONObject.toJSONObject(value));
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Successfully", placeholders);
                            } catch (JSONParseException ex) {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Failed:Text", placeholders);
                            }
                            break;
                        }
                        default: {
                            MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Load.Usage");
                            break;
                        }
                    }
                }
            } else if (args[2].equalsIgnoreCase("save")) {
                if (args.length < 6) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Save.Usage");
                } else {
                    JSONObject json = Configurator.getTable(args[3]);
                    if (json == null) {
                        placeholders.put("{tableName}", args[3]);
                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Save.Not-Exist", placeholders);
                    } else {
                        placeholders.put("{tableName}", Configurator.getTables().entrySet().stream().map(table -> table.getKey()).filter(table -> table.equalsIgnoreCase(args[3])).findFirst().orElse(null));
                        switch (args[4].toLowerCase()) {
                            case "json": {
                                try {
                                    File file = LiteCommandEditorUtils.createFile(String.join(":", Arrays.stream(args).skip(5).toArray(String[]::new)));
                                    placeholders.put("{file}", file.getName());
                                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), LiteCommandEditorProperties.getMessage("Charset")))) {
                                        writer.write(json.toVisualizedJSONString());
                                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Save.Successfully", placeholders);
                                    }
                                }catch (IOException ex) {
                                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Save.Failed", placeholders);
                                }
                                break;
                            }
                            case "yaml": {
                                YamlConfiguration yaml = new YamlConfiguration();
                                json.entrySet().stream().forEach(entry -> {
                                    if (entry.getValue() instanceof Map) {
                                        yaml.createSection(entry.getKey().toString(), (Map<?, ?>) entry.getValue());
                                    } else {
                                        yaml.set(entry.getKey().toString(), entry.getValue());
                                    }
                                });
                                try {
                                    File file = LiteCommandEditorUtils.createFile(String.join(":", Arrays.stream(args).skip(5).toArray(String[]::new)));
                                    placeholders.put("{file}", file.getName());
                                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), LiteCommandEditorProperties.getMessage("Charset")))) {
                                        writer.write(yaml.saveToString());
                                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Save.Successfully", placeholders);
                                    }
                                } catch (IOException ex) {
                                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Save.Failed", placeholders);
                                }
                                break;
                            }
                            default: {
                                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Save.Usage");
                                break;
                            }
                        }
                    }
                }
            } else if (args[2].equalsIgnoreCase("list")) {
                String[] tables = Configurator.getTables().keySet().toArray(new String[] {});
                if (tables.length == 0) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.List.Empty");
                    return;
                }
                int page;
                if (args.length >= 4) {
                    if (!LiteCommandEditorUtils.isInteger(args[3])) {
                        LiteCommandEditorUtils.notANumber(sender, args[3]);
                        return;
                    }
                    page = Integer.valueOf(args[3]);
                } else {
                    page = 1;
                }
                int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getProtectedMessage("Command-Messages.Tools.Configurator.List.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getProtectedMessage("Command-Messages.Tools.Configurator.List.Number-Of-Single-Page")) : 9;
                int arraySize = tables.length;
                int maxPage = arraySize % numberOfSinglePage == 0 ? arraySize / numberOfSinglePage : arraySize / numberOfSinglePage + 1;
                if (page > maxPage) {
                    page = maxPage;
                }
                if (page < 1) {
                    page = 1;
                }
                placeholders.put("!list!", "");
                placeholders.put("{total}", String.valueOf(arraySize));
                placeholders.put("{page}", String.valueOf(page));
                placeholders.put("{previousPage}", String.valueOf(page == 1 ? maxPage : page - 1));
                placeholders.put("{nextPage}", String.valueOf(page == maxPage ? 1 : page + 1));
                placeholders.put("{maxPage}", String.valueOf(maxPage));
                for (String message : MessageUtil.getMessageList("Command-Messages.Tools.Configurator.List.List")) {
                    if (message.toLowerCase().contains("!list!")) {
                        for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                            placeholders.put("{number}", String.valueOf(count));
                            placeholders.put("{tableName}", tables[count - 1]);
                            MessageUtil.sendMessage(sender, message, placeholders);
                        }
                    } else {
                        MessageUtil.sendMessage(sender, message, placeholders);
                    }
                }
            } else if (args[2].equalsIgnoreCase("view")) {
                if (args.length < 4) {
                    MessageUtil.sendCommandMessage(sender, "Tools.Configurator.View.Usage");
                } else {
                    String tableName = Configurator.getTables().entrySet().stream().map(table -> table.getKey()).filter(table -> table.equalsIgnoreCase(args[3])).findFirst().orElse(null);
                    if (tableName != null) {
                        JSONObject table = Configurator.getTables().get(tableName);
                        placeholders.put("{tableName}", tableName);
                        Map<String, String> viewer = getTableInfo(table, new ArrayList<>(), true);
                        String[] paths = viewer.keySet().toArray(new String[] {});
                        if (viewer.isEmpty()) {
                            MessageUtil.sendCommandMessage(sender, "Tools.Configurator.View.Empty");
                            return;
                        }
                        int page;
                        if (args.length >= 5) {
                            if (!LiteCommandEditorUtils.isInteger(args[4])) {
                                LiteCommandEditorUtils.notANumber(sender, args[4]);
                                return;
                            }
                            page = Integer.valueOf(args[4]);
                        } else {
                            page = 1;
                        }
                        int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getProtectedMessage("Command-Messages.Tools.Configurator.View.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getProtectedMessage("Command-Messages.Tools.Configurator.View.Number-Of-Single-Page")) : 9;
                        int arraySize = viewer.size();
                        int maxPage = arraySize % numberOfSinglePage == 0 ? arraySize / numberOfSinglePage : arraySize / numberOfSinglePage + 1;
                        if (page > maxPage) {
                            page = maxPage;
                        }
                        if (page < 1) {
                            page = 1;
                        }
                        placeholders.put("!view!", "");
                        placeholders.put("{total}", String.valueOf(arraySize));
                        placeholders.put("{page}", String.valueOf(page));
                        placeholders.put("{previousPage}", String.valueOf(page == 1 ? maxPage : page - 1));
                        placeholders.put("{nextPage}", String.valueOf(page == maxPage ? 1 : page + 1));
                        placeholders.put("{maxPage}", String.valueOf(maxPage));
                        for (String message : MessageUtil.getMessageList("Command-Messages.Tools.Configurator.View.List")) {
                            if (message.toLowerCase().contains("!view!")) {
                                for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                                    placeholders.put("{number}", String.valueOf(count));
                                    Map<String, String> keyAndValue = new HashMap<>();
                                    keyAndValue.put("{path}", "{raw:" + paths[count - 1] + "}");
                                    keyAndValue.put("{value}", "{raw:" + viewer.get(paths[count - 1]) + "}");
                                    MessageUtil.sendMessage(sender, MessageUtil.replacePlaceholders(message, keyAndValue, false), placeholders);
                                }
                            } else {
                                MessageUtil.sendMessage(sender, message, placeholders);
                            }
                        }
                    } else {
                        placeholders.put("{tableName}", args[3]);
                        MessageUtil.sendCommandMessage(sender, "Tools.Configurator.View.Not-Exist", placeholders);
                    }
                }
            } else {
                MessageUtil.sendCommandMessage(sender, "Tools.Configurator.Help");
            }
        }
    }
    
    private void command_listNames(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.LIST_NAMES.commandPermissionPath, true)) {
            return;
        }
        if (args.length < 3) {
            MessageUtil.sendCommandMessage(sender, "Tools.List-Names.Help");
        } else {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            if (args[2].equalsIgnoreCase("result")) {
                int page;
                if (args.length >= 4) {
                    if (!LiteCommandEditorUtils.isInteger(args[3])) {
                        LiteCommandEditorUtils.notANumber(sender, args[3]);
                        return;
                    }
                    page = Integer.valueOf(args[3]);
                } else {
                    page = 1;
                }
                ListNamesRecord record = listNamesRecorder.get(sender);
                if (record == null || record.getNames().isEmpty()) {
                    MessageUtil.sendCommandMessage(sender, "Tools.List-Names.Result.No-Result");
                    return;
                }
                List<String> keywords = record.getKeywords();
                String[] names = record.getNames().stream().sorted().toArray(String[]::new);
                int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getProtectedMessage("Command-Messages.Tools.List-Names.Result.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getProtectedMessage("Command-Messages.Tools.List-Names.Result.Number-Of-Single-Page")) : 9;
                int arraySize = names.length;
                int maxPage = arraySize % numberOfSinglePage == 0 ? arraySize / numberOfSinglePage : arraySize / numberOfSinglePage + 1;
                if (page > maxPage) {
                    page = maxPage;
                }
                if (page < 1) {
                    page = 1;
                }
                placeholders.put("!list!", "");
                placeholders.put("{type}", record.getDisplayTypeName());
                placeholders.put("{total}", String.valueOf(arraySize));
                placeholders.put("{page}", String.valueOf(page));
                placeholders.put("{previousPage}", String.valueOf(page == 1 ? maxPage : page - 1));
                placeholders.put("{nextPage}", String.valueOf(page == maxPage ? 1 : page + 1));
                placeholders.put("{maxPage}", String.valueOf(maxPage));
                RobustConfiguration messages = ConfigurationType.MESSAGES.getRobustConfig();
                for (String message : MessageUtil.getMessageList("Command-Messages.Tools.List-Names.Result.List")) {
                    if (message.toLowerCase().contains("!list!")) {
                        for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                            String element = names[count - 1];
                            String name = element;
                            if (messages.getBoolean(MessageUtil.getLanguage() + ".Command-Messages.Tools.List-Names.Keyword-Highlight.Enabled")) {
                                String keywordColor = messages.getString(MessageUtil.getLanguage() + ".Command-Messages.Tools.List-Names.Keyword-Highlight.Keyword-Color");
                                String otherColor = messages.getString(MessageUtil.getLanguage() + ".Command-Messages.Tools.List-Names.Keyword-Highlight.Other-Color");
                                name = otherColor + name;
                                for (String keyword : keywords) {
                                    if (name.toLowerCase().contains(keyword.toLowerCase())) {
                                        name = MessageUtil.replacePlaceholders(name, keyword, keywordColor + keyword + otherColor);
                                    }
                                }
                            }
                            placeholders.put("{number}", String.valueOf(count));
                            placeholders.put("{element}", element);
                            placeholders.put("{name}", name);
                            MessageUtil.sendMessage(sender, message, placeholders);
                        }
                    } else {
                        MessageUtil.sendMessage(sender, message, placeholders);
                    }
                }
            } else {
                try {
                    switch (args[2].toLowerCase()) {
                        case "sound": {
                            listNamesRecorder.put(sender, getRecord("Sound", args, Arrays.stream(Sound.values()).map(value -> value.name())));
                            break;
                        }
                        case "treetype": {
                            listNamesRecorder.put(sender, getRecord("TreeType", args, Arrays.stream(TreeType.values()).map(value -> value.name())));
                            break;
                        }
                        case "effect": {
                            listNamesRecorder.put(sender, getRecord("Effect", args, Arrays.stream(Effect.values()).map(value -> value.name())));
                            break;
                        }
                        case "biome": {
                            listNamesRecorder.put(sender, getRecord("Biome", args, Arrays.stream(Biome.values()).map(value -> value.name())));
                            break;
                        }
                        case "gamerule": {
                            listNamesRecorder.put(sender, getRecord("GameRule", args, Arrays.stream(GameRule.values()).map(value -> value.getName())));
                            break;
                        }
                        case "entitytype": {
                            listNamesRecorder.put(sender, getRecord("EntityType", args, Arrays.stream(EntityType.values()).map(value -> value.name())));
                            break;
                        }
                        case "particle": {
                            listNamesRecorder.put(sender, getRecord("Particle", args, NMSUtil.ParticleUtil.values()));
                            break;
                        }
                        case "item": {
                            listNamesRecorder.put(sender, getRecord("Item", args, Arrays.stream(Material.values()).map(value -> value.name())));
                            break;
                        }
                        default: {
                            placeholders.put("{type}", args[2]);
                            MessageUtil.sendCommandMessage(sender, "Tools.List-Names.Not-Available", placeholders);
                            return;
                        }
                    }
                } catch (Throwable t) {}
                getCommandType().getSubCommand().execute(sender, "tools", "listNames", "result");
            }
        }
    }
    
    private void command_updateItemDisplayName(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.UPDATE_ITEM_DISPLAY_NAME.commandPermissionPath, true)) {
            return;
        }
        if (args.length < 3) {
            MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Usage");
        } else {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            if (args.length == 3) {
                if (args[2].equalsIgnoreCase("confirm")) {
                    if (ItemUtil.isDownloading()) {
                        MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Operation-in-Progress", placeholders);
                        return;
                    }
                    LiteCommandEditorThread.runTask(() -> {
                        String languageCode = ItemUtil.getLanguageCode();
                        placeholders.put("{languageCode}", languageCode);
                        placeholders.put("{path}", MessageUtil.getLanguage());
                        MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Starting", placeholders);
                        if (ItemUtil.downloadLanguage(sender, languageCode)) {
                            long loaded = ItemUtil.updateItemNames(sender, MessageUtil.getLanguage());
                            if (loaded > 0) {
                                int total = Material.values().length;
                                placeholders.put("{loaded}", String.valueOf(loaded));
                                placeholders.put("{total}", String.valueOf(total));
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Successfully", placeholders);
                            }
                        }
                    });
                } else {
                    MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Usage");
                }
            } else if (args.length == 4) {
                placeholders.put("{arguments}", args[2] + " " + args[3] + " confirm");
                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Confirming", placeholders);
            } else {
                if (args[4].equalsIgnoreCase("confirm")) {
                    if (ItemUtil.isDownloading()) {
                        MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Executions.Operation-in-Progress", placeholders);
                        return;
                    }
                    String languageCode = ItemUtil.toLanguageCode(args[2]);
                    String path = args[3];
                    LiteCommandEditorThread.runTask(() -> {
                        placeholders.put("{languageCode}", languageCode);
                        placeholders.put("{path}", path);
                        MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Starting", placeholders);
                        if (ItemUtil.downloadLanguage(sender, languageCode)) {
                            long loaded = ItemUtil.updateItemNames(sender, path);
                            if (loaded > 0) {
                                int total = Material.values().length;
                                placeholders.put("{loaded}", String.valueOf(loaded));
                                placeholders.put("{total}", String.valueOf(total));
                                MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Successfully", placeholders);
                            }
                        }
                    });
                } else {
                    MessageUtil.sendCommandMessage(sender, "Tools.Update-Item-Display-Name.Usage");
                }
            }
        }
    }
    
    private void saveLoadedLanguageCodeToCache() {
        if (ItemUtil.getAssetIndex() != null) {
            cachedLanguageCodeList.clear();
            ItemUtil.getAssetIndex().getJSONObject("objects").keySet().stream().forEach(object -> {
                String key = object.toString();
                if (key.startsWith("minecraft/lang/")) {
                    cachedLanguageCodeList.add(key.substring("minecraft/lang/".length(), key.length() - ".json".length()));
                }
            });
        }
    }
    
    private ListNamesRecord getRecord(String typeName, String[] args, Stream<String> stream) {
        List<String> keywords = new ArrayList<>();
        for (int i = 3;i < args.length;i++) {
            keywords.add(args[i]);
        }
        List<String> names = stream.filter(name -> keywords.stream().allMatch(keyword -> name.toLowerCase().contains(keyword.toLowerCase()))).collect(Collectors.toList());
        return new ListNamesRecord(typeName, keywords, names);
    }
    
    private List<String> tab_placeholder(String[] args) {
        if (args.length == 3) {
            return getTabElements(args, 3, Arrays.asList("list", "add", "remove", "help"));
        } else if (args.length == 4) {
            if (args[2].equalsIgnoreCase("remove")) {
                return getTabElements(args, 4, MessageUtil.getDefaultPlaceholders().keySet());
            }
        }
        return new ArrayList<>();
    }
    
    private List<String> tab_configurator(String[] args) {
        if (args.length == 3) {
            return getTabElements(args, 3, Arrays.asList("create", "use", "delete", "load", "save", "list", "view", "help"));
        } else if (args.length >= 4) {
            if (args[2].equalsIgnoreCase("use")) {
                if (args.length == 4) {
                    return getTabElements(args, 4, Configurator.getTables().keySet());
                } else if (args.length == 5) {
                    return getTabElements(args, 5, Arrays.asList("set", "remove", "listadd", "listset", "listremove", "listclear", "help"));
                } else if (args.length >= 6) {
                    String tableName = Configurator.getTables().entrySet().stream().map(table -> table.getKey()).filter(table -> table.equalsIgnoreCase(args[3])).findFirst().orElse(null);
                    if (tableName != null) {
                        JSONObject table = Configurator.getTables().get(tableName);
                        if (args.length == 6) {
                            return getTabElements(args, 6, getTableInfo(table, new ArrayList<>(), false).keySet());
                        } else if (args.length == 7 && (args[4].equalsIgnoreCase("listset") || args[4].equalsIgnoreCase("listremove")) && table.containsKey(args[5]) && table.get(args[5]) instanceof List) {
                            return getTabElements(args, 7, IntStream.range(0, table.getStringList(args[5]).size()).boxed().map(index -> String.valueOf(index + 1)).collect(Collectors.toList()));
                        }
                    }
                }
            } else if ((args[2].equalsIgnoreCase("delete") || args[2].equalsIgnoreCase("save") || args[2].equalsIgnoreCase("view")) && args.length == 4) {
                return getTabElements(args, 4, Configurator.getTables().keySet());
            } else if (args[2].equalsIgnoreCase("load") && args.length == 4) {
                return getTabElements(args, 4, Arrays.asList("file", "text"));
            } else if (args[2].equalsIgnoreCase("save") && args.length == 5) {
                return getTabElements(args, 5, Arrays.asList("yaml", "json"));
            }
        }
        return new ArrayList<>();
    }
    
    private List<String> tab_listNames(String[] args) {
        if (args.length == 3) {
            return getTabElements(args, 3, Arrays.asList("sound", "treetype", "effect", "biome", "gamerule", "entitytype", "particle", "item", "result"));
        }
        return new ArrayList<>();
    }
    
    private List<String> tab_updateItemDisplayNames(String[] args) {
        if (cachedLanguageCodeList.isEmpty() && ItemUtil.getAssetIndex() != null) {
            saveLoadedLanguageCodeToCache();
        }
        return getTabElements(args, 3, cachedLanguageCodeList);
    }
    
    private Map<String, String> getTableInfo(Map<Object, Object> table, List<String> parent, boolean list) {
        Map<String, String> result = new LinkedHashMap<>();
        table.entrySet().stream().forEach(entry -> {
            parent.add(entry.getKey().toString());
            if (entry.getValue() instanceof Map) {
                result.putAll(getTableInfo((Map<Object, Object>) entry.getValue(), parent, list));
            } else if (entry.getValue() instanceof List && list) {
                result.putAll(getTableList((List<Object>) entry.getValue(), parent));
            } else {
                result.put(String.join(".", parent.toArray(new String[] {})), entry.getValue().toString());
            }
            parent.remove(entry.getKey().toString());
        });
        return result;
    }
    
    private Map<String, String> getTableList(List<Object> list, List<String> parent) {
        Map<String, String> result = new LinkedHashMap<>();
        String path = String.join(".", parent.toArray(new String[] {}));
        for (int i = 0;i < list.size();i++) {
            result.put(path + "[" + (i + 1) + "]", list.get(i).toString());
        }
        return result;
    }
    
    public enum SubCommandType {
        /**
         * /lce tools help
         */
        HELP("help", "Commands.Tools"),
        
        /**
         * /lce tools placeholder
         */
        PLACEHOLDER("placeholder", "Commands.Tools.Placeholder"),
        
        /**
         * /lce tools configurator
         */
        CONFIGURATOR("configurator", "Commands.Tools.Configurator"),
        
        /**
         * /lce tools listNames
         */
        LIST_NAMES("listNames", "Commands.Tools.List-Names"),
        
        /**
         * /lce tools updateItemDisplayName
         */
        UPDATE_ITEM_DISPLAY_NAME("updateItemDisplayName", "Commands.Tools.Update-Item-Display-Name");
        
        @Getter
        private final String commandName;
        @Getter
        private final String commandPermissionPath;
        
        private SubCommandType(String commandName, String commandPermissionPath) {
            this.commandName = commandName;
            this.commandPermissionPath = commandPermissionPath;
        }
    }
}
