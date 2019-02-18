package zg.dove.route;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DupKeyRoute<FROM, KEY, DATA, RV> implements IRoute<FROM, KEY, DATA, RV> {
    private Map<Object, List<Callback<FROM, KEY, DATA, RV>>> keyRoutes = new HashMap<>();
    private List<Callback<FROM, KEY, DATA, RV>> defaultRoutes = new ArrayList<>();

    @Override
    public RV trigger(FROM from, KEY key, DATA data) throws Exception {
        List<Callback<FROM, KEY, DATA, RV>> callbacks = keyRoutes.get(key);
        if (callbacks == null) {
            callbacks = defaultRoutes;
        }
        for (Callback<FROM, KEY, DATA, RV> callback : callbacks) {
            callback.call(from, key, data);
        }
        return null;
    }

    @Override
    public void register(KEY key, Callback<FROM, KEY, DATA, RV> cb) {
        if (key == null) {
            defaultRoutes.add(cb);
            return;
        }

        List<Callback<FROM, KEY, DATA, RV>> callbacks = keyRoutes.get(key);
        if (callbacks == null) {
            callbacks = new ArrayList<>();
            keyRoutes.put(key, callbacks);
        }
        callbacks.add(cb);
    }

    @Override
    public void cancel(KEY key) {
        if (key == null) {
            defaultRoutes.clear();
            return;
        }

        keyRoutes.remove(key);
    }

    @Override
    public boolean search(KEY key) {
        List routes = keyRoutes.get(key);
        return routes != null && routes.size() > 0;
    }
}
