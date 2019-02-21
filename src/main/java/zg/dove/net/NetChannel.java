package zg.dove.net;

import io.netty.channel.*;

import java.net.InetSocketAddress;

public class NetChannel {

    public static boolean isActive(Object channelHandlerContext) {
        return channelHandlerContext != null && ((ChannelHandlerContext)channelHandlerContext).channel().isActive();
    }

    public static ChannelFuture write(Object channelHandlerContext, Object msg) throws Exception {
        ChannelHandlerContext context = ((ChannelHandlerContext)channelHandlerContext);
        ChannelPromise future = context.newPromise();
        context.handler().write(context, msg, future);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        return future;
    }

    public static ChannelFuture writeAndFlush(Object channelHandlerContext, Object msg) throws Exception {
        ChannelHandlerContext context = ((ChannelHandlerContext)channelHandlerContext);
        ChannelPromise future = context.newPromise();
        context.handler().write(context, msg, future);
        context.handler().flush(context);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        return future;
    }

    public static ChannelFuture writeAndFlushAndClose(Object channelHandlerContext, Object msg) throws Exception {
        ChannelHandlerContext context = ((ChannelHandlerContext)channelHandlerContext);
        ChannelPromise future = context.newPromise();
        context.handler().write(context, msg, future);
        context.handler().flush(context);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        future.addListener(ChannelFutureListener.CLOSE);
        return future;
    }

    public static void flush(Object channelHandlerContext) throws Exception {
        ChannelHandlerContext context = ((ChannelHandlerContext)channelHandlerContext);
        context.handler().flush(context);
    }

    public static void close(Object channelHandlerContext) throws Exception {
        ChannelHandlerContext context = ((ChannelHandlerContext)channelHandlerContext);
        context.handler().close(context, context.newPromise());
    }

    public static boolean isInEventLoop(Object channelHandlerContext) {
        return ((ChannelHandlerContext)channelHandlerContext).executor().inEventLoop();
    }

    /*
     * 获取对端地址
     * @return
     */
    public static InetSocketAddress remoteAddress(Object channelHandlerContext) {
        return (InetSocketAddress) ((ChannelHandlerContext)channelHandlerContext).channel().remoteAddress();
    }

    /*
     * 获取本端地址
     * @return
     */
    public static InetSocketAddress localAddress(Object channelHandlerContext) {
        return (InetSocketAddress) ((ChannelHandlerContext)channelHandlerContext).channel().localAddress();
    }

    public static void exceptionCaught(Object channelHandlerContext, Throwable cause) {
        ((ChannelHandlerContext)channelHandlerContext).fireExceptionCaught(cause);
    }
}
