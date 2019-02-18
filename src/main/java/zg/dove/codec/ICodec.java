package zg.dove.codec;

/**
 * 编码器（解码器)，将一个对象编码（解码）成另一个对象
 * @param <T> 被编码对象类型
 * @param <R> 编码后的对象类型
 */
public interface ICodec<T, R> {
    /**
     * 编码函数
     *
     * @param obj 需要编码的对象
     * @return 编码后的对象
     */
    R encode(T obj);

    /**
     * 解码函数
     *
     * @param obj 需要解码的对象
     * @return 解码后的对象
     */
    T decode(R obj);
}
