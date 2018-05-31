package com.ppmoney.asset.iframe.entity;

import com.alibaba.fastjson.JSONObject;
import com.ppmoney.asset.iframe.util.PropertyGetter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 2018/5/13.
 */
public class Route {
    private static final Log logger = LogFactory.getLog(Route.class);


    private Object object = null;
    private String[] path = null;
    private List<Object> leaf = null;

    public Route(Object object, String[] path) {
        this(object, path, false);
    }

    public Route(Object object, String[] path, boolean append) {
        Assert.notNull(object, "entity should not be null.");
        Assert.notNull(path, "path should not be null.");

        this.object = object;
        this.path = path;

        routeToLeaf(append);
    }

    private void routeToLeaf(boolean append) {
        List<Object> current = new ArrayList<Object>();
        current.add(object);

        for (int i=0; i<path.length; i++)
            current = retrieve(current, path[i], append);

        leaf = current;
    }

    private List<Object> retrieve(List<Object> current, String key, boolean append) {
        if (key.length() == 0) {
            /**
             * For the reason that split method may return items with value of "",
             * so we just understand blank string as ignore this node and just return the origin list for chaining.
             */
            return current;
        }

        List<Object> result = new ArrayList<Object>();
        for (Object item : current) {
            Object children = PropertyGetter.get(item, key);
            result = mergeChildrenToResult(result, children);
        }

        return result;
    }

    private List<Object> mergeChildrenToResult(List<Object> result, Object children) {
        if (children == null) return result;

        if (!(children instanceof List)) {
            result.add(children);
            return result;
        }

        result.addAll((List)children);
        return result;
    }

    public Object search(String key) {
        Assert.notNull(leaf, "The property of leaf should not be null.");
        if (leaf.size() == 0) {
            logger.debug("Any does not contains correct node.");
            return null;
        }

        List<Object> collection = get(leaf.get(0), key);
        if (collection == null || collection.size() == 0)
            put(key, new JSONObject());

        collection = get(leaf.get(0), key);
        return collection.get(0);
    }

    public Object get(String key) {
        Assert.notNull(leaf, "The property of leaf should not be null.");
        if (leaf.size() == 0) {
            logger.debug("Any does not contains correct node.");
            return null;
        }

        List<Object> collection = get(leaf.get(0), key);
        if (collection == null || collection.size() == 0) return null;

        return collection.get(0); // Return the first element from the collection.
    }

    public List<Object> getAll(String key) {
        List<Object> result = new ArrayList<Object>();

        if (leaf.size() == 0) {
            logger.debug("Any does not contains correct node.");
            return null;
        }

        for (Object object : leaf) {
            List<Object> collection = get(object, key);
            result.addAll(collection);
        }

        return result;
    }

    /**
     * The reason that we design this method with List return type is because we may get an array
     * using object.get(key)
     * @param object
     * @param key
     * @return
     */
    private List<Object> get(Object object, String key) {
        List<Object> result = new ArrayList<Object>();

        // If key is null or empty string, this means we just want to get current object and return it back.
        if (key == null || key.length() == 0) {
            result.add(object);
            return result;
        }

        Method method = ReflectionUtils.findMethod(object.getClass(), "get", Object.class);
        if (method == null) {
            logger.error("The first element of leaf does not contains `get` method, element type: " + object.getClass());
            return result;
        }

        Object item = ReflectionUtils.invokeMethod(method, object, key);
        return unpackage(item);
    }

    protected List<Object> unpackage(Object value) {
        List<Object> result = new ArrayList<Object>();
        if (value == null) return result;

        if (value instanceof List)
            return (List)value;

        result.add(value);
        return result;
    }

    public void put(String key, Object value) {
        Assert.notNull(leaf, "The property leaf should not be null");
        Assert.notEmpty(leaf, "The property leaf should not be empty");

        // This assertion is just for simplifying the design difficulty,
        // we now just require the leaf can only contains one element.
        Assert.isTrue(leaf.size() == 1, "Only one element can be add new value.");

        Object target = leaf.get(0);
        Method method = ReflectionUtils.findMethod(target.getClass(), "put", Object.class, Object.class);
        ReflectionUtils.invokeMethod(method, target, key, value);
    }
}
