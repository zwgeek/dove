package zg.dove.net;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EventListener;


/**
 * 基于netty的 net/http1.1 网络流
 * @author PaPa
 * @create 2018-11-28
 */
public class NetStream implements INetStream {
    public static final Logger logger = LogManager.getLogger(NetStream.class);

    private final ChannelInitializer serverChannelInitializer;
    private final ChannelInitializer clientChannelInitializer;
    private final EventLoopGroup eventLoopGroup;

    public NetStream(ChannelInitializer serverChannelInitializer,
                     ChannelInitializer clientChannelInitializer,
                     EventLoopGroup eventLoopGroup) {
        this.serverChannelInitializer = serverChannelInitializer;
        this.clientChannelInitializer = clientChannelInitializer;
        this.eventLoopGroup = eventLoopGroup;
    }

    public boolean listen(String ip, int port) throws Exception {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap(); //引导辅助程序
            serverBootstrap.group(eventLoopGroup);                   //线程池
            serverBootstrap.channel(NioServerSocketChannel.class);   //设置nio类型的channel

            serverBootstrap.childHandler(serverChannelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //设置监听端口
            ChannelFuture future = serverBootstrap.bind(ip, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully().sync();
        }
        return true;
    }

    public boolean connect(String ip, int port, EventListener listener) throws Exception {
        try {
            //配置客户端NIO线程组
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(clientChannelInitializer);
            //发起异步连接操作
            ChannelFuture future = bootstrap.connect(ip, port);
            future.addListener((ChannelFutureListener) listener);
//            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            eventLoopGroup.shutdownGracefully().sync();
//        }
        return true;
    }

    public boolean close() throws Exception {
        this.eventLoopGroup.shutdownGracefully().sync();
        return true;
    }
}
