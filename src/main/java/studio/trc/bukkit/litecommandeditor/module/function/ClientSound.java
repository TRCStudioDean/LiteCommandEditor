package studio.trc.bukkit.litecommandeditor.module.function;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.module.command.CommandFunctionTask;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public class ClientSound
    implements CommandFunctionTask
{
    public static void sendSounds(List<ClientSound> sounds, Player player) {
        long tick = 0;
        if (LiteCommandEditorThread.isRemoveDuplicateDelayedTasks()) {
            LiteCommandEditorThread.getTaskThread().getTasks().removeIf(task -> task.getIdentifier() != null && task.getIdentifier().equals("ClientSound:" + player.getUniqueId()));
        }
        for (ClientSound sound : sounds) {
            LiteCommandEditorThread.runTask(() -> sound.send(player), tick, "ClientSound:" + player.getUniqueId());
            tick += sound.getDelay();
        }
    }
    
    public static ClientSound build(Map map, String fileName, String configPath) {
        try {
            long soundDelay = map.containsKey("Delay") ? Long.valueOf(map.get("Delay").toString()) : 0;
            float pitch = map.containsKey("Pitch") ? Float.valueOf(map.get("Pitch").toString()) : 0;
            float volume = map.containsKey("Volume") ? Float.valueOf(map.get("Volume").toString()) : 0;
            boolean broadcast = map.containsKey("Broadcast") ? Boolean.valueOf(map.get("Broadcast").toString()) : false; 
            Sound sound = Sound.valueOf(map.get("Sound").toString().toUpperCase());
            return ClientSound.createClientSound().setDelay(soundDelay).setSound(sound).setPitch(pitch).setVolume(volume).setBroadcast(broadcast);
        } catch (Exception ex) {
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
            placeholders.put("{configPath}", fileName + ": " + configPath);
            LiteCommandEditorProperties.sendOperationMessage("LoadingClientSoundFailed", placeholders);
            ex.printStackTrace();
            return null;
        }
    }
    
    public static List<ClientSound> build(List<Map> maps, String fileName, String configPath) {
        List<ClientSound> sounds = new LinkedList();
        maps.stream().forEach(details -> {
            try {
                long soundDelay = details.containsKey("Delay") ? Long.valueOf(details.get("Delay").toString()) : 0;
                float pitch = details.containsKey("Pitch") ? Float.valueOf(details.get("Pitch").toString()) : 0;
                float volume = details.containsKey("Volume") ? Float.valueOf(details.get("Volume").toString()) : 0;
                boolean broadcast = details.containsKey("Broadcast") ? Boolean.valueOf(details.get("Broadcast").toString()) : false; 
                Sound sound = Sound.valueOf(details.get("Sound").toString().toUpperCase());
                sounds.add(ClientSound.createClientSound().setDelay(soundDelay).setSound(sound).setPitch(pitch).setVolume(volume).setBroadcast(broadcast));
            } catch (Exception ex) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                placeholders.put("{exception}", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "null");
                placeholders.put("{configPath}", fileName + ": " + configPath);
                LiteCommandEditorProperties.sendOperationMessage("LoadingClientSoundFailed", placeholders);
                ex.printStackTrace();
            }
        });
        return sounds;
    }
    
    @Getter
    private long delay = 0;
    @Getter
    private float volume = 1;
    @Getter
    private float pitch = 1;
    @Getter
    private Sound sound;
    @Getter
    private boolean broadcast;
    
    public ClientSound() {}
    
    public void send(Player player) {
        if (broadcast) {
            Bukkit.getOnlinePlayers().stream().forEach(p -> p.playSound(player.getLocation(), sound, volume, pitch));
        } else {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    @Override
    public void executeTask(CommandSender sender, Map<String, String> placeholders) {
        if (sender instanceof Player) {
            send((Player) sender);
        }
    }

    @Override
    public String getIdentifier() {
        return "ClientSound";
    }
    
    public ClientSound setDelay(long delay) {
        this.delay = delay;
        return this;
    }
    
    public ClientSound setVolume(float volume) {
        this.volume = volume;
        return this;
    }
    
    public ClientSound setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }
    
    public ClientSound setSound(Sound sound) {
        this.sound = sound;
        return this;
    }
    
    public ClientSound setBroadcast(boolean value) {
        this.broadcast = value;
        return this;
    }

    @Override
    public String toString() {
        return "[" + getIdentifier() + "]: Delay=" + delay + ", Sound=" + sound.name() + ", Volume=" + volume + ", Pitch=" + pitch + ", Broadcast=" + broadcast;
    }
    
    public static ClientSound createClientSound() {
        return new ClientSound();
    }
}
