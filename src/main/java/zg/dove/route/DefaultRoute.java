package zg.dove.route;

import java.util.HashMap;
import java.util.Map;

public class DefaultRoute<FROM, KEY, DATA, RV> implements IRoute<FROM, KEY, DATA, RV> {
    private Map<Object, Callback<FROM, KEY, DATA, RV>> keyRoutes = new HashMap<>();
    private Callback<FROM, KEY, DATA, RV> defaultRoutes = null;

    @Override
    public RV trigger(FROM from, KEY key, DATA data) throws Exception {
        Callback<FROM, KEY, DATA, RV> fcb = keyRoutes.get(key);
        if (fcb != null) {
            return fcb.call(from, key, data);
        } else if (defaultRoutes != null) {
            return defaultRoutes.call(from, key, data);
        } else {
            throw new NoRouteToLogicException();
        }
    }

    @Override
    public void register(KEY key, Callback<FROM, KEY, DATA, RV> cb) {
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

    @Override
    public void cancel(KEY key) {
        if (key == null) {
            if (defaultRoutes != null) {
                defaultRoutes = null;
            }
            return;
        }

        keyRoutes.remove(key);
    }

    @Override
    public boolean search(KEY key) {
        return keyRoutes.containsKey(key);
    }
}


