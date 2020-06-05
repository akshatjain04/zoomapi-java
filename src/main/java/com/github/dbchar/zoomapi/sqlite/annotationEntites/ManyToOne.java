package com.github.dbchar.zoomapi.sqlite.annotationEntites;

public class ManyToOne extends Column {
    private Class<?> manyClass;

    public Class<?> getManyClass() {
        return manyClass;
    }

    public void setManyClass(Class<?> manyClass) {
        this.manyClass = manyClass;
    }
}
