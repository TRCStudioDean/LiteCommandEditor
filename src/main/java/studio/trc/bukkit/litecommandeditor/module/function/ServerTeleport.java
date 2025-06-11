package studio.trc.bukkit.litecommandeditor.module.function;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunction;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;

public class ServerTeleport 
    implements CommandFunctionTask
{
    @Getter
    private final String expression;
    @Getter
    private final String configPath; 
    @Getter
    private final CommandFunction function;
    @Getter
    private final String identifier = "ServerTeleport";

    public ServerTeleport(CommandFunction function, String expression, String configPath) {
        this.expression = expression;
        this.configPath = configPath;
        this.function = function;
    }
    
    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        String[] parameters = MessageUtil.splitStringBySymbol(MessageUtil.replacePlaceholders(sender, expression, placeholders), ':');
        String serverName = parameters[0];
        Player targetPlayer;
        if (parameters.length > 1) {
            if (parameters[1].equalsIgnoreCase("[all]")) {
                Bukkit.getOnlinePlayers().stream().forEach(player -> serverTeleport(player, serverName));
                return;
            } else {
                targetPlayer = Bukkit.getPlayer(parameters[1]);
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            return;
        }
        serverTeleport(targetPlayer, serverName);
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Expression=" + expression;
    }
    
    public static void serverTeleport(Player player, String serverName) {
        if (player != null) {
            ByteArrayOutputStream message = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(message);
            try {
                out.writeUTF("Connect");
                out.writeUTF(serverName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            player.sendPluginMessage(Main.getInstance(), "BungeeCord", message.toByteArray());
        }
    }
    
    public static ServerTeleport build(CommandFunction function, Map map, String configPath) {
        if (map.get("Server-Teleport") != null) {
            return new ServerTeleport(function, map.get("Server-Teleport").toString(), configPath);
        }
        return null;
    }
    
    public static List<ServerTeleport> build(CommandFunction function, List<String> functions, String configPath) {
        return functions.stream().map(syntax -> new ServerTeleport(function, syntax, configPath)).collect(Collectors.toList());
    }
}
