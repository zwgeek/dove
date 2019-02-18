package zg.dove.route;

import java.util.HashMap;
import java.util.Map;

/**
 * 限流路由器
 */
public class RateLimitRoute<FROM, KEY, DATA, RV> implements IRoute<FROM, KEY, DATA, RV> {
    private IRoute<FROM, KEY, DATA, RV> realMsgRoute = null;
    // 整体限制
    private Limit limitGlobal;
    // 统一限制
    private Limit limitCommon;
    // 特别限制
    private Map<KEY, Limit> keyLimits = new HashMap<>();

    public void setLimitGlobal(long gapMs, int num, Callback<FROM, KEY, DATA, RV> cbFail) {
        limitGlobal = new Limit(num, gapMs, cbFail);
    }

    public void setLimitCommon(long gapMs, int num, Callback<FROM, KEY, DATA, RV> cbFail) {
        limitCommon = new Limit(num, gapMs, cbFail);
    }

    public void setLimitKey(KEY key, long gapMs, int num, Callback<FROM, KEY, DATA, RV> cbFail) {
        Limit limit = new Limit(num, gapMs, cbFail);
        keyLimits.put(key, limit);
    }

    public IRoute<FROM, KEY, DATA, RV> getRealMsgRoute() {
        return realMsgRoute;
    }

    public void setRealMsgRoute(IRoute<FROM, KEY, DATA, RV> realMsgRoute) {
        this.realMsgRoute = realMsgRoute;
    }

    private boolean passLimit(Limit limit, long curTime, FROM from, KEY key, DATA data) throws Exception {
        if (limit.time + limit.gap < curTime) {
            limit.time = curTime;
            limit.curNum = 0;
        }
        limit.curNum++;
        if (limit.curNum > limit.num) {
            limit.cbFail.call(from, key, data);
            return false;
        }
        return true;
    }

    @Override
    public RV trigger(FROM from, KEY key, DATA data) throws Exception {
        long curTime = System.currentTimeMillis();
        if (limitGlobal != null) {
            if (!passLimit(limitGlobal, curTime, from, key, data)) {
                return null;
            }
        }

        Limit keyLimit = keyLimits.get(key);
        if (keyLimit == null && limitCommon != null) {
            keyLimit = new Limit(limitCommon.num, limitCommon.gap, limitCommon.cbFail);
            keyLimits.put(key, keyLimit);
        }
        if (keyLimit != null) {
            if (!passLimit(keyLimit, curTime, from, key, data)) {
                return null;
            }
        }

        return realMsgRoute.trigger(from, key, data);
    }

    @Override
    public void register(KEY key, Callback<FROM, KEY, DATA, RV> cb) {
        realMsgRoute.register(key, cb);
    }

    @Override
    public void cancel(KEY key) {
        realMsgRoute.cancel(key);
    }

    @Override
    public boolean search(KEY key) {
        return realMsgRoute.search(key);
    }

    private static class Limit {
        Callback cbFail;
        private long time;
        // gap毫秒时间内，限制只能有num个事件
        private int num;
        private long gap;
        private int curNum;

        public Limit(int num, long gap, Callback cbFail) {
            this.num = num;
            this.gap = gap;
            this.cbFail = cbFail;
        }
    }
}
