package ru.itmo.java;

import javax.tools.JavaCompiler;
import java.lang.management.GarbageCollectorMXBean;
import java.util.Arrays;
import java.util.Objects;

public class HashTable {

    class KeyValuePair {
        boolean deleted;
        Object key;
        Object value;
        public KeyValuePair(Object key, Object value) {
            this.key = key;
            this.value = value;
            deleted = false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            KeyValuePair that = (KeyValuePair) o;
            return Objects.equals(key, that.key) &&
                    Objects.equals(value, that.value);
        }
    }

    KeyValuePair[] array;
    int count;
    final float loadFactor;
    int threshold() {
        return (int)(array.length * loadFactor);
    }

    public HashTable(int size, float loadFactor) {
        array = new KeyValuePair[size];
        if (loadFactor > 1) {
            loadFactor = 0.5f;
        }
        this.loadFactor = loadFactor;
    }
    public HashTable(int size) {
        this(size, 0.5f);
    }

    public Object put(Object key, Object value) {
        if (key == null) {
            return null;
        }
        while (count + 1 >= threshold()) {
            extend();
        }

        KeyValuePair kp = new KeyValuePair(key, value);
        int h = key.hashCode();
        if (h < 0) {
            h = -h;
        }
        int idx = h % array.length;

        boolean haveKey = get(key) != null;

        int i = 0;
        while (array[idx] != null) {
            if (haveKey) {
                if (array[idx].key.equals(key)) {
                    Object toReturn = array[idx].value;
                    array[idx] = kp;
                    return toReturn;
                }
            }
            else if (array[idx].deleted) {
                break;
            }
            i++;
            idx += i*i;
            idx = idx % array.length;
        }

        array[idx] = kp;
        count++;
        return null;
    }

    public Object get(Object key) {
        if (key == null) {
            return null;
        }

        int h = key.hashCode();
        if (h < 0) {
            h = -h;
        }
        int idx = h % array.length;

        int i = 0;
        while (array[idx] != null && i < count) {
            if (array[idx].key.equals(key) && !array[idx].deleted) {
                return array[idx].value;
            }
            i++;
            idx += i*i;
            idx = idx % array.length;
        }

        return null;
    }

    public Object remove(Object key) {
        if (key == null) {
            return null;
        }

        int h = key.hashCode();
        if (h < 0) {
            h = -h;
        }
        int idx = h % array.length;

        int i = 0;
        while (array[idx] != null) {
            if (!array[idx].deleted && array[idx].key.equals(key)) {
                break;
            }
            if (i >= count) {
                return null;
            }
            i++;
            idx += i*i;
            idx = idx % array.length;
        }

        if (array[idx] == null) {
            return null;
        }

        array[idx].deleted = true;
        count--;
        return array[idx].value;
    }

    public int size() {
        return count;
    }

    void extend() {
        KeyValuePair[] newArray = new KeyValuePair[array.length * 2];
        for (KeyValuePair kp:
             array) {

            if (kp == null) {
                continue;
            }

            int h = kp.key.hashCode();
            if (h < 0) {
                h = -h;
            }

            int idx = h % newArray.length;

            int i = 0;
            while (newArray[idx] != null) {
                i++;
                idx += i*i;
                idx = idx % newArray.length;
            }
            newArray[idx] = kp;
        }
        Arrays.fill(array, null);
        array = newArray;
    }
}