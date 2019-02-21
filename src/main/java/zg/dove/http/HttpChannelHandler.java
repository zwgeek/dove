package zg.dove.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import zg.dove.filter.IFilter;
import zg.dove.net.NetChannelHandler;
import zg.dove.net.NetSessionContext;
import zg.dove.route.IRoute;

@ChannelHandler.Sharable
public class HttpChannelHandler extends NetChannelHandler {

    public HttpChannelHandler(IFilter filter, IRoute route) {
        super(filter, route);
    }

    private HttpResponse _createResponse() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "X-Requested-With");
        response.setHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET");
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
        return response;
    }

    @Override
    protected Object _channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            // NOTICE:为了实现上简单，我们现在只考虑整包的处理
            return this._channelRead0(ctx, msg);
        }

        HttpResponse response = this._createResponse();
        NetSessionContext.removeAttribute(ctx, NetSessionContext.SCOPE_REQUEST);
        NetSessionContext.putAttribute(ctx, NetSessionContext.SCOPE_REQUEST, HttpResponse.class, response);

        return new HttpRequest(((FullHttpRequest)msg));
    }

    private Object _channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            throw new RuntimeException("not support " + msg.getClass().getName());
        }

        HttpResponse response = new HttpResponse((FullHttpResponse)msg);
        NetSessionContext.putAttribute(ctx, NetSessionContext.SCOPE_REQUEST, HttpResponse.class, response);

        return NetSessionContext.getAttribute(ctx, HttpRequest.class);
    }

    @Override
    protected Object _write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof HttpResponse)) {
            return this._write0(ctx, msg);
        }

        ((HttpResponse)msg).setHeader(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(((HttpResponse)msg).size()));
        return ((HttpResponse)msg).getResponse();
    }

    private Object _write0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            throw new RuntimeException("not support " + msg.getClass().getName());
        }

        NetSessionContext.putAttribute(ctx, NetSessionContext.SCOPE_REQUEST, HttpRequest.class, msg);
        return ((HttpRequest)msg).getRequest();
    }
}
