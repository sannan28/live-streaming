package live.streaming.im.core.server.kafka;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

// 延迟任务
public class DelayedTask implements Delayed {

    // 任务到期时间
    private long executeTime;

    // 任务
    private Runnable task;

    public DelayedTask(long delay, Runnable task) {
        this.executeTime = System.currentTimeMillis() + delay;
        this.task = task;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.executeTime, ((DelayedTask) o).executeTime);
    }

    public void execute() {
        task.run();
    }
}
