package zg.dove.scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/*
 * 定时任务调度器
 */
public interface IScheduler extends Executor {
    /*
     * 调度器,无限次数
     *
     * @param command      要执行的回调函数
     * @param initialDelay 第一次调用前的延迟
     * @param period       调度周期
     * @param unit         时间单位
     */
    Future schedule(Runnable command, long initialDelay, long period, TimeUnit unit);

    /*
     * 调度器,仅一次
     *
     * @param command      要执行的回调函数
     * @param initialDelay 第一次调用前的延迟
     * @param unit         时间单位
     */
    Future schedule(Runnable command, long initialDelay, TimeUnit unit);
}
