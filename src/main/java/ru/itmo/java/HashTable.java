package ru.itmo.java;

import java.util.Arrays;
import java.util.Objects;

public class HashTable {

    class KeyValuePair {
        boolean deleted;
        final Object key;
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
        return (int) (array.length * loadFactor);
    }



    public HashTable(int size, float loadFactor) {
        array = new KeyValuePair[size];
        if (loadFactor > 1 || loadFactor <= 0) {
            loadFactor = 0.5f;
        }
        this.loadFactor = loadFactor;
    }
    public HashTable(int size) {
        this(size, 0.5f);
    }



    private int GetExpectedIndex(Object key, int arrayLength)
    {
        int hash = key.hashCode();
        if (hash < 0) {
            hash = -hash;
        }
        return hash % arrayLength;
    }

    public Object put(Object key, Object value) {
        if (key == null) {
            return null;
        }
        while (count + 1 >= threshold()) {
            extend();
        }

        KeyValuePair pair = new KeyValuePair(key, value);

        int index = GetExpectedIndex(key, array.length);

        boolean haveKey = get(key) != null;

        int i = 0;
        while (array[index] != null) {
            if (haveKey) {
                if (array[index].key.equals(key)) {
                    Object toReturn = array[index].value;
                    array[index] = pair;
                    return toReturn;
                }
            }
            else if (array[index].deleted) {
                break;
            }
            i++;
            index += i*i;
            index = index % array.length;
        }

        array[index] = pair;
        count++;
        return null;
    }

    public Object get(Object key) {
        if (key == null) {
            return null;
        }

        int index = GetExpectedIndex(key, array.length);

        int i = 0;
        while (array[index] != null && i < count) {
            if (array[index].key.equals(key) && !array[index].deleted) {
                return array[index].value;
            }
            i++;
            index += i*i;
            index = index % array.length;
        }

        return null;
    }

    public Object remove(Object key) {
        if (key == null) {
            return null;
        }

        int index = GetExpectedIndex(key, array.length);

        int i = 0;
        while (array[index] != null) {
            if (!array[index].deleted && array[index].key.equals(key)) {
                break;
            }
            if (i >= count) {
                return null;
            }
            i++;
            index += i*i;
            index = index % array.length;
        }

        if (array[index] == null) {
            return null;
        }

        array[index].deleted = true;
        count--;
        return array[index].value;
    }

    public int size() {
        return count;
    }

    void extend() {
        KeyValuePair[] newArray = new KeyValuePair[array.length * 2];
        for (KeyValuePair pair:
             array) {

            if (pair == null) {
                continue;
            }

            int index = GetExpectedIndex(pair.key, newArray.length);

            int i = 0;
            while (newArray[index] != null) {
                i++;
                index += i*i;
                index = index % newArray.length;
            }
            newArray[index] = pair;
        }
        Arrays.fill(array, null);
        array = newArray;
    }
}