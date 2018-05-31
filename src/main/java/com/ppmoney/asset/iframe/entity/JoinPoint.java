package com.ppmoney.asset.iframe.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

/**
 * Created by paul on 2018/5/11.
 */
@Getter
@Setter
public class JoinPoint {
    private String path;
    private Identity kvs;

    public JoinPoint(String path, Identity kvs) {
        Assert.notNull(path, "path should not be null.");
        Assert.notNull(kvs, "kvs should not be null.");
        this.path = path;
        this.kvs = kvs;
    }

    public boolean equals(Object object) {
        if (object == null) return false;
        if (!(object instanceof JoinPoint)) return false;

        JoinPoint another = (JoinPoint)object;
        return this.path.equals(another.path) && this.kvs.equals(another.kvs);
    }

    public int hashCode() {
        return this.path.hashCode() + this.kvs.hashCode();
    }
}
