package com.github.dbchar.zoomapi.sqlite.annotationEntites;

public class OneToMany extends Column {
    private Class<?> oneClass;

    public Class<?> getOneClass() {
        return oneClass;
    }

    public void setOneClass(Class<?> oneClass) {
        this.oneClass = oneClass;
    }
}
