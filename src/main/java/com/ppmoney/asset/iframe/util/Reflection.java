package com.ppmoney.asset.iframe.util;

import com.ppmoney.asset.iframe.entity.Route;
import com.ppmoney.asset.iframe.entity.SimpleRoute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by paul on 2018/5/9.
 */
public class Reflection {
    public static Log logger = LogFactory.getLog(Reflection.class);

    public static Object search(Object object, String signature) {
        Path path = new Path(signature);
        Route route = new Route(object, path.getPath(), true);

        return route.search(path.getKey());
    }

    /**
     * This is much like get function, the only difference is that it may return a List
     * @param object
     * @param signature
     * @return
     */
    public static Object getReference(Object object, String signature) {
        Path path = new Path(signature);
        Route route = new SimpleRoute(object, path.getPath());

        return route.get(path.getKey());
    }

    public static Object get(Object object, String signature) {
        Path path = new Path(signature);
        Route route = new Route(object, path.getPath());

        return route.get(path.getKey());
    }

    public static List<Object> getAll(Object object, String signature) {
        Path path = new Path(signature);
        Route route = new Route(object, path.getPath());

        return route.getAll(path.getKey());
    }

    /**
     * We name this method as set because we don't want to override the JSONObject's put method.
     * @param signature
     * @param value
     */
    public static void set(Object object, String signature, Object value) {
        Path path = new Path(signature);
        Route route = new Route(object, path.getPath());

        route.put(path.getKey(), value);
    }

    private static final class Path {
        private String path; // The path to the leaf element
        private String key; // The leaf element key

        public Path(String signature) {
            Assert.notNull(signature, "Signature should not be null.");
            int index = signature.lastIndexOf('.'); // Find the last index of split dot.
            if (index == -1) { // This means we want just pick up element from the root node.
                this.path = ""; // Does not have any path to leaf element, we use empty string so won't get NullPointException;
                this.key = signature; // signature is equals to the key for root.
                return ;
            }

            this.path = signature.substring(0, index).trim();
            this.key = signature.substring(index + 1).trim();

            // Make sure the dot is not the first or the last char in signature, eg: .key or key., this is illegal.
            Assert.isTrue(this.path.length() > 0, "The dot(.) is the first element of signature, it is illegal.");
            Assert.isTrue(this.key.length() > 0, "The dot(.) is the last element of signature, it is illegal.");
        }

        public boolean isRoot() {
            return this.path.length() == 0;
        }

        public String[] getPath() {
            return path.split("\\.");
        }

        public String getKey() {
            return key;
        }
    }
}
