package zg.dove.net;

public final class NetEvent {
    public static final Integer DATA = Integer.valueOf(-8); // 网络数据
    public static final Integer DISCONNECTED = Integer.valueOf(-1);   //断开连接
    public static final Integer CONNECTED = Integer.valueOf(-2);  //建立连接

    public static final Integer READ_EXCEPTION = Integer.valueOf(-3); // 读取异常，输入异常
    public static final Integer WRITE_EXCEPTION = Integer.valueOf(-4); // 写入异常，输出异常
    public static final Integer READ_IDLE_TIMEOUT = Integer.valueOf(-5); // 读取心跳超时
    public static final Integer WRITE_IDLE_TIMEOUT = Integer.valueOf(-6);    // 写入心跳超时
    public static final Integer ALL_IDLE_TIMEOUT = Integer.valueOf(-7); // 读写都空闲

    public static final Integer HIGH_WATER_UNWRITEABLE = Integer.valueOf(-9); // 达到高水位线
    public static final Integer HIGH_WATER_WRITEABLE = Integer.valueOf(-10); // 降低到高水位线以下

    public static final Integer HEART_TICK = Integer.valueOf(-11);      // 心跳

    public static final Integer DATA_PROCESS_BEFORE = Integer.valueOf(-12);
    public static final Integer DATA_PROCESS_AFTER = Integer.valueOf(-13);
}
