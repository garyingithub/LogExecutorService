package org.yawlfoundation.cloud;

/**
 * Created by gary on 29/05/2017.
 */
public class Pair<T,B> {
    T key;
    B value;

    public Pair(T key, B value) {
        this.key = key;
        this.value = value;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public B getValue() {
        return value;
    }

    public void setValue(B value) {
        this.value = value;
    }
}
