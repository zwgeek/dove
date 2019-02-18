package zg.dove.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import zg.dove.route.IRoute;

public class NetClient {
    private static NetStream stream;
    private static NetChannelAction action;

    private static NetStream getStream() {
        if (stream == null) {
            throw new RuntimeException("NetStream[Net] is not init");
        }
        return stream;
    }

    public static void setStream(NetStream stream) {
        NetClient.stream = stream;
    }

    private static NetChannelAction getAction() {
        if (action == null) {
            throw new RuntimeException("NetChannelAction is not init");
        }
        return action;
    }

    public static void setAction(NetChannelAction action) {
        NetClient.action = action;
    }

    public static void connect(String ip, int port, Object msg) throws Exception {
        NetClient.getStream().connect(ip, port, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    future.channel().write(msg);
                }
            }
        });
    }

    public static void connect(String ip, int port, Object msg, Class key, IRoute.Callback cb) throws Exception {
        NetClient.connect(ip, port, msg);

//        if (key == null) {
//            return;
//        }

        NetChannelAction _action = NetClient.getAction();
        _action.register(key, (_from, _key, _data) -> {
            cb.call(_from, _key, _data);
            _action.cancel(key);
            return null;
        });
    }
}
