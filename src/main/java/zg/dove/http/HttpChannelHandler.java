package zg.dove.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import zg.dove.net.NetChannelHandler;
import zg.dove.net.NetSessionContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpChannelHandler extends NetChannelHandler {
    private static final String PATH = "html";
    private static Map<String, String> CONTENT_TYPES = new HashMap<>();
    static {
        CONTENT_TYPES.put("awf", "application/vnd.adobe.workflow");
        CONTENT_TYPES.put("bmp", "application/x-bmp");
        CONTENT_TYPES.put("gif", "image/gif");
        CONTENT_TYPES.put("ico", "image/x-icon");
        CONTENT_TYPES.put("jpe", "image/jpeg");
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("net", "image/pnetvue");
        CONTENT_TYPES.put("rp", "image/vnd.rn-realpix");
        CONTENT_TYPES.put("tif", "image/tiff");
        CONTENT_TYPES.put("tiff", "image/tiff");
        CONTENT_TYPES.put("wbmp", "image/vnd.wap.wbmp");
        CONTENT_TYPES.put("png", "image/png");
        CONTENT_TYPES.put("jpg", "image/jpeg");
        CONTENT_TYPES.put("jfif", "image/jpeg");
        CONTENT_TYPES.put("tif", "image/tiff");

        CONTENT_TYPES.put("aifc", "audio/aiff");
        CONTENT_TYPES.put("la1", "audio/x-liquid-file");
        CONTENT_TYPES.put("m3u", "audio/mpegurl");
        CONTENT_TYPES.put("midi", "audio/mid");
        CONTENT_TYPES.put("mns", "audio/x-musicnet-stream");
        CONTENT_TYPES.put("mp2", "audio/mp2");
        CONTENT_TYPES.put("mp3", "audio/mp3");
        CONTENT_TYPES.put("pls", "audio/scpls");
        CONTENT_TYPES.put("ram", "audio/x-pn-realaudio");
        CONTENT_TYPES.put("rpm", "audio/x-pn-realaudio-plugin");
        CONTENT_TYPES.put("wax", "audio/x-ms-wax");
        CONTENT_TYPES.put("xpl", "audio/scpls");
        CONTENT_TYPES.put("wma", "audio/x-ms-wma");
        CONTENT_TYPES.put("wav", "audio/wav");
        CONTENT_TYPES.put("snd", "audio/basic");
        CONTENT_TYPES.put("rmm", "audio/x-pn-realaudio");
        CONTENT_TYPES.put("rmi", "audio/mid");
        CONTENT_TYPES.put("mpga", "audio/rn-mpeg");
        CONTENT_TYPES.put("mp1", "audio/mp1");
        CONTENT_TYPES.put("mnd", "audio/x-musicnet-download");
        CONTENT_TYPES.put("mid", "audio/mid");
        CONTENT_TYPES.put("lmsff", "audio/x-la-lms");
        CONTENT_TYPES.put("lavs", "audio/x-liquid-secure");

        CONTENT_TYPES.put("m2v", "video/x-mpeg");
        CONTENT_TYPES.put("m4e", "video/mpeg4");
        CONTENT_TYPES.put("mp2v", "video/mpeg");
        CONTENT_TYPES.put("mp4", "video/mpeg4");
        CONTENT_TYPES.put("mpeg", "video/mpg");
        CONTENT_TYPES.put("mps", "video/x-mpeg");
        CONTENT_TYPES.put("mpv", "video/mpg");
        CONTENT_TYPES.put("rv", "video/vnd.rn-realvideo");
        CONTENT_TYPES.put("wmv", "video/x-ms-wmv");
        CONTENT_TYPES.put("wvx", "video/x-ms-wvx");
        CONTENT_TYPES.put("wmx", "video/x-ms-wmx");
        CONTENT_TYPES.put("wm", "video/x-ms-wm");
        CONTENT_TYPES.put("mpv2", "video/mpeg");
        CONTENT_TYPES.put("mpg", "video/mpg");
        CONTENT_TYPES.put("mpe", "video/x-mpeg");
        CONTENT_TYPES.put("mpa", "video/x-mpg");
        CONTENT_TYPES.put("movie", "video/x-sgi-movie");
        CONTENT_TYPES.put("m1v", "video/x-mpeg");
        CONTENT_TYPES.put("IVF", "video/x-ivf");
        CONTENT_TYPES.put("avi", "video/avi");
        CONTENT_TYPES.put("asx", "video/x-ms-asf");
        CONTENT_TYPES.put("asf", "video/x-ms-asf");


        CONTENT_TYPES.put("jpg", "application/x-jpg");
        CONTENT_TYPES.put("img", "application/x-img");

        CONTENT_TYPES.put("html", "text/html");
        CONTENT_TYPES.put(".htx", "text/html");

        CONTENT_TYPES.put("js", "application/x-javascript");

    }

    public HttpChannelHandler() {
        super();
    }

    private HttpResponse _createResponse() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "X-Requested-With");
        response.setHeader(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET");
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
        return response;
    }

    @Override
    protected Object _channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            // NOTICE:为了实现上简单，我们现在只考虑整包的处理
            return this._channelRead0(msg);
        }

        HttpResponse response = this._createResponse();
        netChannel.getNetSessionContext().removeAttribute(NetSessionContext.SCOPE_REQUEST);
        netChannel.getNetSessionContext().putAttribute(NetSessionContext.SCOPE_REQUEST, HttpResponse.class, response);

        HttpRequest request = new HttpRequest(((FullHttpRequest)msg));
        if (!request.method().equals(HttpMethod.GET)) {
            return request;
        }

        String[] units = request.uri().split(".");
        if (units.length <= 1) {
            return request;
        }

        String contentType = CONTENT_TYPES.get(units[units.length]);
        if (contentType == null) {
            return request;
        }

        File file = new File(PATH);
        if (!file.exists()) {
            return request;
        }

        byte[] bytes = new byte[HttpChannelConfig.MAX_FILE_LENGTH];
        new FileInputStream(file).read(bytes, 0, HttpChannelConfig.MAX_FILE_LENGTH);
        response.write(bytes);
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, contentType);
        ctx.write(response);
        return null;
    }

    private Object _channelRead0(Object msg) throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            throw new RuntimeException("not support " + msg.getClass().getName());
        }

        HttpResponse response = new HttpResponse((FullHttpResponse)msg);
        netChannel.getNetSessionContext().putAttribute(NetSessionContext.SCOPE_REQUEST, HttpResponse.class, response);

        HttpRequest request = (HttpRequest)netChannel.getNetSessionContext().getAttribute(HttpRequest.class);
        return request;
    }

    @Override
    protected Object _write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof HttpResponse)) {
            return this._write0(msg);
        }

        ((HttpResponse)msg).setHeader(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(((HttpResponse)msg).size()));
        return ((HttpResponse)msg).getResponse();
    }

    private Object _write0(Object msg) throws Exception {
        if (!(msg instanceof HttpRequest)) {
            throw new RuntimeException("not support " + msg.getClass().getName());
        }

        netChannel.getNetSessionContext().putAttribute(NetSessionContext.SCOPE_REQUEST, HttpRequest.class, msg);
        return ((HttpRequest)msg).getRequest();
    }
}
