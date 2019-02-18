package zg.dove.http;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * http1.1 ssl初始化
 * @author PaPa
 * @create 2018-11-28
 */
public class HttpsChannelInitializer extends HttpChannelInitializer {

    public HttpsChannelInitializer() {
        super();
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        channel.pipeline().addLast(sslCtx.newHandler(channel.alloc()));

        super.initChannel(channel);
    }
}
