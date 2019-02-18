package zg.dove.net;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import zg.dove.codec.ICodec;
import zg.dove.codec.StringCodec;

public class NetChannelCodec extends ChannelHandlerAdapter {
    private static ICodec codec = new StringCodec();

    public NetChannelCodec() {
    }

    public static void setCodec(ICodec codec) {
        NetChannelCodec.codec = codec;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ReferenceCountUtil.release(msg);
        msg = codec.decode(msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        msg = codec.encode(msg);
        super.write(ctx, msg, promise);
    }
}
