package com.ppmoney.asset.iframe.entity;

/**
 * The abstraction of Identity, one Identity identify one unique Any Object.
 * Created by paul on 2018/5/10.
 */
public interface Identity<T> {
    boolean dimensionAs(Identity another);

    Identity<Entry> zip(Identity right);
    Identity unzip(ZipSide side);

    boolean matches(Object object);
    Identity values(Object object);

    // When T is of type Entry, we can use this method to set all Entry<K,V> into the object.
    void set(Object object);
}
