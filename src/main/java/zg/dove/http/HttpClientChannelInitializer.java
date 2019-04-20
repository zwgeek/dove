package zg.dove.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.IdleStateHandler;
import zg.dove.filter.IFilter;
import zg.dove.route.IRoute;

/**
 * http1.1 初始化
 * @author PaPa
 * @create 2018-11-28
 */
public class HttpClientChannelInitializer extends ChannelInitializer {

    private final HttpChannelHandler httpChannelHandler;

    public HttpClientChannelInitializer(IFilter filter, IRoute route) {
        this.httpChannelHandler = new HttpChannelHandler(filter, route);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("idle", new IdleStateHandler(HttpChannelConfig.READDER_IDLE_TIME, HttpChannelConfig.WRITER_IDLE_TIME,
                        HttpChannelConfig.ALL_IDLE_TIME))
                .addLast("codec", new HttpClientCodec())
                .addLast("aggregator", new HttpObjectAggregator(HttpChannelConfig.MAX_CONTENT_LENGTH))
                .addLast("channel", this.httpChannelHandler);
    }
}