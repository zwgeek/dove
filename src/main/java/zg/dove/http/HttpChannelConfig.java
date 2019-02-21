package zg.dove.http;

/**
 * http1.1 配置
 * @author PaPa
 * @create 2018-11-28
 */
public class HttpChannelConfig {
    public static final int READDER_IDLE_TIME = 10;
    public static final int WRITER_IDLE_TIME = 10;
    public static final int ALL_IDLE_TIME = 10;
    public static final int MAX_CONTENT_LENGTH = 1024 * 1024;
    public static final int MAX_FILE_LENGTH = 5 * 1024 * 1024;
}
