package studio.trc.bukkit.litecommandeditor.module.command;

/**
 * Compound Function Description Interface
 */
public interface CommandCompoundFunction 
{
    /**
     * Get function identifier
     * @return 
     */
    default String getIdentifier() {
        return null;
    }
}
