package zg.dove.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;


public class NetChannelInitializer extends ChannelInitializer {

    public NetChannelInitializer() {
    }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                .addLast("idle", new IdleStateHandler(NetChannelConfig.READDER_IDLE_TIME, NetChannelConfig.WRITER_IDLE_TIME,
                        NetChannelConfig.ALL_IDLE_TIME))
                .addLast("codec", new NetChannelCodec())
                .addLast("channel", new NetChannelHandler());
    }
}
