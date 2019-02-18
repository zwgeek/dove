package zg.dove.net;

import java.util.EventListener;

/**
 * http1.1 网络流
 * @author PaPa
 * @create 2018-11-28
 */
public interface INetStream {
    /** 监听 **/
    boolean listen(String ip, int port) throws Exception;
    /** 连接 **/
    boolean connect(String ip, int port, EventListener listener) throws Exception;
    /** 关闭 **/
    boolean close() throws Exception;
}
