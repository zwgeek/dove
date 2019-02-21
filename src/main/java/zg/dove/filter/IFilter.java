package zg.dove.filter;


public interface IFilter<I, O> {
    /**
     * 输入过滤, 传递过滤后的数据
     * @param context, 跟NetChannel配合使用
     * @param msg
     * @return
     */
    I onFilterIn(Object context, I msg);

    /**
     * 输出过滤, 传递过滤后的数据
     * @param context, 跟NetChannel配合使用
     * @param msg
     * @return
     */
    O onFilterOut(Object context, O msg);

    /**
     * 异常过滤, 处理异常链
     * @param context, 跟NetChannel配合使用
     * @param t
     */
    Throwable onFilterException(Object context, Throwable t);
}
