package zg.dove.http.filter;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.ssl.NotSslRecordException;
import zg.dove.filter.IFilter;
import zg.dove.http.HttpResponse;
import zg.dove.net.NetChannel;
import zg.dove.net.NetSessionContext;
import zg.dove.route.IRoute;

import java.nio.file.NoSuchFileException;

public class StatusFilter implements IFilter {
    @Override
    public Object onFilterIn(Object context, Object msg) throws Exception {
        return msg;
    }

    @Override
    public Object onFilterOut(Object context, Object msg) throws Exception {
        return msg;
    }

    @Override
    public Throwable onFilterException(Object context, Throwable t) {
        if (t instanceof NotSslRecordException) {
            return t;
        }

        HttpResponse response = (HttpResponse)NetSessionContext.getAttribute(context, NetSessionContext.SCOPE_REQUEST, HttpResponse.class);
        if (t instanceof IRoute.NoRouteToLogicException || t instanceof NoSuchFileException) {
            response.setStatus(HttpResponseStatus.NOT_FOUND.code());
        }
        else {
            response.setStatus(HttpResponseStatus.BAD_GATEWAY.code());
        }

        try {
            NetChannel.writeAndFlushAndClose(context, response);
        } catch (Exception e) {
            return e;
        }
        return null;
    }
}
