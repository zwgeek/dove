package zg.dove.filter;

public class PassFilter implements IFilter<Object, Object> {

    @Override
    public Object onFilterIn(Object context, Object msg) throws Exception {
        return msg;
    }

    @Override
    public Object onFilterOut(Object context, Object msg) throws Exception {
        return msg;
    }

    @Override
    public Throwable onFilterException(Object context, Throwable t) {
        return t;
    }
}
