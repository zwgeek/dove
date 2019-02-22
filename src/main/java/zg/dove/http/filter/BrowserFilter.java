package zg.dove.http.filter;

import zg.dove.filter.IFilter;
import zg.dove.http.HttpRequest;

public class BrowserFilter implements IFilter {

    @Override
    public Object onFilterIn(Object context, Object msg) throws Exception {
        HttpRequest request = (HttpRequest) msg;
        //去除浏览器"/favicon.ico"的干扰
        if(request.path().equals("/favicon.ico")){
            return null;
        }
        return msg;
    }

    @Override
    public Object onFilterOut(Object context, Object msg) throws Exception {
        return msg;
    }

    @Override
    public Throwable onFilterException(Object context, Throwable t) {
        return null;
    }
}
