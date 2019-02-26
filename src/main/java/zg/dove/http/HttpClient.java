package zg.dove.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import zg.dove.route.RefRoute;
import zg.dove.net.NetStream;

public class HttpClient {
    private static NetStream stream;
    private static HttpChannelAction action;

    private static NetStream getStream() {
        if (stream == null) {
            throw new RuntimeException("NetStream[Net] is not init");
        }
        return stream;
    }

    public static void setStream(NetStream stream) {
        HttpClient.stream = stream;
    }

    private static HttpChannelAction getAction() {
        if (action == null) {
            throw new RuntimeException("NetChannelAction is not init");
        }
        return action;
    }

    public static void setAction(HttpChannelAction action) {
        HttpClient.action = action;
    }

    private static HttpRequest _createRequest(String method, String ip, String msg) throws Exception {
        HttpRequest request = new HttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method), ip, Unpooled.wrappedBuffer(msg.getBytes("utf-8")));
        request.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
        return request;
    }

    public static void get(String ip, int port, String msg) throws Exception {
        HttpClient.connect(BeanHttp.Method.GET, ip, port, msg);
    }

    public static void get(String ip, int port, String msg, RefRoute.Callback cb) throws Exception {
        HttpClient.connect(BeanHttp.Method.GET, ip, port, msg, cb);
    }

    public static void post(String ip, int port, String msg) throws Exception {
        HttpClient.connect(BeanHttp.Method.POST, ip, port, msg);
    }

    public static void post(String ip, int port, String msg, RefRoute.Callback cb) throws Exception {
        HttpClient.connect(BeanHttp.Method.GET, ip, port, msg, cb);
    }

    public static void connect(String method, String ip, int port, String msg) throws Exception {
        HttpClient.getStream().connect(ip, port, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    future.channel().write(HttpClient._createRequest(method, ip, msg));
                }
            }
        });
    }

    public static void connect(String method, String ip, int port, String msg, RefRoute.Callback cb) throws Exception {
        HttpClient.connect(method, ip, port, msg);

        HttpChannelAction _action = HttpClient.getAction();

        _action.register(method, ip, (context, request, response) -> {
            cb.call(context, request, response);
            _action.cancel(method, ip);
            return false;
        });
    }
}
