package zg.dove.http.filter;

import io.netty.handler.codec.http.HttpResponseStatus;
import zg.dove.filter.IFilter;
import zg.dove.http.HttpException;
import zg.dove.http.HttpResponse;
import zg.dove.net.NetChannel;
import zg.dove.net.NetSessionContext;
import zg.dove.route.IRoute;

public class HttpStatusFilter implements IFilter {
    @Override
    public Object onFilterIn(Object context, Object msg) {
        return msg;
    }

    @Override
    public Object onFilterOut(Object context, Object msg) {
        return msg;
    }

    @Override
    public Throwable onFilterException(Object context, Throwable t) {
        if (t instanceof HttpException) {
            HttpResponse response = (HttpResponse)NetSessionContext.getAttribute(context, NetSessionContext.SCOPE_REQUEST, HttpResponse.class);
            if (t.getCause() instanceof IRoute.NoRouteToLogicException) {
                response.setStatus(HttpResponseStatus.NOT_FOUND.code());
                try {
                    NetChannel.writeAndFlush(context, response);
                } catch (Exception e) {
                    return e;
                }
            }

            return null;
        }

        return t;
    }
}
