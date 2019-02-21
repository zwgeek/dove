package zg.dove.http.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zg.dove.filter.IFilter;
import zg.dove.http.*;
import zg.dove.net.NetChannel;
import zg.dove.net.NetSessionContext;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileFilter implements IFilter {
    public static final Logger logger = LogManager.getLogger(FileFilter.class);
    private static final String PATH = System.getProperty("user.dir");
    private static Map<String, String> CONTENT_TYPES = new HashMap<>();
    static {
        CONTENT_TYPES.put("awf", "application/vnd.adobe.workflow");
        CONTENT_TYPES.put("bmp", "application/x-bmp");
        CONTENT_TYPES.put("img", "application/x-img");
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

        CONTENT_TYPES.put("html", "text/html");
        CONTENT_TYPES.put("htx", "text/html");

        CONTENT_TYPES.put("js", "application/x-javascript");

    }

    @Override
    public Object onFilterIn(Object context, Object msg) throws Exception {
        HttpRequest request = (HttpRequest) msg;

        if (!request.method().equals(BeanHttp.Method.GET)) {
            return request;
        }

        String[] units = request.uri().split("\\.");
        if (units.length <= 1) {
            return request;
        }

        String contentType = CONTENT_TYPES.get(units[units.length - 1]);
        if (contentType == null) {
            return request;
        }

        ByteBuffer buffer = ByteBuffer.allocate(HttpChannelConfig.MAX_FILE_LENGTH);
        AsynchronousFileChannel.open(Paths.get(PATH + request.uri())).read(buffer, 0, "async file",
            new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer readCount, Object attachment) {
                    HttpResponse response = (HttpResponse) NetSessionContext.getAttribute(context, NetSessionContext.SCOPE_REQUEST, HttpResponse.class);
                    response.write(buffer.array(), 0, readCount);
                    response.setHeader("content-type", contentType);
                    try {
                        NetChannel.writeAndFlushAndClose(context, response);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    NetChannel.exceptionCaught(context, exc);
                }
            });

        return null;
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
