package com.sunil45.crimeregadmin;

import java.io.Serializable;

public class MyPair implements Serializable {
    String key;
    long value;

    public MyPair(String key, long value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public long getValue() {
        return value;
    }
}
