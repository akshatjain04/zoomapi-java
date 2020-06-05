package com.github.dbchar.zoomapi.sqlite.annotationEntites;

import static com.github.dbchar.zoomapi.sqlite.utils.FieldUtils.DATE_FORMAT;

public class KeyValue {
    private String key;
    private Object value;

    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
//    if(value instanceof java.util.Date || value instanceof java.sql.Date){
        if (value instanceof java.util.Date) {
            return DATE_FORMAT.format(value);
        }
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
