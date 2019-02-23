package zg.dove.http.filter;

import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import zg.dove.filter.IFilter;
import zg.dove.http.HttpRequest;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentFilter implements IFilter {
    private static final String DIR = System.getProperty("user.dir");

    @Override
    public Object onFilterIn(Object context, Object msg) throws Exception {
        HttpRequest request = (HttpRequest) msg;

        Map content = null;
        CharSequence type = request.getHeader("content-type");
        if (type != null) {
            if (type.equals("application/x-www-form-urlencoded")) {
                QueryStringDecoder decoder = new QueryStringDecoder((String) request.content(), false);
                content = decoder.parameters();
            }
            else if (type.toString().startsWith("multipart/form-data")) {
                content = new HashMap<String, Object>();
                List<InterfaceHttpData> datas = request.from();
                for (InterfaceHttpData data : datas) {
                    if(data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                        FileUpload fileUpload = (FileUpload) data;
                        if(fileUpload.isCompleted()) {
                            fileUpload.renameTo(new File(DIR + fileUpload.getFilename()));
                        }
                    }
                    else {
                        Attribute attribute = (Attribute) data;
                        content.put(attribute.getName(), attribute.getValue());
                    }
                }
            }
            request.build(content);
        }

        return request;
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
