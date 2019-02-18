package zg.dove.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.filter.IFilter;
import zg.dove.route.IRoute;

import java.io.IOException;

public class NetChannelAction {
    public static final Logger logger = LogManager.getLogger(NetChannelAction.class);

    protected IRoute  route;
    protected IFilter filter;

    public void initRoute(IRoute route, boolean closeOnReadIdle, boolean closeOnWriteIdle, Object heartTick) {
        this.route = route;

        this.route.register(NetEvent.DATA_PROCESS_BEFORE, (from, key, msg) -> { return null; });

        this.route.register(NetEvent.DATA, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel)from;
            NetSessionContext netSessionContext = netChannel.getNetSessionContext();
            netSessionContext.removeAttribute(NetSessionContext.SCOPE_REQUEST);

            Object response;
            netSessionContext.putAttribute(NetSessionContext.SCOPE_REQUEST, msg.getClass(), msg);

            try {
                response = route.trigger(from, msg.getClass(), msg);
                if (response != null) {
                    netChannel.writeAndFlush(response);
                }
            } catch (Exception e) {
                logger.error("net msg event exception", e);
            }

            return null;
        });

        this.route.register(NetEvent.DATA_PROCESS_AFTER, (from, key, msg) -> { return null; });

        this.route.register(NetEvent.DISCONNECTED, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel) from;
            logger.debug("lost connection {}->{}", netChannel.remoteAddress(), netChannel.localAddress());
            return null;
        });

        this.route.register(NetEvent.CONNECTED, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel) from;
            logger.debug("new connection {}->{}", netChannel.remoteAddress(), netChannel.localAddress());
            return null;
        });

        this.route.register(NetEvent.READ_EXCEPTION, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel) from;
            if (msg instanceof IOException) {
                logger.error(String.format("read exception, force close, %s:%s", msg.getClass().getName(), ((IOException) msg).getMessage()));
            } else {
                logger.error("read exception, force close", msg);
            }
            if (netChannel != null) {
                netChannel.close();
            }
            return null;
        });

        this.route.register(NetEvent.WRITE_EXCEPTION, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel) from;
            if (msg instanceof IOException) {
                logger.error(String.format("write exception, force close, %s:%s", msg.getClass().getName(), ((IOException) msg).getMessage()));
            } else {
                logger.error("write exception, force close", msg);
            }
            if (netChannel != null) {
                netChannel.close();
            }
            return null;
        });

        this.route.register(NetEvent.READ_IDLE_TIMEOUT, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel) from;
            if (closeOnReadIdle) {
                logger.debug("connection {}->{} read idle", netChannel.remoteAddress(), netChannel.localAddress());
                netChannel.close();
            }
            return null;
        });

        this.route.register(NetEvent.WRITE_IDLE_TIMEOUT, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel) from;
            if (closeOnWriteIdle) {
                logger.debug("connection {}->{} write idle", netChannel.remoteAddress(), netChannel.localAddress());
                netChannel.close();
            } else if (heartTick != null) {
                netChannel.writeAndFlush(heartTick);
            }
            return null;
        });

        this.route.register(NetEvent.ALL_IDLE_TIMEOUT, (from, key, msg) -> {
            NetChannel netChannel = (NetChannel) from;
            if (closeOnReadIdle || closeOnWriteIdle) {
                logger.debug("connection {}->{} all idle", netChannel.remoteAddress(), netChannel.localAddress());
                netChannel.close();
            }
            return null;
        });
    }

    public void initFilter(IFilter filter) {
        this.filter = filter;
    }

    public void register(Class key, IRoute.Callback cb) {
        this.route.register(key, cb);
    }

    public void cancel(Class key) {
        this.route.cancel(key);
    }
}
