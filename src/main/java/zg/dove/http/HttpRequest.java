package zg.dove.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

public class HttpRequest {
    private FullHttpRequest request;

    private String path;
    private Map<String, List<String>> params;
    private Object content;

    protected HttpRequest(FullHttpRequest request) {
        this.request = request;

        QueryStringDecoder queryDecoder = new QueryStringDecoder(this.request.uri());
        this.path = queryDecoder.path();
        this.params = queryDecoder.parameters();

        this.content = this.request.content().toString(CharsetUtil.UTF_8);
    }

    protected HttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        this.request = new DefaultFullHttpRequest(httpVersion, method, uri);
    }

    protected HttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
        this.request = new DefaultFullHttpRequest(httpVersion, method, uri, content);
    }

    protected FullHttpRequest getRequest() {
        return request;
    }

    public CharSequence getHeader(String key) {
        return this.request.headers().get(new AsciiString(key));
    }

    public CharSequence getHeader(AsciiString key) {
        return this.request.headers().get(key);
    }

    public void setHeader(String key, CharSequence value) {
        this.request.headers().set(new AsciiString(key), value);
    }

    public void setHeader(AsciiString key, CharSequence value) {
        this.request.headers().set(key, value);
    }

    public String method() {
        return this.request.method().toString();
    }

    public String path() {
        return this.path;
    }

    public Map<String, List<String>> params() {
        return this.params;
    }

    public Object content() {
        return this.content;
    }

    public void build(Object content) {
        this.content = content;
    }

    public List<InterfaceHttpData> from() {
        return new HttpPostRequestDecoder(this.request).getBodyHttpDatas();
    }
}
