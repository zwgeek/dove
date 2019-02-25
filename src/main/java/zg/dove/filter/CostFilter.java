package zg.dove.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.net.NetSessionContext;

public class CostFilter implements IFilter {
    public static final Logger logger = LogManager.getLogger(CostFilter.class);

    @Override
    public Object onFilterIn(Object context, Object msg) throws Exception {
        NetSessionContext.putAttribute(context, NetSessionContext.SCOPE_REQUEST, "CostTime", System.currentTimeMillis());
        return msg;
    }

    @Override
    public Object onFilterOut(Object context, Object msg) throws Exception {
        long time = (long)NetSessionContext.getAttribute(context, NetSessionContext.SCOPE_REQUEST, "CostTime");
        logger.debug("[msg cost time] => {} : {} ms", msg, System.currentTimeMillis() - time);
        return msg;
    }

    @Override
    public Throwable onFilterException(Object context, Throwable t) {
        return t;
    }
}
