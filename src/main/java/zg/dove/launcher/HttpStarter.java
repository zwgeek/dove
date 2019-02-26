package zg.dove.launcher;

import io.netty.channel.nio.NioEventLoopGroup;
import zg.dove.filter.DupFilter;
import zg.dove.filter.IFilter;
import zg.dove.http.*;
import zg.dove.net.NetStream;
import zg.dove.route.DefaultRoute;
import zg.dove.route.IRoute;
import zg.dove.utils.Assert;
import zg.dove.utils.ClzParse;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

public class HttpStarter extends Starter {
    public static HttpChannelAction initAction(IFilter filter, IRoute route) {
        HttpChannelAction action = new HttpChannelAction();
        action.initFilter(filter);
        action.initRoute(route, true, true, null);

        HttpClient.setAction(action);
        return action;
    }

    public static NetStream initStream(String enable, String cert, String key, String password, IFilter filter, IRoute route) throws Exception {
        HttpChannelInitializer httpChannelInitializer;
        if (enable == null || enable.equals("0")) {
            httpChannelInitializer = new HttpChannelInitializer(filter, route);
        } else {
            Assert.verify(cert != null);
            Assert.verify(key != null);

            httpChannelInitializer = new HttpsChannelInitializer(
                    new File(HttpStarter.class.getClassLoader().getResource(cert).getPath()), new File(HttpStarter.class.getClassLoader().getResource(key).getPath()),
                    password, filter, route);
        }
        NetStream stream = new NetStream(httpChannelInitializer, (NioEventLoopGroup)Starter.eventLoopGroup);

        HttpClient.setStream(stream);
        return stream;
    }

    @Override
    public void start(Properties properties) throws Exception {
        Assert.verify(properties.getProperty("http.addr.ip") != null);
        Assert.verify(properties.getProperty("http.addr.port") != null);
        Assert.verify(properties.getProperty("http.work.thread") != null);

        IFilter filter = new DupFilter();
        IRoute route = new DefaultRoute();

        HttpChannelAction action = HttpStarter.initAction(filter, route);

        if (properties.getProperty("http.filter.list") != null) {
            String[] filternames = properties.getProperty("http.filter.list").split("\\|");
            for (String filtername : filternames) {
                action.addFilter((IFilter) Class.forName(filtername).newInstance());
            }
        }

        Class clz;
        String[] clznames = ClzParse.findClassesByPackage(properties.getProperty("common.package"),
                ClzParse.EMPTY_LIST, ClzParse.EMPTY_LIST);
        for (String clzname : clznames) {
            clz = Class.forName(clzname);
            for (Method method : clz.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers())) {
                    BeanHttp beanHttp = method.getAnnotation(BeanHttp.class);
                    if (beanHttp == null) continue;
                    action.register(beanHttp.method(), beanHttp.path(),
                        (context, request, response) -> method.invoke(null, context, request, response));
                }
            }
        }
        Starter.eventLoopGroup = new NioEventLoopGroup(Integer.valueOf(properties.getProperty("http.work.thread")));
        HttpStarter.initStream(properties.getProperty("http.ssl.enable"), properties.getProperty("http.ssl.cert"),
                properties.getProperty("http.ssl.key"), properties.getProperty("http.ssl.password"), filter, route)
                .listen(properties.getProperty("http.addr.ip"), Integer.valueOf(properties.getProperty("http.addr.port")));
    }

    @Override
    public void finish() throws Exception {

    }
}
