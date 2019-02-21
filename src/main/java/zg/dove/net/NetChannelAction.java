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
            Object response;
            NetSessionContext.removeAttribute(from, NetSessionContext.SCOPE_REQUEST);
            NetSessionContext.putAttribute(from, NetSessionContext.SCOPE_REQUEST, msg.getClass(), msg);

            response = route.trigger(from, msg.getClass(), msg);
            if (response != null) {
                NetChannel.writeAndFlush(from, response);
            }

            return null;
        });

        this.route.register(NetEvent.DATA_PROCESS_AFTER, (from, key, msg) -> { return null; });

        this.route.register(NetEvent.DISCONNECTED, (from, key, msg) -> {
            logger.debug("lost connection");
            return null;
        });

        this.route.register(NetEvent.CONNECTED, (from, key, msg) -> {
            logger.debug("new connection");
            return null;
        });

        this.route.register(NetEvent.READ_EXCEPTION, (from, key, msg) -> {
            if (msg instanceof IOException) {
                logger.error(String.format("read exception, force close, %s:%s", msg.getClass().getName(), ((IOException) msg).getMessage()));
            } else {
                logger.error("read exception, force close", msg);
            }
            if (from != null) {
                NetChannel.close(from);
            }
            return null;
        });

        this.route.register(NetEvent.WRITE_EXCEPTION, (from, key, msg) -> {
            if (msg instanceof IOException) {
                logger.error(String.format("write exception, force close, %s:%s", msg.getClass().getName(), ((IOException) msg).getMessage()));
            } else {
                logger.error("write exception, force close", msg);
            }
            if (from != null) {
                NetChannel.close(from);
            }
            return null;
        });

        this.route.register(NetEvent.READ_IDLE_TIMEOUT, (from, key, msg) -> {
            if (closeOnReadIdle) {
                logger.debug("connection read idle");
                NetChannel.close(from);
            }
            return null;
        });

        this.route.register(NetEvent.WRITE_IDLE_TIMEOUT, (from, key, msg) -> {
            if (closeOnWriteIdle) {
                logger.debug("connection write idle");
                NetChannel.close(from);
            } else if (heartTick != null) {
                NetChannel.writeAndFlush(from, heartTick);
            }
            return null;
        });

        this.route.register(NetEvent.ALL_IDLE_TIMEOUT, (from, key, msg) -> {
            if (closeOnReadIdle || closeOnWriteIdle) {
                logger.debug("connection all idle");
                NetChannel.close(from);
            }
            return null;
        });

        this.route.register(NetEvent.HIGH_WATER_WRITEABLE, (from, key, msg) -> {
            logger.debug("high water writeable");
            return null;
        });

        this.route.register(NetEvent.HIGH_WATER_UNWRITEABLE, (from, key, msg) -> {
            logger.debug("high water unwriteable");
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
