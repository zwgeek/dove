package zg.dove.route;

public class DelegateRoute<FROM, KEY, DATA, RV> implements IRoute<FROM, KEY, DATA, RV> {
    private IRoute<FROM, KEY, DATA, RV> realRoute = null;
    public IRoute<FROM, KEY, DATA, RV> getRealRoute() {
        return realRoute;
    }

    public void setRealRoute(IRoute<FROM, KEY, DATA, RV> realMsgRoute) {
        this.realRoute = realMsgRoute;
    }

    @Override
    public RV trigger(FROM from, KEY key, DATA data) throws Exception {
        return realRoute.trigger(from, key, data);
    }

    @Override
    public void register(KEY key, Callback<FROM, KEY, DATA, RV> cb) {
        realRoute.register(key, cb);
    }

    @Override
    public void cancel(KEY key) {
        realRoute.cancel(key);
    }

    @Override
    public boolean search(KEY key) {
        return realRoute.search(key);
    }
}

