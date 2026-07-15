package studio.trc.bukkit.litecommandeditor.thread;

import lombok.Getter;
import lombok.Setter;

public class LiteCommandEditorTask
{
    /**
     * Identifer for differentiation.
     */
    @Getter
    private final String identifier;
    
    /**
     * Task function.
     */
    @Getter
    private final Runnable task;
    
    /**
     * Total execute times.
     */
    @Getter
    private final long totalExecuteTimes;
    
    /**
     * Tick intervals
     */
    @Getter
    private final long tickInterval;
    
    /**
     * Stack trace (for tracking exceptions)
     */
    @Getter
    private final StackTraceElement[] originalStackTrace;
    
    /**
     * Current total executed times.
     */
    @Getter
    @Setter
    private long executeTimes = 0;
    
    /**
     * Current ticked times.
     */
    @Getter
    @Setter
    private long tickedTimes = 0;

    public LiteCommandEditorTask(Runnable task, long totalExecuteTimes, long tickInterval) {
        this.task = task;
        this.totalExecuteTimes = totalExecuteTimes;
        this.tickInterval = tickInterval;
        originalStackTrace = Thread.currentThread().getStackTrace();
        identifier = null;
    }

    public LiteCommandEditorTask(String identifier, Runnable task, long totalExecuteTimes, long tickInterval) {
        this.identifier = identifier;
        this.task = task;
        this.totalExecuteTimes = totalExecuteTimes;
        this.tickInterval = tickInterval;
        originalStackTrace = Thread.currentThread().getStackTrace();
    }
    
    /**
     * Execute the task.
     */
    public void execute() {
        if (totalExecuteTimes != -1 || tickInterval > 0) {
            tickedTimes++;
        }
        if (tickInterval <= 0 || tickedTimes % tickInterval == 0) {
            try {
                task.run();
            } finally {
                executeTimes++;
            }
        }
    }
    
    /**
     * @return Is task finished
     */
    public boolean isFinished() {
        return totalExecuteTimes != -1 && executeTimes >= totalExecuteTimes;
    }
}
