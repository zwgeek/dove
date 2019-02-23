package zg.dove.http;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import zg.dove.filter.IFilter;
import zg.dove.route.IRoute;

import java.io.File;

/**
 * http1.1 ssl初始化
 * @author PaPa
 * @create 2018-11-28
 */
public class HttpsChannelInitializer extends HttpChannelInitializer {

    private final SslContext sslContext;

    public HttpsChannelInitializer(File cert, File key, String password, IFilter filter, IRoute route) throws Exception {
        super(filter, route);
        this.sslContext = SslContext.newServerContext(cert, key, password);
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast("ssl", this.sslContext.newHandler(channel.alloc()));

        super.initChannel(channel);
    }
}
