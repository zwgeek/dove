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
    private Map<Object, Callback<FROM, DATA, RV>> keyRoutes = new HashMap<>();
    private Callback<FROM, DATA, RV> defaultRoutes = null;

    public Object trigger(FROM from, KEY key, DATA data, RV result) throws Exception {
        Callback<FROM, DATA, RV> fcb = keyRoutes.get(key);
        if (fcb != null) {
            return fcb.call(from, data, result);
        } else if (defaultRoutes != null) {
            return defaultRoutes.call(from, data, result);
        } else {
            throw new IRoute.NoRouteToLogicException();
        }
    }

    public void register(KEY key, Callback<FROM, DATA, RV> cb) {
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
    public interface Callback<FROM, DATA, RV> {
        Object call(FROM from, DATA data, RV result) throws Exception;
    }
}
