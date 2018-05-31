package com.ppmoney.asset.iframe.util;

import org.springframework.core.convert.support.DefaultConversionService;

/**
 * Created by paul on 2018/5/12.
 */
public class ConversionService {
    private static final DefaultConversionService conversionService = new DefaultConversionService();
    public static <T> T convert(Object source, Class<T> type) {
        return conversionService.convert(source, type);
    }
}
