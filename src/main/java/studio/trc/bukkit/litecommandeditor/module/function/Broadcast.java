package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;

public class Broadcast
    implements CommandFunctionTask
{
    @Getter
    private final List<String> messages;
    @Getter
    private final String identifier = "Broadcast";

    public Broadcast(List<String> messages) {
        this.messages = messages;
    }
    
    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        Bukkit.getOnlinePlayers().stream().forEach(player -> MessageUtil.sendMessage(player, messages, placeholders));
        MessageUtil.sendMessage(Bukkit.getConsoleSender(), messages, placeholders);
    }
    
    public static Broadcast build(Map map) {
        if (map.get("Broadcast") != null) {
            List<String> messages = new LinkedList();
            if (map.get("Broadcast") instanceof Collection) {
                messages.addAll((Collection<? extends String>) map.get("Broadcast"));
            } else {
                messages.add(map.get("Broadcast").toString());
            }
            return new Broadcast(messages);
        }
        return null;
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Broadcast=" + messages;
    }
}
