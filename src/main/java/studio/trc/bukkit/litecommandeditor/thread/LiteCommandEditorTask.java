package studio.trc.bukkit.litecommandeditor.thread;

import lombok.Getter;

public class LiteCommandEditorTask
{
    @Getter
    private final Runnable task;
    @Getter
    private final String identifier;
    @Getter
    private final long totalExecuteTimes;
    @Getter
    private final long tickInterval;
    @Getter
    private long executeTimes = 0;
    @Getter
    private long tickedTimes = 0;

    public LiteCommandEditorTask(Runnable task, long totalExecuteTimes) {
        this.task = task;
        this.totalExecuteTimes = totalExecuteTimes;
        tickInterval = 0;
        identifier = null;
    }

    public LiteCommandEditorTask(Runnable task, long totalExecuteTimes, long tickInterval) {
        this.task = task;
        this.totalExecuteTimes = totalExecuteTimes;
        this.tickInterval = tickInterval;
        identifier = null;
    }

    public LiteCommandEditorTask(Runnable task, long totalExecuteTimes, long tickInterval, String identifier) {
        this.task = task;
        this.totalExecuteTimes = totalExecuteTimes;
        this.tickInterval = tickInterval;
        this.identifier = identifier;
    }
    
    public void run() {
        if (totalExecuteTimes != -1 || tickInterval > 0) {
            tickedTimes++;
        }
        if (tickInterval <= 0 || tickedTimes % tickInterval == 0) {
            task.run();
            executeTimes++;
        }
    }
}
