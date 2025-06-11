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
     * 执行子命令
     * @param sender 发送命令的人
     * @param args 参数
     */
    public void execute(CommandSender sender, String... args);
    
    /**
     * 子命令的名字
     * @return 
     */
    public String getName();
    
    /**
     * Tab补充命令
     * @param sender 发送命令的人
     * @param subCommand 子命令
     * @param args 参数
     * @return 
     */
    public List<String> tabComplete(CommandSender sender, String subCommand, String... args);
    
    /**
     * 子命令的类型
     * @return 
     */
    public LiteCommandEditorSubCommandType getCommandType();
    
    /**
     * 使用TAB快速填充玩家名
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
     * 使用TAB快速填充元素名字
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
