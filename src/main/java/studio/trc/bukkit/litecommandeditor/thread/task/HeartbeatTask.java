package studio.trc.bukkit.litecommandeditor.thread.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import lombok.Getter;

import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorTask;
import studio.trc.bukkit.litecommandeditor.thread.LiteCommandEditorThread;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;

public class HeartbeatTask
    extends LiteCommandEditorTask
{
    @Getter
    private final boolean finished = false;

    /**
     * @param targetThread Target thread
     * @param mode 1 = heartbeat updater, 2 = heartbeat inspector.
     */
    public HeartbeatTask(LiteCommandEditorThread targetThread, int mode) {
        super("HeartbeatTask", () -> {
            switch (mode) {
                case 1: {
                    targetThread.setHeartbeat(System.currentTimeMillis());
                    break;
                }
                case 2: {
                    boolean blocked = !targetThread.isRunning() || !targetThread.isAlive();
                    if (!blocked) {
                        blocked = System.currentTimeMillis() - targetThread.getHeartbeat() >= LiteCommandEditorProperties.getLong("BlockedTime");
                    }
                    if (blocked) {
                        Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                        if (System.currentTimeMillis() - targetThread.getHeartbeat() < LiteCommandEditorProperties.getLong("CrashedTime")) {
                            placeholders.put("{thread}", targetThread.getName());
                            placeholders.put("{trace}",  targetThread.getStackTrace("", null, null).toString());
                            placeholders.put("{blockedTime}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() - targetThread.getHeartbeat())));
                            LiteCommandEditorProperties.sendOperationMessage("ThreadBlocked", placeholders);
                        } else {
                            placeholders.put("{thread}", targetThread.getName());
                            placeholders.put("{trace}",  targetThread.getStackTrace("", null, null).toString());
                            LiteCommandEditorProperties.sendOperationMessage("ThreadCrashed", placeholders);
                            LiteCommandEditorThread.initialize();
                        }
                    }
                    break;
                }
            }
        }, -1, 10);
    }
}
