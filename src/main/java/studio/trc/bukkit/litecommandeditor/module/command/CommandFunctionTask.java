package studio.trc.bukkit.litecommandeditor.module.command;

import java.util.Map;

import org.bukkit.command.CommandSender;

public interface CommandFunctionTask
    extends CommandCompoundFunction
{
    /**
     * Execute task
     * @param sender Command sender
     * @param placeholders placeholders
     */
    public void executeTask(CommandSender sender, Map<String, String> placeholders);
    
    /**
     * Get function identifier
     * @return 
     */
    default String getIdentifier() {
        return null;
    }
}
