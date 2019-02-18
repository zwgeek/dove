package zg.dove.net;

import java.util.HashMap;
import java.util.Map;

public class NetSessionContext {
    // 每连接属性
    public static final String SCOPE_SESSION = "_scope.session";
    // 每请求属性
    public static final String SCOPE_REQUEST = "_scope.request";

    private Map<String, Map<Object, Object>> scopeAttributes = new HashMap<>();


    // 获取顺序为 其它scope -> SCOPE_REQUEST -> SCOPE_SESSION
    public Object getAttribute(Object key) {
        return doGetAttribute(key);
    }

    private Object doGetAttribute(Object key) {
        Object value;
        Map<Object, Object> attributes;

        Map<Object, Object> sessionAttributes = null;
        Map<Object, Object> requestAttributes = null;
        for (Map.Entry<String, Map<Object, Object>> iter : scopeAttributes.entrySet()) {
            if (iter.getKey().equals(SCOPE_REQUEST)) {
                requestAttributes = iter.getValue();
                continue;
            }
            if (iter.getKey().equals(SCOPE_SESSION)) {
                sessionAttributes = iter.getValue();
                continue;
            }

            attributes = iter.getValue();
            value = attributes.get(key);
            if (value != null) {
                return value;
            }
        }

        if (requestAttributes != null) {
            value = requestAttributes.get(key);
            if (value != null) {
                return value;
            }
        }

        if (sessionAttributes != null) {
            value = sessionAttributes.get(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    public Object getAttribute(String scope, Object key) {
        Map<Object, Object> attributes = scopeAttributes.get(scope);
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }

    public Map<Object, Object> removeAttribute(String scope) {
        return scopeAttributes.remove(scope);
    }

    public Object removeAttribute(String scope, Object key) {
        Map<Object, Object> attributes = scopeAttributes.get(scope);
        if (attributes == null) {
            return null;
        }
        Object value = attributes.remove(key);
        if (attributes.isEmpty()) {
            scopeAttributes.remove(scope);
        }
        return value;
    }

    public Object putAttribute(String scope, Object key, Object value) {
        Map<Object, Object> attributes = scopeAttributes.get(scope);
        if (attributes == null) {
            attributes = new HashMap<>();
            scopeAttributes.put(scope, attributes);
        }
        return attributes.put(key, value);
    }
}

