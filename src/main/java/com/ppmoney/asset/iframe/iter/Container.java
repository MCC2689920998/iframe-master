package com.ppmoney.asset.iframe.iter;

import com.alibaba.fastjson.JSONObject;
import com.ppmoney.asset.iframe.entity.Identity;
import com.ppmoney.asset.iframe.entity.ZipSide;
import com.ppmoney.asset.iframe.util.Reflection;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ppmoney.asset.iframe.util.IdentityBuilder.MetaIdentity;
import static com.ppmoney.asset.iframe.util.IdentityBuilder.VoidIdentity;

/**
 * Created by paul on 2018/5/10.
 */
public class Container {
    private final JSONObject model; // The origin model object.
    // The memory space used to store all the nodes.
    private final Map<Identity, Extension> extensions;
    private final Map<String, Set<Identity>> iterators;

    public Container(JSONObject model) {
        Assert.notNull(model, "model should not be null.");

        this.model = model;
        this.extensions = new ConcurrentHashMap<Identity, Extension>();
        this.iterators = new ConcurrentHashMap<String, Set<Identity>>();
    }

    public JSONObject getModel() {
        return model;
    }

    /**
     * This will return all the Identities of the special node with path equals `path`
     * @param path
     * @return
     */
    public Set<Identity> getIdentities(String path) {
        Assert.notNull(path, "the path should not be null");
        return iterators.get(path);
    }

    public Set<Object> search(String path, Identity kvs) {
        return search(path, kvs, false);
    }

    // When append is false, we will add a node in model if we did not find anything.
    public Set<Object> search(String path, Identity kvs, boolean append) {
        Assert.notNull(path, "path should not be null.");
        Assert.notNull(kvs, "kvs should not be null.");
        Set<Object> result = get(path, kvs);
        if (result != null) return result;

        // result == null this means we have not traversal the model with `path` and `kvs`, so let's do it now
        if (append) append(path, kvs); // In append mode, we should add a new object in model when it does not exist.

        update(path, kvs.unzip(ZipSide.Left));

        // and now, we try to get the result again for the reason that we have constructed it again.
        return get(path, kvs);
    }

    private void append(String path, Identity kvs) {
        if (kvs == MetaIdentity || kvs == VoidIdentity) {
            appendObject(path);
            return ;
        }

        appendList(path, kvs);
    }

    private void appendObject(String path) {
        // The search function will help us do all the things we need here.
        Reflection.search(model, path);
    }

    private void appendList(String path, Identity kvs) {
        List<Object> exists = (List<Object>)Reflection.getReference(model, path);
        if (exists == null) exists = new ArrayList<Object>();
        if (contains(exists, kvs)) return; // Okay, nothing else should do.

        JSONObject element = new JSONObject();
        kvs.set(element);

        exists.add(element);
        Reflection.set(model, path, exists);
    }

    private boolean contains(List<Object> collection, Identity kvs) {
        if (collection == null) return false;

        for (Object object : collection)
            if (kvs.matches(object)) return true;

        return false;
    }

    /**
     * return null means we have never traversal the model with this `path` and `kvs`
     * return empty list means we have traversal the model with this `path` and `kvs' but nothing has been found.
     * @param path
     * @param kvs
     * @return
     */
    private Set<Object> get(String path, Identity kvs) {
        Identity values = kvs.unzip(ZipSide.Right);
        Extension extension = extensions.get(values);
        if (extension == null) return null;

        return extension.get(path, kvs);
    }

    public void update(String path, Identity names) {
        Assert.notNull(path, "path should not be null.");
        Assert.notNull(names, "names should not be null.");

        List<Object> total = Reflection.getAll(model, path);

        for (Object element : total) {
            updateGroup(path, MetaIdentity, element); // update the all category.

            if (names != MetaIdentity) {
                Identity values = names.values(element);
                updateGroup(path, names.zip(values), element);
            }
        }
    }

    private void updateGroup(String path, Identity kvs, Object element) {
        Identity names = kvs.unzip(ZipSide.Left);
        Identity values = kvs.unzip(ZipSide.Right);

        Extension extension = searchExtension(values);
        Set<Identity> iterator = searchIterator(path);
        if (kvs.matches(element)) {
            extension.add(path, names, element);
            iterator.add(kvs); // Save the key-value Identity.
        }
    }

    private Extension searchExtension(Identity values) {
        Extension extension = extensions.get(values);
        if (extension != null) return extension;

        extensions.put(values, new Extension(model, values));
        return extensions.get(values);
    }

    private Set<Identity> searchIterator(String path) {
        Set<Identity> iterator = iterators.get(path);
        if (iterator != null) return iterator;

        iterators.put(path, new HashSet<Identity>());
        return iterators.get(path);
    }
}
