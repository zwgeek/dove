package zg.dove.filter;

import zg.dove.net.NetChannel;

import java.util.ArrayList;
import java.util.List;

public class DupFilter implements IFilter<Object, Object> {
    private List<IFilter<Object, Object>> filters = new ArrayList();

    public boolean add(IFilter filter) {
        return filters.add(filter);
    }

    public boolean remove(IFilter filter) {
        return filters.remove(filter);
    }

    @Override
    public Object onFilterIn(NetChannel ch, Object msg) {
        for (IFilter<Object, Object> filter : filters) {
            msg = filter.onFilterIn(ch, msg);
            if (msg == null) {
                break;
            }
        }
        return msg;
    }

    @Override
    public Object onFilterOut(NetChannel ch, Object msg) {
        for (int i = filters.size() - 1; i >= 0; i--) {
            IFilter<Object, Object> filter = filters.get(i);
            msg = filter.onFilterOut(ch, msg);
            if (msg == null) {
                break;
            }
        }
        return msg;
    }

    @Override
    public Throwable onFilterException(NetChannel ch, Throwable t) {
        for (int i = filters.size() - 1; i >= 0; i--) {
            IFilter<Object, Object> filter = filters.get(i);
            t = filter.onFilterException(ch, t);
            if (t == null) {
                break;
            }
        }
        return t;
    }
}
