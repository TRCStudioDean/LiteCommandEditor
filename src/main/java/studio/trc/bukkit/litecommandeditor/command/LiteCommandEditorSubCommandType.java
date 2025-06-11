package studio.trc.bukkit.litecommandeditor.command;

import lombok.Getter;

import studio.trc.bukkit.litecommandeditor.command.subcommand.*;

public enum LiteCommandEditorSubCommandType
{
    //<editor-fold desc="SubCommandTypes" defaultstate="collapsed">
    /**
     * /lce help
     * {@link HelpCommand}
     */
    HELP("help", new HelpCommand(), "Commands.Help"),
    
    /**
     * /lce reload
     * {@link ReloadCommand}
     */
    RELOAD("reload", new ReloadCommand(), "Commands.Reload"),
    
    /**
     * /lce info
     * {@link InfoCommand}
     */
    INFO("info", new InfoCommand(), "Commands.Info"),
    
    /**
     * /lce itemcollection
     * {@link ItemCollectionCommand}
     */
    ITEM_COLLECTION("itemcollection", new ItemCollectionCommand(), "Commands.Item-Collection"),
    
    /**
     * /lce load
     * {@link LoadCommand}
     */
    LOAD("load", new LoadCommand(), "Commands.Load"),
    
    /**
     * /lce unload
     * {@link UnloadCommand}
     */
    UNLOAD("unload", new UnloadCommand(), "Commands.Unload"),
    
    /**
     * /lce debug
     * {@link DebugCommand}
     */
    DEBUG("debug", new DebugCommand(), "Commands.Debug"),
    
    /**
     * /lce delete
     * {@link DeleteCommand}
     */
    DELETE("delete", new DeleteCommand(), "Commands.Delete"),
    
    /**
     * /lce list
     * {@link ListCommand}
     */
    LIST("list", new ListCommand(), "Commands.List"),
    
    /**
     * /lce listall
     * {@link ListAllCommand}
     */
    LIST_ALL("listall", new ListAllCommand(), "Commands.List-All"),
    
    /**
     * /lce tools
     * {@link ToolsCommand}
     */
    TOOLS("tools", new ToolsCommand(), "Commands.Tools");
    //</editor-fold>

    @Getter
    private final String subCommandName;
    @Getter
    private final String commandPermissionPath;
    @Getter
    private final LiteCommandEditorSubCommand subCommand;

    private LiteCommandEditorSubCommandType(String subCommandName, LiteCommandEditorSubCommand subCommand, String commandPermissionPath) {
        this.subCommandName = subCommandName;
        this.subCommand = subCommand;
        this.commandPermissionPath = commandPermissionPath;
    }

    public static LiteCommandEditorSubCommandType getCommandType(String subCommand) {
        for (LiteCommandEditorSubCommandType type : values()) {
            if (type.getSubCommandName().equalsIgnoreCase(subCommand)) {
                return type;
            }
        }
        return null;
    }
}
