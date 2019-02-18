package zg.dove.http.filter;

import zg.dove.filter.IFilter;
import zg.dove.http.HttpRequest;
import zg.dove.net.NetChannel;

public class BrowserFilter implements IFilter {

    @Override
    public Object onFilterIn(NetChannel ch, Object msg) {
        HttpRequest request = (HttpRequest) msg;
        //去除浏览器"/favicon.ico"的干扰
        if(request.uri().equals("/favicon.ico")){
            return null;
        }
        return msg;
    }

    @Override
    public Object onFilterOut(NetChannel ch, Object msg) {
        return msg;
    }

    @Override
    public Throwable onFilterException(NetChannel ch, Throwable t) {
        return null;
    }
}
