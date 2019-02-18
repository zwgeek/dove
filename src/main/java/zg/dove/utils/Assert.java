package zg.dove.utils;

/**
 * 断言
 */
public class Assert {
    public static void verify(boolean val) {
        verify(val, "assert verify failed.");
    }

    /*
     * 系统内错误检查，这一般是一个系统bug造成的
     * @param val
     */
    public static void verify(boolean val, String msg) {
        if (!val) {
            throw new AssertException(AssertException.T_VERIFY, msg);
        }
    }

    public static void check(boolean val) {
        check(val, "assert check failed.");
    }

    /*
     * 系统外错误检查，入参错误，这一般是调用者的错误操作造成的，也就是入参错误
     * @param val
     */
    public static void check(boolean val, String msg) {
        if (!val) {
            throw new AssertException(AssertException.T_CHECK, msg);
        }
    }

    /**
     * 断言错误异常
     */
    public final static class AssertException extends RuntimeException {
        public static final int T_VERIFY = 1;
        public static final int T_CHECK = 2;

        private final int type;

        public AssertException(int type, String msg) {
            super(msg);
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
