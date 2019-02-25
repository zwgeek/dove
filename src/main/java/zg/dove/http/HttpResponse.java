package zg.dove.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.AsciiString;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

public class HttpResponse  {
    private final FullHttpResponse response;

    private Object content;

    protected HttpResponse(FullHttpResponse response) {
        this.response = response;
        this.content = response.content();
    }

    protected HttpResponse(HttpVersion version, HttpResponseStatus status) {
        this(new DefaultFullHttpResponse(version, status));
    }

    protected HttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
        this(new DefaultFullHttpResponse(version, status, content));
    }

    protected FullHttpResponse getResponse() {
        return response;
    }

    public void setStatus(int statusCode) {
        this.response.setStatus(HttpResponseStatus.valueOf(statusCode));
    }

    public CharSequence getHeader(String key) {
        return this.response.headers().get(new AsciiString(key));
    }

    public CharSequence getHeader(AsciiString key) {
        return this.response.headers().get(key);
    }

    public void setHeader(String key, CharSequence value) {
        this.response.headers().set(new AsciiString(key), value);
    }

    public void setHeader(AsciiString key, CharSequence value) {
        this.response.headers().set(key, value);
    }

    public int size() {
        return this.response.content().readableBytes();
    }

    public Object content() {
        return this.content;
    }

    public void write(byte[] bytes) {
        this.response.content().writeBytes(bytes);
    }

    public void write(byte[] bytes, int srcIndex, int length) {
        this.response.content().writeBytes(bytes, srcIndex, length);
    }

    public void write(Object msg) {
        this.content = msg;
    }
}
