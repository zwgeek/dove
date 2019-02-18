package zg.dove.net;

import io.netty.channel.*;

import java.net.InetSocketAddress;

public class NetChannel {
    private ChannelHandlerContext channelHandlerContext;
    private NetSessionContext netSessionContext;

    public NetChannel() {
        this.netSessionContext = new NetSessionContext();
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }
    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public NetSessionContext getNetSessionContext() {
        return netSessionContext;
    }
    public void setNetSessionContext(NetSessionContext netSessionContext) {
        this.netSessionContext = netSessionContext;
    }

    public boolean isActive() {
        return channelHandlerContext != null && channelHandlerContext.channel().isActive();
    }

    public ChannelFuture write(Object msg) throws Exception {
        ChannelPromise future = channelHandlerContext.newPromise();
        channelHandlerContext.handler().write(channelHandlerContext, msg, future);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        return future;
    }

    public ChannelFuture writeAndFlush(Object msg) throws Exception {
        ChannelPromise future = channelHandlerContext.newPromise();
        channelHandlerContext.handler().write(channelHandlerContext, msg, future);
        channelHandlerContext.handler().flush(channelHandlerContext);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        return future;
    }

    public void flush() throws Exception {
        channelHandlerContext.handler().flush(channelHandlerContext);
    }

    public void close() throws Exception {
        channelHandlerContext.handler().close(channelHandlerContext, channelHandlerContext.newPromise());
    }

    public boolean isInEventLoop() {
        return this.channelHandlerContext.executor().inEventLoop();
    }

    /*
     * 获取对端地址
     * @return
     */
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
    }

    /*
     * 获取本端地址
     * @return
     */
    public InetSocketAddress localAddress() {
        return (InetSocketAddress) channelHandlerContext.channel().localAddress();
    }

    public Channel channel() {
        return channelHandlerContext.channel();
    }
}
