package zg.dove.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.http.HttpRequest;
import zg.dove.net.NetChannel;

public class CostFilter implements IFilter {
    public static final Logger logger = LogManager.getLogger(CostFilter.class);
    private String name;
    private long basetime;

    @Override
    public Object onFilterIn(NetChannel ch, Object msg) {
        if (msg instanceof HttpRequest) {
            name = ((HttpRequest) msg).method() + ' ' + ((HttpRequest) msg).uri();
        } else {
            name = msg.getClass().getName();
        }
        basetime = System.currentTimeMillis();
        return msg;
    }

    @Override
    public Object onFilterOut(NetChannel ch, Object msg) {
        logger.debug("[msg cost time] => {} : {} ms", name, System.currentTimeMillis() - basetime);
        return msg;
    }

    @Override
    public Throwable onFilterException(NetChannel ch, Throwable t) {
        return t;
    }
}
