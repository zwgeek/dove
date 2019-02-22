package zg.dove.http;

import io.netty.handler.codec.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanHttp {
    /*
     * 路由url
     * @return
     */
    String path();

    /*
     * 路由方法, 这边默认写死了, 注意一下
     * @return
     */
    String method() default "GET";

    class Method {
        public static final String GET = HttpMethod.GET.toString();
        public static final String POST = HttpMethod.POST.toString();
        public static final String OPTIONS = HttpMethod.OPTIONS.toString();
        public static final String DELETE = HttpMethod.DELETE.toString();
        public static final String PUT = HttpMethod.PUT.toString();
        public static final String HEAD = HttpMethod.HEAD.toString();
        public static final String TRACE = HttpMethod.TRACE.toString();
        public static final String CONNECT = HttpMethod.CONNECT.toString();
    }
}
