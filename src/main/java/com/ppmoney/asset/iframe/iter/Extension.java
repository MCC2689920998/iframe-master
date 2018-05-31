package com.ppmoney.asset.iframe.iter;

import com.alibaba.fastjson.JSONObject;
import com.ppmoney.asset.iframe.entity.Identity;
import com.ppmoney.asset.iframe.entity.JoinPoint;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by paul on 2018/5/10.
 */
public class Extension {
    private final JSONObject model; // Should we store model here???
    private final Identity values;
    private final Map<JoinPoint, Set<Object>> contents;

    public Extension(JSONObject model, Identity values) {
        Assert.notNull(model, "model should not be null.");
        Assert.notNull(values, "identity should not be null.");

        this.model = model;
        this.values = values;

        this.contents = new ConcurrentHashMap<JoinPoint, Set<Object>>();
    }

    public void add(String path, Identity names, Object item) {
        Assert.notNull(path, "path should not be null");
        Assert.notNull(names, "names should not be null");
        Assert.notNull(item, "item should not be null");

        Identity kvs = names.zip(values);
        JoinPoint joinPoint = new JoinPoint(path, kvs);

        Set<Object> content = search(joinPoint);
        content.add(item);
    }

    private Set<Object> search(JoinPoint joinPoint) {
        Set<Object> content = contents.get(joinPoint);
        if (content != null) return content;

        contents.put(joinPoint, new HashSet<Object>());
        return contents.get(joinPoint);
    }

    public Set<Object> get(String path, Identity kvs) {
        Assert.notNull(path, "path should not be null.");
        Assert.notNull(kvs, "ksv should not be null.");

        JoinPoint joinPoint = new JoinPoint(path, kvs);
        return contents.get(joinPoint);
    }
}
