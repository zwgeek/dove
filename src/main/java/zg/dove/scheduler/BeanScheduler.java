package zg.dove.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanScheduler {
    /*
     * 第一次执行前的延迟
     *
     * @return
     */
    int delay() default 0;

    /*
     * 调度周期
     *
     * @return
     */
    int period() default 0;

    /*
     * 是否循环
     *
     * @return
     */
    boolean loop() default false;

    /*
     * delay、period的时间单位
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
