package zg.dove.http;

import io.netty.channel.*;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * http1.1 初始化
 * @author PaPa
 * @create 2018-11-28
 */
public class HttpChannelInitializer extends ChannelInitializer {

    public HttpChannelInitializer() {
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("idle", new IdleStateHandler(HttpChannelConfig.READDER_IDLE_TIME, HttpChannelConfig.WRITER_IDLE_TIME,
                        HttpChannelConfig.ALL_IDLE_TIME))
                .addLast("codec", new HttpServerCodec())
                .addLast("aggregator", new HttpObjectAggregator(HttpChannelConfig.MAX_CONTENT_LENGTH))
                .addLast("channel", new HttpChannelHandler());
    }
}
