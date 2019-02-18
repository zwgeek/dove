package zg.dove.route;

import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个特别的靠引用拿结果的route
 * @param <FROM>
 * @param <KEY>
 * @param <DATA>
 * @param <RV>
 */
public class RefRoute<FROM, KEY, DATA, RV> {
    private Map<Object, Callback<DATA, RV>> keyRoutes = new HashMap<>();
    private Callback<DATA, RV> defaultRoutes = null;

    public boolean trigger(FROM from, KEY key, DATA data, RV result) throws Exception {
        Callback<DATA, RV> fcb = keyRoutes.get(key);
        if (fcb != null) {
            return fcb.call(data, result);
        } else if (defaultRoutes != null) {
            return defaultRoutes.call(data, result);
        } else {
            throw new IRoute.NoRouteToLogicException();
        }
    }

    public void register(KEY key, Callback<DATA, RV> cb) {
        if (key == null) {
            if (defaultRoutes != null) {
                throw new RuntimeException("duplicate default process");
            }
            defaultRoutes = cb;
            return;
        }

        //if (keyRoutes.containsKey(key)) {}

        keyRoutes.put(key, cb);
    }

    public void cancel(KEY key) {
        if (key == null) {
            if (defaultRoutes != null) {
                defaultRoutes = null;
            }
            return;
        }

        keyRoutes.remove(key);
    }

    public boolean search(KEY key) {
        return keyRoutes.containsKey(key);
    }

    /*
     * 事件回调函数 FROM, KEY,
     */
    @FunctionalInterface
    public interface Callback<DATA, RV> {
        boolean call(DATA data, RV result) throws Exception;
    }
}
