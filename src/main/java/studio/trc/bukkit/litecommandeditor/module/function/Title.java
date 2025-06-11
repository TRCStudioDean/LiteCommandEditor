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

public class Title
    implements CommandFunctionTask
{
    public static void sendTitleAnimation(List<Title> titles, Player player) {
        sendTitleAnimation(titles, player, MessageUtil.getDefaultPlaceholders());
    }
    
    public static void sendTitleAnimation(List<Title> titles, Player player, Map<String, String> placeholders) {
        long tick = 0;
        if (LiteCommandEditorThread.isRemoveDuplicateDelayedTasks()) {
            LiteCommandEditorThread.getTaskThread().getTasks().removeIf(task -> task.getIdentifier() != null && task.getIdentifier().equals("TitleAnimation:" + player.getUniqueId()));
        }
        for (Title title : titles) {
            LiteCommandEditorThread.runTask(() -> title.send(player, placeholders), tick, "TitleAnimation:" + player.getUniqueId());
            tick += title.getDelay();
        }
    }
    
    public static Title build(Map map, String fileName, String configPath) {
        try {
            double fadein = map.containsKey("Fade-In") ? Double.valueOf(map.get("Fade-In").toString()) : 1;
            double stay = map.containsKey("Stay") ? Double.valueOf(map.get("Stay").toString()) : 5;
            double fadeout = map.containsKey("Fade-Out") ? Double.valueOf(map.get("Fade-Out").toString()) : 1;
            long titleDelay = map.containsKey("Delay") ? Long.valueOf(map.get("Delay").toString()) : 0;
            String title = map.containsKey("Title") ? map.get("Title").toString() : null;
            String subTitle = map.containsKey("Sub-Title") ? map.get("Sub-Title").toString() : null;
            return Title.createTitle().setDelay(titleDelay).setFadeIn(fadein).setStay(stay).setFadeOut(fadeout).setTitle(title).setSubTitle(subTitle);
        } catch (Exception ex) {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
            placeholders.put("{configPath}", fileName + ": " + configPath);
            LiteCommandEditorProperties.sendOperationMessage("LoadingTitleFailed", placeholders);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static List<Title> build(List<Map> maps, String fileName, String configPath) {
        List<Title> titles = new LinkedList();
        maps.stream().forEach(details -> {
            try {
                double fadein = details.containsKey("Fade-In") ? Double.valueOf(details.get("Fade-In").toString()) : 1;
                double stay = details.containsKey("Stay") ? Double.valueOf(details.get("Stay").toString()) : 5;
                double fadeout = details.containsKey("Fade-Out") ? Double.valueOf(details.get("Fade-Out").toString()) : 1;
                long titleDelay = details.containsKey("Delay") ? Long.valueOf(details.get("Delay").toString()) : 0;
                String title = details.containsKey("Title") ? details.get("Title").toString() : null;
                String subTitle = details.containsKey("Sub-Title") ? details.get("Sub-Title").toString() : null;
                titles.add(Title.createTitle().setDelay(titleDelay).setFadeIn(fadein).setStay(stay).setFadeOut(fadeout).setTitle(title).setSubTitle(subTitle));
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{configPath}", fileName + ": " + configPath);
                LiteCommandEditorProperties.sendOperationMessage("LoadingTitleFailed", placeholders);
                ex.printStackTrace();
            }
        });
        return titles;
    }
    
    @Getter
    private String title = null;
    @Getter
    private String subTitle = null;
    @Getter
    private double fadeIn = 1;
    @Getter
    private double stay = 5;
    @Getter
    private double fadeOut = 1;
    @Getter
    private long delay = 0;
    
    public Title() {}
    
    public void send(Player player) {
        NMSUtil.TitleUtil.sendTitle(player, title, subTitle, fadeIn, stay, fadeOut);
    }
    
    public void send(Player player, Map<String, String> placeholders) {
        NMSUtil.TitleUtil.sendTitle(player, title, subTitle, fadeIn, stay, fadeOut, placeholders);
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        if (sender instanceof Player) {
            send((Player) sender, placeholders);
        }
    }

    @Override
    public String getIdentifier() {
        return "TitleAnimation";
    }
    
    public Title setDelay(long delay) {
        this.delay = delay;
        return this;
    }
    
    public Title setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public Title setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }
    
    public Title setFadeIn(double fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }
    
    public Title setStay(double stay) {
        this.stay = stay;
        return this;
    }
    
    public Title setFadeOut(double fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Delay=" + delay + ", Title=" + title + ", SubTitle=" + subTitle + ", FadeIn=" + fadeIn + ", Stay=" + stay + ", FadeOut=" + fadeOut;
    }
    
    public static Title createTitle() {
        return new Title();
    }
}
