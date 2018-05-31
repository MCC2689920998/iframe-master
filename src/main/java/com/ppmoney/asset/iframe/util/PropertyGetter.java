package com.ppmoney.asset.iframe.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Created by paul on 2018/5/13.
 */
public class PropertyGetter {
    public static Log logger = LogFactory.getLog(PropertyGetter.class);

    public static Object get(Object object, String key) {
        return get(object, key, false);
    }

    private static Object get(Object object, String key, boolean append) {
        Method method = ReflectionUtils.findMethod(object.getClass(), "get", Object.class);
        if (method == null) {
            logger.warn("Could not find `get` method for key: " + key + " of object: " + object);
            return null;
        }

        Object result = ReflectionUtils.invokeMethod(method, object, key);
        if (append == false) return result;

        if (result != null) return result;
        // As we have not found the child with name of `key`, so we should new one with JSONObject type and add it to the `object`
        result = new JSONObject();
        method = ReflectionUtils.findMethod(object.getClass(), "put", Object.class, Object.class);
        ReflectionUtils.invokeMethod(method, object, key, result);

        return result;
    }
}
