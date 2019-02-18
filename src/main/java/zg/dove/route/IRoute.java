package zg.dove.route;

/*
 * 消息路由
 */
public interface IRoute<FROM, KEY, DATA, RV> {
    /**
     * 事件触发
     *
     * @param from 事件源
     * @param key  事件id，之所以有传id出去，是考虑一个函数可以处理多条消息情况
     * @param data 事件数据
     */
    RV trigger(FROM from, KEY key, DATA data) throws Exception;

    /**
     * 事件回调函数注册
     *
     * @param key key==null表示默认处理函数，当找不到处理函数时就调用默认处理函数
     * @param cb  回调函数
     */
    void register(KEY key, Callback<FROM, KEY, DATA, RV> cb);

    /**
     * 事件回调函数取消
     *
     * @param key
     */
    void cancel(KEY key);

    /**
     * 事件回调函数搜索
     *
     * @param key
     * @return
     */
    boolean search(KEY key);

    /*
     * 事件回调函数
     */
    @FunctionalInterface
    interface Callback<FROM, KEY, DATA, RV> {
        RV call(FROM from, KEY key, DATA data) throws Exception;
    }

    public class NoRouteToLogicException extends Exception{}

}
