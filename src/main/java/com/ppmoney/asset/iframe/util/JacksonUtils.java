package com.ppmoney.asset.iframe.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by paul on 2018/5/19.
 */
public class JacksonUtils {
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
