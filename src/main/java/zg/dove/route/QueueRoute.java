package zg.dove.route;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueRoute<FROM, KEY, DATA, RV> implements IRoute<FROM, KEY, DATA, RV> {
    private IRoute<FROM, KEY, DATA, RV> realMsgRoute = new DupKeyRoute<FROM, KEY, DATA, RV>();
    private Queue<Object[]> pendingMessages = new ConcurrentLinkedQueue<>();

    @Override
    public RV trigger(FROM from, KEY key, DATA data) {
        pendingMessages.add(new Object[]{from, key, data});
        return null;
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

    public void doTrigger() throws Exception {
        while (!pendingMessages.isEmpty()) {
            Object[] pendData = pendingMessages.poll();
            realMsgRoute.trigger((FROM) pendData[0], (KEY) pendData[1], (DATA) pendData[2]);
        }
    }
}

