package studio.trc.bukkit.litecommandeditor.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public interface LiteCommandEditorSubCommand
{
    /**
     * Execute sub command.
     * @param sender Command sender.
     * @param args arguments.
     */
    public void execute(CommandSender sender, String... args);
    
    /**
     * Sub command's name.
     * @return 
     */
    public String getName();
    
    /**
     * Tab complete.
     * @param sender Command sender.
     * @param subCommand The sub command.
     * @param args arguments.
     * @return 
     */
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args);
    
    /**
     * Sub command type.
     * @return 
     */
    public LiteCommandEditorSubCommandType getCommandType();
    
    /**
     * Quickly fill in player names using TAB
     * @param args
     * @param length
     * @return 
     */
    default List<String> getTabPlayersName(String[] args, int length) {
        if (args.length == length) {
            List<String> onlines = Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
            List<String> names = new ArrayList<>();
            onlines.stream().filter(command -> command.toLowerCase().startsWith(args[length - 1].toLowerCase())).forEach(names::add);
            return names;
        }
        return new ArrayList<>();
    }
    
    /**
     * Quickly fill in elements using TAB
     * @param args
     * @param length
     * @param elements
     * @return 
     */
    default List<String> getTabElements(String[] args, int length, Collection<String>... elements) {
        if (args.length == length) {
            List<String> names = new ArrayList<>();
            for (Collection<String> element : elements) {
                element.stream().filter(command -> command.toLowerCase().startsWith(args[length - 1].toLowerCase())).forEach(names::add);
            }
            return names;
        }
        return new ArrayList<>();
    }
}
