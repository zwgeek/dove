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
    public Object onFilterIn(NetChannel ch, Object msg) {
        return msg;
    }

    @Override
    public Object onFilterOut(NetChannel ch, Object msg) {
        return msg;
    }

    @Override
    public Throwable onFilterException(NetChannel ch, Throwable t) {
        if (t instanceof HttpException) {
            NetSessionContext netSessionContext = ch.getNetSessionContext();
            HttpResponse response = (HttpResponse)netSessionContext.getAttribute(NetSessionContext.SCOPE_REQUEST, HttpResponse.class);
            if (t.getCause() instanceof IRoute.NoRouteToLogicException) {
                response.setStatus(HttpResponseStatus.NOT_FOUND.code());
                try {
                    ch.writeAndFlush(response);
                } catch (Exception e) {
                    return e;
                }
            }

            return null;
        }

        return t;
    }
}
