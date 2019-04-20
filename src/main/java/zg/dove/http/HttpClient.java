package zg.dove.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import zg.dove.net.NetChannel;
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
        byte[] bytes = msg.getBytes("utf-8");
        HttpRequest request = new HttpRequest(HttpVersion.HTTP_1_1, HttpMethod.valueOf(method), "/", Unpooled.wrappedBuffer(bytes));
        request.setHeader(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.setHeader(HttpHeaderNames.ACCEPT_LANGUAGE, "zh-CN");
        request.setHeader(HttpHeaderNames.ACCEPT_ENCODING, "gzip, deflate");
        request.setHeader(HttpHeaderNames.HOST, ip);
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

    public static void connect(String method, String ip, int port, String msg, RefRoute.Callback cb) throws Exception {
        HttpClient.getStream().connect(ip, port, (ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                HttpRequest request = HttpClient._createRequest(method, ip, msg);
                request.setId(future.channel().id().toString());
                future.channel().write(request);
                future.channel().flush();

                HttpChannelAction _action = HttpClient.getAction();
                String key = future.channel().id().toString();
                _action.register(method, key, (context, _request, _response) -> {
                    cb.call(context, _request, _response);
                    _action.cancel(method, key);
                    NetChannel.close(context);
                    return null;
                });
            }
        });
    }

    public static void connect(String method, String ip, int port, String msg) throws Exception {
        HttpClient.connect(method, ip, port, msg);

    }
}
