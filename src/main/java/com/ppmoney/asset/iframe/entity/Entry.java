package com.ppmoney.asset.iframe.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by paul on 2018/5/10.
 */
@AllArgsConstructor
@Getter
@Setter
public class Entry<K, V> {
    private K key;
    private V value;

    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof Entry)) return false;

        Entry another = (Entry)object;
        return this.key.equals(another.key) && this.value.equals(another.value);
    }

    public int hashCode() {
        return this.key.hashCode() + this.value.hashCode();
    }
}
