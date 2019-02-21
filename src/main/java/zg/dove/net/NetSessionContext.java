package zg.dove.net;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

import java.util.HashMap;
import java.util.Map;

public class NetSessionContext {
    // 每连接属性
    public static final String SCOPE_SESSION = "_scope.session";
    // 每请求属性
    public static final String SCOPE_REQUEST = "_scope.request";


    // 获取顺序为 其它scope -> SCOPE_REQUEST -> SCOPE_SESSION
    public static Object getAttribute(Object scopeAttributes, Object key) {
        return doGetAttribute(scopeAttributes, key);
    }

    private static Object doGetAttribute(Object scopeAttributes, Object key) {
        Object sessionAttributes = ((AttributeMap)scopeAttributes).attr(AttributeKey.valueOf(SCOPE_REQUEST)).get();
        Object requestAttributes = ((AttributeMap)scopeAttributes).attr(AttributeKey.valueOf(SCOPE_SESSION)).get();

        if (requestAttributes != null) {
            return ((Map<Object, Object>)requestAttributes).get(key);
        }

        if (sessionAttributes != null) {
            return ((Map<Object, Object>)sessionAttributes).get(key);

        }

        return null;
    }

    public static Object getAttribute(Object scopeAttributes, String scope, Object key) {
        Object attributes = ((AttributeMap)scopeAttributes).attr(AttributeKey.valueOf(scope)).get();
        if (attributes == null) {
            return null;
        }
        return ((Map<Object, Object>)attributes).get(key);
    }

    public static Map<Object, Object> removeAttribute(Object scopeAttributes, String scope) {
        Object attributes = ((AttributeMap)scopeAttributes).attr(AttributeKey.valueOf(scope)).getAndRemove();
//        if (attributes == null) {
//            return null;
//        }
        return (Map<Object, Object>)attributes;
    }

    public static Object removeAttribute(Object scopeAttributes, String scope, Object key) {
        Object attributes = ((AttributeMap)scopeAttributes).attr(AttributeKey.valueOf(scope)).get();
        if (attributes == null) {
            return null;
        }
        Object value = ((Map<Object, Object>)attributes).remove(key);
        return value;
    }

    public static Object putAttribute(Object scopeAttributes, String scope, Object key, Object value) {
        Attribute<Object> _attributes = ((AttributeMap)scopeAttributes).attr(AttributeKey.valueOf(scope));
        Object attributes = _attributes.get();
        if (attributes == null) {
            attributes = new HashMap<>();
            _attributes.set(attributes);
        }
        return ((Map<Object, Object>)attributes).put(key, value);
    }
}

