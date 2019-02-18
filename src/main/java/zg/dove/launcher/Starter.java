package zg.dove.launcher;

import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

public abstract class Starter {
    /**
     * 这里保存一下
     */
    protected static ScheduledExecutorService eventLoopGroup;
    /**
     * 开始
     */
    protected abstract void start(Properties properties) throws Exception;

    /**
     * 结束
     */
    protected abstract void finish() throws Exception;
}
