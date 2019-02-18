package zg.dove.filter;

import zg.dove.net.NetChannel;

public interface IFilter<I, O> {
    /**
     * 输入过滤, 传递过滤后的数据
     * @param ch
     * @param msg
     * @return
     */
    I onFilterIn(NetChannel ch, I msg);

    /**
     * 输出过滤, 传递过滤后的数据
     * @param ch
     * @param msg
     * @return
     */
    O onFilterOut(NetChannel ch, O msg);

    /**
     * 异常过滤, 处理异常链
     * @param ch
     * @param e
     */
    Throwable onFilterException(NetChannel ch, Throwable t);
}
