package zg.dove.route;

import java.util.concurrent.Executor;

/**
 * 把消息放入Executor中执行
 */
public class ExecutorRoute<FROM, KEY, DATA, RV> implements IRoute<FROM, KEY, DATA, RV> {
    private Executor executor;
    private IRoute<FROM, KEY, DATA, RV> route;

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public IRoute getRoute() {
        return route;
    }

    public void setRoute(IRoute<FROM, KEY, DATA, RV> route) {
        this.route = route;
    }

    @Override
    public RV trigger(FROM from, KEY key, DATA data) {
        executor.execute(() -> {
            try {
                route.trigger(from, key, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    @Override
    public void register(KEY key, Callback<FROM, KEY, DATA, RV> cb) {
        route.register(key, cb);
    }

    @Override
    public void cancel(KEY key) {
        route.cancel(key);
    }

    @Override
    public boolean search(KEY key) {
        return route.search(key);
    }
}
