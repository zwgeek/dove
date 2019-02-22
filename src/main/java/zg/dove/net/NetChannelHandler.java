package zg.dove.net;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.filter.IFilter;
import zg.dove.route.IRoute;

@ChannelHandler.Sharable
public class NetChannelHandler extends ChannelHandlerAdapter {
    public static final Logger logger = LogManager.getLogger(NetChannelHandler.class);
    private IFilter filter;
    private IRoute route;

    public NetChannelHandler(IFilter filter, IRoute route) {
        this.filter= filter;
        this.route = route;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        route.trigger(ctx, NetEvent.CONNECTED, null);
        ctx.fireChannelActive();
    }


    protected Object _channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        return msg;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object _msg = this._channelRead(ctx, msg);
        if (_msg != null) {
            if (filter != null) {
                _msg = filter.onFilterIn(ctx, _msg);
                if (_msg == null) {
                    logger.debug("recv filtered : {}", msg);
                    ReferenceCountUtil.release(msg);
                    return;
                }
            }
            logger.debug("recv msg : {}", msg);
            route.trigger(ctx, NetEvent.DATA_PROCESS_BEFORE, _msg);
            route.trigger(ctx, NetEvent.DATA, _msg);
            route.trigger(ctx, NetEvent.DATA_PROCESS_AFTER, _msg);
        }

        ReferenceCountUtil.release(msg);
    }

    protected Object _write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        return msg;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (filter != null) {
            Object _msg = msg;
            msg = filter.onFilterOut(ctx, msg);
            if (msg == null) {
                logger.debug("send filtered : {}", _msg);
                return;
            }
        }

        msg = this._write(ctx, msg, promise);
        if (msg == null) {
            return;
        }

        logger.debug("send msg : {}", msg);
        super.write(ctx, msg, promise);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    route.trigger(ctx, NetEvent.READ_IDLE_TIMEOUT, null);
                    break;
                case WRITER_IDLE:
                    route.trigger(ctx, NetEvent.WRITE_IDLE_TIMEOUT, null);
                    break;
                case ALL_IDLE:
                    route.trigger(ctx, NetEvent.ALL_IDLE_TIMEOUT, null);
                    break;
                default:
                    logger.warn("unknown event : {}", evt);
                    break;
            }
            return;
        }
        else if (evt instanceof SslHandshakeCompletionEvent) {
            logger.debug("ssl handshake done");
            return;
        }

        logger.warn("unknown event : {}", evt);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (filter != null) {
            Object _cause = cause;
            cause = filter.onFilterException(ctx, cause);
            if (cause == null) {
                logger.debug("exception filtered : {}", _cause);
                return;
            }
        }

        logger.warn("exception caught : {}", cause);
        route.trigger(ctx, NetEvent.READ_EXCEPTION, cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        route.trigger(ctx, NetEvent.DISCONNECTED, null);
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            route.trigger(ctx, NetEvent.HIGH_WATER_WRITEABLE, null);
        } else {
            route.trigger(ctx, NetEvent.HIGH_WATER_UNWRITEABLE, null);
        }
        super.channelWritabilityChanged(ctx);
    }
}
