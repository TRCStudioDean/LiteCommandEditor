package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.NMSUtil;

public class ActionBar
    implements CommandFunctionTask
{
    public static void sendActionBarAnimation(List<ActionBar> actionBar, Player player) {
        sendActionBarAnimation(actionBar, player, MessageUtil.getDefaultPlaceholders());
    }
    
    public static void sendActionBarAnimation(List<ActionBar> actionBars, Player player, Map<String, String> placeholders) {
        long tick = 0;
        if (LiteCommandEditorThread.isRemoveDuplicateDelayedTasks()) {
            LiteCommandEditorThread.getTaskThread().getTasks().removeIf(task -> task.getIdentifier() != null && task.getIdentifier().equals("ActionBarAnimation:" + player.getUniqueId()));
        }
        for (ActionBar actionBar : actionBars) {
            LiteCommandEditorThread.runTask(() -> actionBar.send(player, placeholders), tick, "ActionBarAnimation:" + player.getUniqueId());
            tick += actionBar.getDelay();
        }
    }
    
    public static ActionBar build(Map map, String fileName, String configPath) {
        try {
            long actionBarDelay = map.containsKey("Delay") ? Long.valueOf(map.get("Delay").toString()) : 0;
            String text = map.containsKey("Text") ? map.get("Text").toString() : null;
            return ActionBar.createActionBar().setDelay(actionBarDelay).setText(text);
        } catch (Exception ex) {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
            placeholders.put("{configPath}", fileName + ": " + configPath);
            LiteCommandEditorProperties.sendOperationMessage("LoadingActionBarFailed", placeholders);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static List<ActionBar> build(List<Map> maps, String fileName, String configPath) {
        List<ActionBar> actionbars = new LinkedList();
        maps.stream().forEach(details -> {
            try {
                long actionBarDelay = details.get("Delay") == null ? 0 : Long.valueOf(details.get("Delay").toString());
                String text = details.get("Text").toString();
                actionbars.add(ActionBar.createActionBar().setDelay(actionBarDelay).setText(text));
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{configPath}", fileName + ": " + configPath);
                LiteCommandEditorProperties.sendOperationMessage("LoadingActionBarFailed", placeholders);
                ex.printStackTrace();
            }
        });
        return actionbars;
    }
    
    @Getter
    private long delay = 0;
    @Getter
    private String text;
    
    public ActionBar() {}
    
    public void send(Player player) {
        NMSUtil.ActionBarUtil.sendActionBar(player, text, MessageUtil.getDefaultPlaceholders());
    }
    
    public void send(Player player, Map<String, String> placeholders) {
        NMSUtil.ActionBarUtil.sendActionBar(player, text, placeholders);
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        if (sender instanceof Player) {
            send((Player) sender, placeholders);
        }
    }

    @Override
    public String getIdentifier() {
        return "ActionBarAnimation";
    }
    
    public ActionBar setDelay(long delay) {
        this.delay = delay;
        return this;
    }
    
    public ActionBar setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Delay=" + delay + ", Text=" + text;
    }
    
    public static ActionBar createActionBar() {
        return new ActionBar();
    }
}
