package studio.trc.bukkit.litecommandeditor.thread;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;
import studio.trc.bukkit.litecommandeditor.util.LiteCommandEditorProperties;
import studio.trc.bukkit.litecommandeditor.util.PluginControl;

public class LiteCommandEditorThread
    extends Thread
{
    @Getter
    private static LiteCommandEditorThread taskThread = null;
    
    @Getter
    @Setter
    private boolean running = false;
    @Getter
    private final List<LiteCommandEditorTask> tasks = new LinkedList<>();
    
    @Getter
    private final double delay;
    
    public LiteCommandEditorThread(double delay) {
        super("LiteCommandEditor-Pool");
        this.delay = delay;
    }

    @Override
    public void run() {
        running = true;
        List<LiteCommandEditorTask> waitToExecute = new LinkedList<>();
        List<LiteCommandEditorTask> waitToRemove = new LinkedList<>();
        while (running) {
            try {
                long usedTime = System.currentTimeMillis();
                if (!PluginControl.isReloading()) {
                    waitToExecute.clear();
                    synchronized (tasks) {
                        waitToExecute.addAll(tasks);
                    }
                    waitToExecute.stream().filter(task -> {
                        if (task.getTotalExecuteTimes() != -1 && task.getExecuteTimes() >= task.getTotalExecuteTimes()) {
                            waitToRemove.add(task);
                            return false;
                        }
                        return true;
                    }).forEach(LiteCommandEditorTask::run);
                    if (!waitToRemove.isEmpty()) {
                        waitToRemove.stream().forEach(task -> {
                            synchronized (tasks) {
                                tasks.remove(task);
                            }
                        });
                        waitToRemove.clear();
                    }
                }
                long speed = ((long) (delay * 1000)) - (System.currentTimeMillis() - usedTime);
                if (speed >= 0) sleep(speed);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void initialize() {
        if (taskThread != null && taskThread.running) {
            taskThread.running = false;
        }
        taskThread = new LiteCommandEditorThread(ConfigurationType.CONFIG.getRobustConfig().getDouble("Async-Thread-Settings.Tick-Delay"));
        LiteCommandEditorProperties.sendOperationMessage("AsyncThreadStarted", MessageUtil.getDefaultPlaceholders());
        taskThread.start();
    }
    
    public static void runTask(Runnable task) {
        synchronized (taskThread.tasks) {
            taskThread.tasks.add(new LiteCommandEditorTask(task, 1));
        }
    }
    
    public static void runTask(Runnable task, long tickInterval) {
        synchronized (taskThread.tasks) {
            taskThread.tasks.add(new LiteCommandEditorTask(task, 1, tickInterval));
        }
    }
    
    public static void runTask(Runnable task, long tickInterval, String identifier) {
        synchronized (taskThread.tasks) {
            taskThread.tasks.add(new LiteCommandEditorTask(task, 1, tickInterval, identifier));
        }
    }
    
    public static boolean isRemoveDuplicateDelayedTasks() {
        return ConfigurationType.CONFIG.getRobustConfig().getBoolean("Async-Thread-Settings.Remove-Duplicate-Delayed-Tasks");
    }
}
