package zg.dove.http;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.filter.DupFilter;
import zg.dove.filter.IFilter;
import zg.dove.net.NetChannel;
import zg.dove.net.NetChannelAction;
import zg.dove.net.NetEvent;
import zg.dove.net.NetSessionContext;
import zg.dove.route.IRoute;
import zg.dove.route.RefRoute;

import java.util.HashMap;
import java.util.Map;

public class HttpChannelAction extends NetChannelAction {
    public static final Logger logger = LogManager.getLogger(HttpChannelAction.class);

    private Map<Object, RefRoute> methodRoutes = new HashMap<>();

    @Override
    public void initRoute(IRoute route, boolean closeOnReadIdle, boolean closeOnWriteIdle, Object heartTick) {
        //init
        super.initRoute(route, closeOnReadIdle, closeOnWriteIdle, heartTick);

        methodRoutes.put(BeanHttp.Method.OPTIONS, new RefRoute());
        methodRoutes.put(BeanHttp.Method.GET, new RefRoute());
        methodRoutes.put(BeanHttp.Method.HEAD, new RefRoute());
        methodRoutes.put(BeanHttp.Method.POST, new RefRoute());
        methodRoutes.put(BeanHttp.Method.PUT, new RefRoute());
        methodRoutes.put(BeanHttp.Method.DELETE, new RefRoute());
        methodRoutes.put(BeanHttp.Method.TRACE, new RefRoute());
        methodRoutes.put(BeanHttp.Method.CONNECT, new RefRoute());

        this.route.register(NetEvent.DATA, (from, key, msg) -> {
            HttpRequest request = (HttpRequest)msg;
            RefRoute _route = methodRoutes.get(request.method());
            HttpResponse response = (HttpResponse)NetSessionContext.getAttribute(from, NetSessionContext.SCOPE_REQUEST, HttpResponse.class);
            //NetSessionContext.putAttribute(from, NetSessionContext.SCOPE_REQUEST, msg.getClass(), msg);

            Object bool = _route.trigger(from, request.getId(), msg, response);
            if (bool != null) {
                NetChannel.writeAndFlushAndClose(from, response);
            }

            return null;
        });
    }

    @Override
    public void initFilter(IFilter filter) {
        if (!(filter instanceof DupFilter)) {
            logger.warn("http filter must be DupFilter");
            return;
        }
        super.initFilter(filter);
    }

    public void register(String method, String url, RefRoute.Callback cb) {
        methodRoutes.get(method).register(url, cb);
    }

    public void cancel(String method, String key) {
        methodRoutes.get(method).cancel(key);
    }

    public boolean addFilter(IFilter filter) {
        return ((DupFilter)this.filter).add(filter);
    }

    public boolean removeFilter(IFilter filter) {
        return ((DupFilter)this.filter).remove(filter);
    }
}
