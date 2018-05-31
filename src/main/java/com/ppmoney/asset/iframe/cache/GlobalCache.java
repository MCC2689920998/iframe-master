package com.ppmoney.asset.iframe.cache;

import com.ppmoney.asset.iframe.entity.Identity;
import com.ppmoney.asset.iframe.util.IdentityBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is used to cache object.
 * Created by paul on 2018/5/9.
 */
public interface GlobalCache {
    // We design a special key to store the Cache Identity.
    static String IdKey = "__ID__KEY";

    static GlobalCache getInstance() {
        return Impl.getInstance();
    }

    <T> T get(Identity identity, Class<T> type);
    <T> Identity put(T object);

    void remove(Identity identity);
    void clear();

    final class Impl implements GlobalCache {
        private static final Log logger = LogFactory.getLog(Impl.class);

        // Let's implement the singleton mode.
        private static GlobalCache instance = null;
        public static final GlobalCache getInstance() {
            if (instance == null) {
                synchronized (Impl.class) {
                    if (instance == null) {
                        instance = new Impl();
                    }
                }
            }

            return instance;
        }

        /**
         * Property and Method of Impl
         */
        private transient Long serial = 0L;
        private Map<Identity, Object> cache = new ConcurrentHashMap<Identity, Object>();

        private Impl() {

        }

        @Override
        public <T> T get(Identity identity, Class<T> type) {
            return (T)cache.get(identity);
        }

        @Override
        public <T> Identity put(T object) {
            Identity identity = createIdentity();
            cache.put(identity, object);

            return identity;
        }

        // We just make sure there is at most one thread run this method at any time.
        private synchronized Identity createIdentity() {
            return IdentityBuilder.build(serial++);
        }

        @Override
        public void remove(Identity identity) {
            cache.remove(identity);
        }

        @Override
        public void clear() {
            cache.clear();
        }
    }
}
