package zg.dove.launcher;

import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.codec.ICodec;
import zg.dove.filter.IFilter;
import zg.dove.net.*;
import zg.dove.route.IRoute;
import zg.dove.net.NetStream;
import zg.dove.utils.Assert;
import zg.dove.utils.ClzParse;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;

public class NetStarter extends Starter {
    public static final Logger logger = LogManager.getLogger(NetStarter.class);

    public static NetChannelAction initAction(IFilter filter, IRoute route) {
        NetChannelAction action = new NetChannelAction();
        action.initFilter(filter);
        action.initRoute(route, false, false, null);

        NetClient.setAction(action);
        return action;
    }

    public static NetStream initStream(IFilter filter, IRoute route) {
        NetChannelInitializer netChannelInitializer = new NetChannelInitializer(filter, route);
        NetStream stream = new NetStream(netChannelInitializer, (NioEventLoopGroup)Starter.eventLoopGroup);

        NetClient.setStream(stream);
        return stream;
    }

    @Override
    protected void start(Properties properties) throws Exception {
        Assert.verify(properties.getProperty("net.addr.ip") != null);
        Assert.verify(properties.getProperty("net.addr.port") != null);
        Assert.verify(properties.getProperty("net.work.thread") != null);
        Assert.verify(properties.getProperty("net.work.codec") != null);
        Assert.verify(properties.getProperty("net.work.filter") != null);
        Assert.verify(properties.getProperty("net.work.route") != null);

        Class<?> clz = Class.forName(properties.getProperty("net.work.codec"));
        ICodec codec = (ICodec)clz.getDeclaredConstructor().newInstance();
        NetChannelCodec.setCodec(codec);

        clz = Class.forName(properties.getProperty("net.work.filter"));
        IFilter filter = (IFilter)clz.getDeclaredConstructor().newInstance();

        clz = Class.forName(properties.getProperty("net.work.route"));
        IRoute route = (IRoute)clz.getDeclaredConstructor().newInstance();

        NetChannelAction action = NetStarter.initAction(filter, route);

        String[] clznames = ClzParse.findClassesByPackage(properties.getProperty("common.package"),
                ClzParse.EMPTY_LIST, ClzParse.EMPTY_LIST);
        for (String clzname: clznames) {
            clz = Class.forName(clzname);
            for (Method method : clz.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers())) {
                    BeanNet beanNet = method.getAnnotation(BeanNet.class);
                    if (beanNet == null) continue;
                    action.register(beanNet.key(),
                        (context, msgClz, msg) -> method.invoke(null, context, msgClz, msg));
                }
            }
        }

        Starter.eventLoopGroup = new NioEventLoopGroup(Integer.valueOf(properties.getProperty("net.work.thread")));
        NetStarter.initStream(filter, route).listen(properties.getProperty("net.addr.ip"), Integer.valueOf(properties.getProperty("net.addr.port")));
    }

    @Override
    protected void finish() throws Exception {

    }
}
