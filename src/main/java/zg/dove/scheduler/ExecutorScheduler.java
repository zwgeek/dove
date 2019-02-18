package zg.dove.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.net.NetStream;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorScheduler implements IScheduler {
    public static final Logger logger = LogManager.getLogger(NetStream.class);

    private ScheduledExecutorService scheduledExecutorService;

    public ExecutorScheduler(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public Future schedule(Runnable method, long initialDelay, long period, TimeUnit unit) {
        return scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                method.run();
            } catch (Throwable e) {
                logger.warn("exception caught while run schedule command", e);
            }
        }, initialDelay, period, unit);
    }

    @Override
    public Future schedule(Runnable method, long initialDelay, TimeUnit unit) {
        return scheduledExecutorService.schedule(() -> {
            try {
                method.run();
            } catch (Throwable e) {
                logger.warn("exception caught while run schedule command", e);
            }
        }, initialDelay, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.scheduledExecutorService.execute(command);
    }

}
