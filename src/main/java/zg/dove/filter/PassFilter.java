package zg.dove.filter;

import zg.dove.net.NetChannel;

public class PassFilter implements IFilter<Object, Object> {

    @Override
    public Object onFilterIn(NetChannel ch, Object msg) {
        return msg;
    }

    @Override
    public Object onFilterOut(NetChannel ch, Object msg) {
        return msg;
    }

    @Override
    public Throwable onFilterException(NetChannel ch, Throwable t) {
        return t;
    }
}
