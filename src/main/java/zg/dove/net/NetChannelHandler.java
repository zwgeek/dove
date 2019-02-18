package zg.dove.net;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.filter.DupFilter;
import zg.dove.filter.IFilter;
import zg.dove.route.DefaultRoute;
import zg.dove.route.IRoute;

public class NetChannelHandler extends ChannelHandlerAdapter {
    public static final Logger logger = LogManager.getLogger(NetChannelHandler.class);
    private static IFilter filter = new DupFilter();
    private static IRoute route = new DefaultRoute();

    protected NetChannel netChannel;

    public NetChannelHandler() {
        this.netChannel = new NetChannel();
    }

    public static void setFilter(IFilter filter) {
        NetChannelHandler.filter = filter;
    }

    public static void setRoute(IRoute route) {
        NetChannelHandler.route = route;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("channel active {}->{}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
        netChannel.setChannelHandlerContext(ctx);
        route.trigger(netChannel, NetEvent.CONNECTED, null);
        ctx.fireChannelActive();
    }


    protected Object _channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        return msg;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("recv msg:{}", msg.getClass().getName());

        Object _msg = this._channelRead(ctx, msg);
        if (_msg != null) {
            if (filter != null) {
                _msg = filter.onFilterIn(netChannel, _msg);
                if (_msg == null) {
                    logger.debug("msg filtered:{}", msg.getClass().getName());
                    ReferenceCountUtil.release(msg);
                    return;
                }
            }
            route.trigger(netChannel, NetEvent.DATA_PROCESS_BEFORE, _msg);
            route.trigger(netChannel, NetEvent.DATA, _msg);
            route.trigger(netChannel, NetEvent.DATA_PROCESS_AFTER, _msg);
        }

        ReferenceCountUtil.release(msg);
    }

    protected Object _write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        return msg;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.debug("send msg:{}", msg.getClass().getName());
        if (filter != null) {
            Object _msg = msg;
            msg = filter.onFilterOut(netChannel, msg);
            if (msg == null) {
                logger.debug("msg filtered:{}", _msg.getClass().getName());
                return;
            }
        }

        msg = this._write(ctx, msg, promise);
        if (msg == null) {
            return;
        }

        super.write(ctx, msg, promise);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    route.trigger(netChannel, NetEvent.READ_IDLE_TIMEOUT, null);
                    break;
                case WRITER_IDLE:
                    route.trigger(netChannel, NetEvent.WRITE_IDLE_TIMEOUT, null);
                    break;
                case ALL_IDLE:
                    route.trigger(netChannel, NetEvent.ALL_IDLE_TIMEOUT, null);
                    break;
                default:
                    logger.warn("unknown event {}:{}", evt.getClass().getName(), evt);
                    break;
            }
            return;
        }

        logger.warn("unknown event {}:{}", evt.getClass().getName(), evt);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("exception caught {}", cause.getMessage());
        if (filter != null) {
            Object _cause = cause;
            cause = filter.onFilterException(netChannel, cause);
            if (cause == null) {
                logger.debug("msg filtered:{}", _cause.getClass().getName());
                return;
            }
        }

        route.trigger(netChannel, NetEvent.READ_EXCEPTION, cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        route.trigger(netChannel, NetEvent.DISCONNECTED, null);
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            route.trigger(netChannel, NetEvent.HIGH_WATER_WRITEABLE, null);
        } else {
            route.trigger(netChannel, NetEvent.HIGH_WATER_UNWRITEABLE, null);
        }
        super.channelWritabilityChanged(ctx);
    }
}
