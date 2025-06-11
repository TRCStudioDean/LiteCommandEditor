package studio.trc.bukkit.litecommandeditor.command.subcommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import net.md_5.bungee.api.chat.BaseComponent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommand;
import studio.trc.bukkit.litecommandeditor.command.LiteCommandEditorSubCommandType;
import studio.trc.bukkit.litecommandeditor.itemmanager.ItemUtil;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandManager;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemCollection;
import studio.trc.bukkit.litecommandeditor.module.tool.ItemInfo;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorUtils;

public class ItemCollectionCommand 
    implements LiteCommandEditorSubCommand
{
    @Override
    public void execute(CommandSender sender, String... args) {
        if (args.length == 1) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Help");
        } else {
            String subCommandType = args[1];
            if (subCommandType.equalsIgnoreCase("list")) {
                command_list(sender, args);
            } else if (subCommandType.equalsIgnoreCase("create")) {
                command_create(sender, args);
            } else if (subCommandType.equalsIgnoreCase("delete")) {
                command_delete(sender, args);
            } else if (subCommandType.equalsIgnoreCase("give")) {
                command_give(sender, args);
            } else {
                MessageUtil.sendCommandMessage(sender, "Item-Collection.Usage");
            }
        }
    }

    @Override
    public String getName() {
        return "itemcollection";
    }

    @Override
    public LiteCommandEditorSubCommandType getCommandType() {
        return LiteCommandEditorSubCommandType.ITEM_COLLECTION;
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
            if (subCommandType.equalsIgnoreCase("list") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.LIST.commandPermissionPath, false)) {
                return getTabElements(args, 3, CommandManager.getRegisteredCommands().stream().map(command -> command.getCommandName()).collect(Collectors.toList()));
            } else if (subCommandType.equalsIgnoreCase("create") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.CREATE.commandPermissionPath, false)) {
                return getTabElements(args, 3, CommandManager.getRegisteredCommands().stream().map(command -> command.getCommandName()).collect(Collectors.toList()));
            } else if (subCommandType.equalsIgnoreCase("delete") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.DELETE.commandPermissionPath, false)) {
                return tab_delete(sender, args);
            } else if (subCommandType.equalsIgnoreCase("give") && LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.GIVE.commandPermissionPath, false)) {
                return tab_give(sender, args);
            }
        }
        return new ArrayList<>();
    }
    
    private void command_create(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.CREATE.commandPermissionPath, true) || !LiteCommandEditorUtils.isPlayer(sender, true)) {
            return;
        }
        if (args.length < 4) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Create.Usage");
            return;
        }
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        Player player = (Player) sender;
        String commandName = args[2];
        placeholders.put("{command}", commandName);
        CommandConfiguration command = CommandManager.getCommandConfiguration(commandName);
        if (command == null) {
            MessageUtil.sendCommandMessage(player, "Item-Collection.Not-Exist", placeholders);
            return;
        }
        String itemName = args[3];
        placeholders.put("{name}", itemName);
        ItemStack item = LiteCommandEditorUtils.getItemInHand(player);
        if (item == null || item.getType().equals(Material.AIR)) {
            MessageUtil.sendCommandMessage(player, "Item-Collection.Create.Doesnt-Have-Item-In-Hand");
            return;
        }
        Map<String, BaseComponent> json = new HashMap<>();
        json.put("%item%", ItemUtil.getJSONItemStack(item));
        if (command.getItemCollection().addItem(item, itemName)) {
            MessageUtil.sendCommandMessage(player, "Item-Collection.Create.Successfully", placeholders, json);
        } else {
            MessageUtil.sendCommandMessage(player, "Item-Collection.Create.Already-Exist", placeholders);
        }
    }
    
    private void command_delete(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.DELETE.commandPermissionPath, true)) {
            return;
        }
        if (args.length < 4) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Delete.Usage");
            return;
        }
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        String commandName = args[2];
        placeholders.put("{command}", commandName);
        CommandConfiguration command = CommandManager.getCommandConfiguration(commandName);
        if (command == null) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Not-Exist", placeholders);
            return;
        }
        String itemName = args[3];
        ItemInfo itemInfo = command.getItemCollection().getItem(itemName);
        if (itemInfo == null) {
            placeholders.put("{name}", itemName);
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Delete.Not-Exist", placeholders);
            return;
        }
        ItemStack item = itemInfo.getItem(sender, placeholders);
        Map<String, BaseComponent> json = new HashMap<>();
        json.put("%item%", ItemUtil.getJSONItemStack(item));
        if (command.getItemCollection().removeItem(itemName)) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Delete.Successfully", placeholders, json);
        } else {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Delete.Not-Exist", placeholders);
        }
    }
    
    private void command_give(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.GIVE.commandPermissionPath, true)) {
            return;
        }
        if (args.length < 4) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Give.Usage");
            return;
        }
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        String commandName = args[2];
        placeholders.put("{command}", commandName);
        CommandConfiguration command = CommandManager.getCommandConfiguration(commandName);
        if (command == null) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Not-Exist", placeholders);
            return;
        }
        placeholders.put("{name}", args[3]);
        ItemCollection collection = command.getItemCollection();
        ItemInfo itemInfo = collection.getItem(args[3]);
        if (itemInfo == null) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Give.Not-Exist", placeholders);
            return;
        }
        Map<String, BaseComponent> json = new HashMap<>();
        Player target;
        if (args.length == 4) {
            if (!LiteCommandEditorUtils.isPlayer(sender, true)) {
                return;
            }
            target = (Player) sender;
            json.put("%item%", ItemUtil.getJSONItemStack(itemInfo.give(sender, placeholders, target, -1)));
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Give.Gave-Self", placeholders, json);
        } else {
            target = Bukkit.getPlayer(args[4]);
            if (target == null) {
                LiteCommandEditorUtils.playerNotExist(sender, args[4]);
                return;
            }
            placeholders.put("{player}", target.getName());
            json.put("%item%", ItemUtil.getJSONItemStack(itemInfo.give(sender, placeholders, target, -1)));
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Give.Gave-Others", placeholders, json);
        }
    }
    
    private void command_list(CommandSender sender, String[] args) {
        if (!LiteCommandEditorUtils.hasCommandPermission(sender, SubCommandType.LIST.commandPermissionPath, true)) {
            return;
        }
        if (args.length < 3) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.List.Usage");
            return;
        }
        int page;
        if (args.length > 3) {
            if (!LiteCommandEditorUtils.isInteger(args[3])) {
                LiteCommandEditorUtils.notANumber(sender, args[3]);
                return;
            }
            page = Integer.valueOf(args[3]);
        } else {
            page = 1;
        }
        if (page < 1) {
            page = 1;
        }
        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
        placeholders.put("{command}", args[2]);
        CommandConfiguration commandConfig = CommandManager.getRegisteredCommands().stream().filter(config -> config.getCommandName().equalsIgnoreCase(args[2])).findFirst().orElse(null);
        if (commandConfig == null) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.Not-Exist", placeholders);
            return;
        }
        ItemCollection collection = commandConfig.getItemCollection();
        Map<String, ItemInfo> items = collection.getItems();
        if (items.isEmpty()) {
            MessageUtil.sendCommandMessage(sender, "Item-Collection.List.Empty", placeholders);
            return;
        }
        List<String> names = items.keySet().stream().collect(Collectors.toList());
        int numberOfSinglePage = LiteCommandEditorUtils.isInteger(MessageUtil.getProtectedMessage("Command-Messages.Item-Collection.List.Number-Of-Single-Page")) ? Integer.valueOf(MessageUtil.getProtectedMessage("Command-Messages.Item-Collection.List.Number-Of-Single-Page")) : 9;
        int arraySize = items.size();
        int maxPage = arraySize % numberOfSinglePage == 0 ? arraySize / numberOfSinglePage : arraySize / numberOfSinglePage + 1;
        if (page > maxPage) {
            page = maxPage;
        }
        placeholders.put("!list!", "");
        placeholders.put("{total}", String.valueOf(arraySize));
        placeholders.put("{page}", String.valueOf(page));
        placeholders.put("{previousPage}", String.valueOf(page == 1 ? maxPage : page - 1));
        placeholders.put("{nextPage}", String.valueOf(page == maxPage ? 1 : page + 1));
        placeholders.put("{maxPage}", String.valueOf(maxPage));
        for (String message : MessageUtil.getMessageList("Command-Messages.Item-Collection.List.List")) {
            if (message.toLowerCase().contains("!list!")) {
                for (int count = page * numberOfSinglePage - numberOfSinglePage + 1; count <= arraySize && count <= page * numberOfSinglePage; count++) {
                    ItemInfo item = items.get(names.get(count - 1));
                    placeholders.put("{number}", String.valueOf(count));
                    placeholders.put("{name}", names.get(count - 1));
                    Map<String, BaseComponent> json = new HashMap<>();
                    json.put("%item%", ItemUtil.getJSONItemStack(item.getItem(sender, placeholders)));
                    MessageUtil.sendMessage(sender, message, placeholders, json);
                }
            } else {
                MessageUtil.sendMessage(sender, message, placeholders);
            }
        }
    }

    private List<String> tab_delete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return getTabElements(args, 3, CommandManager.getRegisteredCommands().stream().map(command -> command.getCommandName()).collect(Collectors.toList()));
        } else if (args.length == 4) {
            CommandConfiguration command = CommandManager.getCommandConfiguration(args[2]);
            if (command != null) {
                return getTabElements(args, 4, command.getItemCollection().getItems().keySet());
            }
        }
        return new ArrayList<>();
    }
    
    private List<String> tab_give(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return getTabElements(args, 3, CommandManager.getRegisteredCommands().stream().map(command -> command.getCommandName()).collect(Collectors.toList()));
        } else if (args.length == 4) {
            CommandConfiguration command = CommandManager.getCommandConfiguration(args[2]);
            if (command != null) {
                return getTabElements(args, 4, command.getItemCollection().getItems().keySet());
            }
        } else if (args.length == 5) {
            return getTabPlayersName(args, 5);
        }
        return new ArrayList<>();
    }
    
    public enum SubCommandType {
        /**
         * /lce itemcollection help
         */
        HELP("help", "Commands.Item-Collection"),
        
        /**
         * /lce itemcollection list
         */
        LIST("list", "Commands.Item-Collection.List"),
        
        /**
         * /lce itemcollection create
         */
        CREATE("create", "Commands.Item-Collection.Create"),
        
        /**
         * /lce itemcollection delete
         */
        DELETE("delete", "Commands.Item-Collection.Delete"),
        
        /**
         * /lce itemcollection give
         */
        GIVE("give", "Commands.Item-Collection.Give");
        
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
