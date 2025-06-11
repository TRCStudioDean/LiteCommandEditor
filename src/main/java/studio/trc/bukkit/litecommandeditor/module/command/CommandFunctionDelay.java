package studio.trc.bukkit.litecommandeditor.module.command;

import java.util.Map;

import lombok.Getter;

public class CommandFunctionDelay
    implements CommandCompoundFunction
{
    @Getter
    private final double delay;
    
    public CommandFunctionDelay(double delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "[Delay] Delay=" + delay;
    }
    
    public static CommandFunctionDelay build(Map map) {
        if (map.get("Delay") != null) {
            return new CommandFunctionDelay(Double.valueOf(map.get("Delay").toString()));
        }
        return null;
    }
}
