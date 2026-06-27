package studio.trc.bukkit.litecommandeditor.thread;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;
import lombok.Setter;

import studio.trc.bukkit.litecommandeditor.Main;
import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class LiteCommandEditorThread
    extends Thread
{
    @Getter
    private static LiteCommandEditorThread updaterThread = null;
    @Getter
    private static LiteCommandEditorThread taskThread = null;
    
    @Getter
    @Setter
    private boolean running = false;
    @Getter
    private final List<LiteCommandEditorTask> tasks = new CopyOnWriteArrayList<>();
    @Getter
    private volatile long heartbeat = System.currentTimeMillis();
    
    @Getter
    private final double delay;
    
    public LiteCommandEditorThread(String name, double delay) {
        super(name);
        this.delay = delay;
    }

    @Override
    public void run() {
        running = true;
        addHeartbeatTask();
        List<LiteCommandEditorTask> waitToExecute = new ArrayList<>();
        List<LiteCommandEditorTask> waitToRemove = new ArrayList<>();
        while (running) {
            try {
                long usedTime = System.currentTimeMillis();
                if (!tasks.isEmpty() && !PluginControl.isReloading()) {
                    waitToExecute.addAll(tasks);
                    waitToExecute.stream().filter(task -> {
                        if (task.isFinished()) {
                            waitToRemove.add(task);
                            return false;
                        }
                        return true;
                    }).forEach(task -> {
                        try {
                            task.execute();
                        } catch (Throwable t) {
                            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                            placeholders.put("{thread}", getName());
                            placeholders.put("{throwable}",  getStackTrace(t.getMessage() != null ? (t.getClass().getName() + ": " + t.getMessage()) : t.getClass().getName(), task, t).toString());
                            LiteCommandEditorProperties.sendOperationMessage("ThreadThrowed", placeholders);
                            waitToRemove.add(task);
                        }
                    });
                    if (!waitToRemove.isEmpty()) {
                        waitToRemove.stream().forEach(tasks::remove);
                        waitToRemove.clear();
                    }
                    if (!waitToExecute.isEmpty()) waitToExecute.clear();
                }
                long speed = ((long) (delay * 1000)) - (System.currentTimeMillis() - usedTime);
                if (speed >= 0) sleep(speed);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void addHeartbeatTask() {
        tasks.add(new LiteCommandEditorTask("HEARTBEAT", () -> {
            heartbeat = System.currentTimeMillis();
        }, -1, 10));
    }
    
    public boolean isBlocked() {
        if (!running || !isAlive()) return false;
        return System.currentTimeMillis() - heartbeat >= LiteCommandEditorProperties.getLong("BlockedTime");
    }
    
    public StringBuilder getStackTrace(String text, LiteCommandEditorTask task, Throwable throwable) {
        StringBuilder builder = new StringBuilder(text);
        builder.append("\n");
        StackTraceElement[] stackTrace = throwable != null ? throwable.getStackTrace() : getStackTrace();
        for (int i = 1; i < stackTrace.length; i++) {
            builder.append("  ├ ");
            builder.append(stackTrace[i].toString());
            builder.append("\n");
        }
        if (task != null) {
            StackTraceElement[] originalStackTrace = task.getOriginalStackTrace();
            builder.append("  └┐\n");
            for (int i = 0; i < originalStackTrace.length; i++) {
                if (i == stackTrace.length - 1) {
                    builder.append("   └ ");
                } else {
                    builder.append("   ├ ");
                }
                builder.append(originalStackTrace[i].toString());
                if (i < stackTrace.length - 1) {
                    builder.append("\n");
                }
            }
        } else {
            builder.append("  └ ");
        }
        return builder;
    }
    
    public static void initialize() {
        if (taskThread != null && taskThread.running) {
            taskThread.running = false;
        }
        taskThread = new LiteCommandEditorThread("LiteCommandEditor-TaskThread", ConfigurationType.CONFIG.getRobustConfig().getDouble("Async-Thread-Settings.Tick-Delay"));
        // A updater thread to check heartbeat
        if (updaterThread != null && updaterThread.running) {
            updaterThread.running = false;
        }
        updaterThread = new LiteCommandEditorThread("LiteCommandEditor-UpdaterThread", 1);
        
        updaterThread.tasks.add(new LiteCommandEditorTask(() -> {
            if (taskThread.isBlocked()) {
                Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
                if (taskThread.tasks.stream().anyMatch(task -> task.getIdentifier().equals("HEARTBEAT")) && System.currentTimeMillis() - taskThread.heartbeat < LiteCommandEditorProperties.getLong("CrashedTime")) {
                    placeholders.put("{thread}", taskThread.getName());
                    placeholders.put("{trace}",  taskThread.getStackTrace("", null, null).toString());
                    placeholders.put("{blockedTime}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() - taskThread.heartbeat)));
                    LiteCommandEditorProperties.sendOperationMessage("ThreadBlocked", placeholders);
                } else {
                    placeholders.put("{thread}", taskThread.getName());
                    placeholders.put("{trace}",  taskThread.getStackTrace("", null, null).toString());
                    LiteCommandEditorProperties.sendOperationMessage("ThreadCrashed", placeholders);
                    initialize();
                }
            }
        }, -1, 10));
        LiteCommandEditorProperties.sendOperationMessage("AsyncThreadStarted", MessageUtil.getDefaultPlaceholders());
        taskThread.start();
    }
    
    public static void runTask(Runnable task) {
        runTask(task, 0, null);
    }
    
    public static void runTask(Runnable task, long tickInterval) {
        runTask(task, tickInterval, null);
    }
    
    public static void runTask(Runnable task, long tickInterval, String identifier) {
        if ((taskThread == null || !taskThread.running || !taskThread.isAlive()) && Main.getInstance().isEnabled()) {
            initialize();
            Map<String, String> placeholders = MessageUtil.getDefaultPlaceholders();
            placeholders.put("{thread}", taskThread.getName());
            placeholders.put("{heartbeat}", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(taskThread.getHeartbeat())));
            LiteCommandEditorProperties.sendOperationMessage("NewTaskWhenThreadDown", placeholders);
        }
        taskThread.tasks.add(new LiteCommandEditorTask(identifier, task, 1, tickInterval));
    }
    
    public static boolean isRemoveDuplicateDelayedTasks() {
        return ConfigurationType.CONFIG.getRobustConfig().getBoolean("Async-Thread-Settings.Remove-Duplicate-Delayed-Tasks");
    }
}
