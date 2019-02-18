package zg.dove.route;


public class ExceptionFilterRoute<FROM, KEY, DATA, RV> implements IRoute<FROM, KEY, DATA, RV> {
    private IRoute<FROM, KEY, DATA, RV> rawRoute;
    private Class filterException;

    public ExceptionFilterRoute(IRoute<FROM, KEY, DATA, RV> rawRoute, Class filterException) {
        this.rawRoute = rawRoute;
        this.filterException = filterException;
    }

    public ExceptionFilterRoute() {
    }

    public IRoute<FROM, KEY, DATA, RV> getRawRoute() {
        return rawRoute;
    }

    public void setRawRoute(IRoute<FROM, KEY, DATA, RV> rawRoute) {
        this.rawRoute = rawRoute;
    }

    public Class getFilterException() {
        return filterException;
    }

    public void setFilterException(Class filterException) {
        this.filterException = filterException;
    }

    @Override
    public RV trigger(FROM from, KEY key, DATA data) throws Exception {
        try {
            return rawRoute.trigger(from, key, data);
        } catch (Throwable e) {
            if (!filterException.isAssignableFrom(e.getClass())) {
                throw e;
            }

            return null;
        }
    }

    @Override
    public void register(KEY key, Callback<FROM, KEY, DATA, RV> cb) {
        rawRoute.register(key, cb);
    }

    @Override
    public void cancel(KEY key) {
        rawRoute.cancel(key);
    }

    @Override
    public boolean search(KEY key) {
        return rawRoute.search(key);
    }
}

