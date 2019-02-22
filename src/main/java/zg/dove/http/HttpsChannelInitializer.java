package zg.dove.http;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import zg.dove.filter.IFilter;
import zg.dove.route.IRoute;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.KeyStore;

/**
 * http1.1 ssl初始化
 * @author PaPa
 * @create 2018-11-28
 */
public class HttpsChannelInitializer extends HttpChannelInitializer {

    private final SSLEngine sslEngine;

    public HttpsChannelInitializer(SSLContext sslContext, IFilter filter, IRoute route) {
        super(filter, route);
        this.sslEngine = sslContext.createSSLEngine();
        this.sslEngine.setUseClientMode(false);
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new SslHandler(this.sslEngine));

        super.initChannel(channel);
    }
}
