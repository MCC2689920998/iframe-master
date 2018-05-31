package com.ppmoney.asset.iframe.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This Route is the simplified Route, it just return the
 * Created by paul on 2018/5/13.
 */
public class SimpleRoute extends Route {
    public SimpleRoute(Object object, String[] path) {
        super(object, path);
    }

    @Override
    protected List<Object> unpackage(Object value) {
        List<Object> result = new ArrayList<Object>();
        if (value == null) return result;

        result.add(value);
        return result;
    }
}
