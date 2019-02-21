package zg.dove.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

public class HttpRequest {
    private FullHttpRequest request;

    protected HttpRequest(FullHttpRequest request) {
        this.request = request;
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

    public void setHeader(String key, CharSequence value) {
        this.request.headers().set(new AsciiString(key), value);
    }

    public void setHeader(AsciiString key, CharSequence value) {
        this.request.headers().set(key, value);
    }

    public String uri() {
        return this.request.uri();
    }

    public String method() {
        return this.request.method().toString();
    }
}
