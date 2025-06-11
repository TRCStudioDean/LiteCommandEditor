package studio.trc.bukkit.litecommandeditor.module.tool;

import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import studio.trc.bukkit.litecommandeditor.module.CommandCondition;
import studio.trc.bukkit.litecommandeditor.module.CommandConfiguration;

public interface Function
{
    /**
     * Will other functions not be executed after running the current function.
     * @return 
     */
    public boolean isBreakFunction();
    
    /**
     * Get priority of this function.
     * @return 
     */
    public int getPriority();
    
    /**
     * Get file name of this function's configuration file.
     * @return 
     */
    public String getFileName();
    
    /**
     * Get config path of this function.
     * @return 
     */
    public String getConfigPath();
    
    /**
     * Get configuration instance.
     * @return 
     */
    public YamlConfiguration getConfig();
    
    /**
     * Get command configuration instance.
     * @return 
     */
    public CommandConfiguration getCommandConfig();
    
    /**
     * Get all conditions of this function.
     * @return 
     */
    public List<CommandCondition.Schedule> getConditions();
}
