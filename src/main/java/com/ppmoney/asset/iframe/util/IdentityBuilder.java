package com.ppmoney.asset.iframe.util;

import com.ppmoney.asset.iframe.entity.Entry;
import com.ppmoney.asset.iframe.entity.Identity;
import com.ppmoney.asset.iframe.entity.ZipSide;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by paul on 2018/5/10.
 */
public class IdentityBuilder {
    // MetaIdentity is very like 1 in number and it is unique.
    public static final Identity<Entry> MetaIdentity = Impl.MetaIdentity;
    // VoidIdentity is very like 0 in number and it it unique.
    public static final Identity VoidIdentity = Impl.VoidIdentity;

    public static <T> Identity<T> build(T... serial) {
        if (serial == null || serial.length == 0)
            return Impl.MetaIdentity;

        return new Impl<T>(serial);
    }

    /**
     * The abstraction of Identity, one Identity identify one unique Any Object.
     * Created by paul on 2018/5/10.
     */
    private static final class Impl<T> implements Identity<T> {
        private static final Identity MetaIdentity = new Impl<Entry>();
        private static final Identity VoidIdentity = new Impl();

        private final T[] serial;

        private Impl(T... serial) {
            // We assert that all the serial is not null.
            for (T item : serial) {
                Assert.notNull(item, "all serial should not be null");
            }

            this.serial = serial;
        }

        public boolean equals(Object object) {
            if (object == null) return false;
            if (!(object instanceof Impl)) return false;

            Impl another = (Impl)object;

            // VoidIdentity is equals to any other identity
            if (another == VoidIdentity || this == VoidIdentity) return true;

            // Not the same size.
            if (!this.dimensionAs(another)) return false;

            // We just decide not to add this nonsequenced entry feature.
            // if (isEntryIdentity()) return equalsIgnoreSequence(another);

            // compare one by one in sequence.
            for (int i=0; i<serial.length; i++) {
                if (!(this.serial[i].equals(another.serial[i])))
                    return false;
            }

            return true;
        }

        private boolean equalsIgnoreSequence(Impl<Entry> another) {
            if (!this.dimensionAs(another)) return false;

            Set<Entry> allEntries = new HashSet<Entry>();
            for (Entry entry : another.serial)
                allEntries.add(entry);

            for (T item : serial) {
                if (!allEntries.contains(item)) return false;
            }

            return true;
        }

        public int hashCode() {
            int hashCode = 0;
            for (T item : serial) {
                hashCode += item.hashCode();
            }

            return hashCode;
        }

        public boolean dimensionAs(Identity another) {
            if (another == null) return false;

            return this.serial.length == ((Impl)another).serial.length;
        }

        public Identity<Entry> zip(Identity right) {
            Assert.notNull(right, "The right identity should not be null.");
            Assert.isTrue(this.dimensionAs(right), "The dimension of the two identity should be the same");

            // Meta Identity can only be zipped with Meta Identity
            // And the result is still a Meta Identity
            if (this == MetaIdentity && right == MetaIdentity)
                return MetaIdentity;

            Entry[] entries = new Entry[this.serial.length];
            for (int i=0; i<this.serial.length; i++) {
                entries[i] = new Entry(this.serial[i], ((Impl)right).serial[i]);
            }

            return new Impl<Entry>(entries);
        }

        public Identity unzip(ZipSide side) {
            if (this == MetaIdentity) return this; // Unzip Meta Identity will get an Meta Identity

            // Assert that all the serial item is types of Entry.
            for (T serialItem : serial) {
                Assert.isInstanceOf(Entry.class, serialItem, "this identity can not be unzipped.");
            }

            Object[] target = new Object[serial.length];
            for (int i=0; i<serial.length; i++) {
                if (side == ZipSide.Left) {
                    target[i] = ((Entry)serial[i]).getKey();
                    continue;
                }

                target[i] = ((Entry)serial[i]).getValue();
            }

            return new Impl(target);
        }

        /**
         * Only the serial element is of type Entry, can we uses this method.
         */
        public boolean matches(Object object) {
            Assert.notNull(object, "object should not be null");
            // When we say matches, the Identity must be of Entry type
            // Image Entry<K, V>, then every k, there is object.get(k) == v

            if (this == MetaIdentity) return true;

            // Assert that Identity is of type Entry
            Assert.isTrue(isEntryIdentity(), "this identity can not use matches.");

            for (T serialItem : serial) {
                Entry entry = (Entry)serialItem;
                Object value = PropertyGetter.get(object, (String)entry.getKey());
                if (!entry.getValue().equals(value)) return false;
            }

            return true;
        }

        public Identity values(Object object) {
            Assert.notNull(object, "object should not be null");

            Object[] items = new Object[serial.length];
            for (int i=0; i<serial.length; i++) {
                items[i] = PropertyGetter.get(object, (String)serial[i]);
            }

            return new Impl(items);
        }

        @Override
        public void set(Object object) {
            if (object == null) return ;

            for (Object value : this.serial) {
                Entry<String, Object> entry = (Entry<String, Object>)value;
                Reflection.set(object, entry.getKey(), entry.getValue());
            }
        }

        /**
         * check if the serial element is of type Entry.
         */
        private boolean isEntryIdentity() {
            if (this == MetaIdentity) return true;

            for (T element : serial) {
                if (!(element instanceof Entry)) return false;
            }

            return true;
        }
    }

}
