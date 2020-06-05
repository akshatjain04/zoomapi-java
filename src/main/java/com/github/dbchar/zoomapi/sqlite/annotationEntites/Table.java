package com.github.dbchar.zoomapi.sqlite.annotationEntites;

import com.github.dbchar.zoomapi.sqlite.manager.DatabaseException;
import com.github.dbchar.zoomapi.sqlite.utils.ClassUtils;
import com.github.dbchar.zoomapi.sqlite.utils.FieldUtils;

import java.util.HashMap;
import java.util.Map;

public class Table {
    private static final Map<String, Table> tableInfoMap = new HashMap<>();

    private String className;
    private String tableName;
    private Id id;
    private boolean isExist;

    public final HashMap<String, Column> columnMap = new HashMap<>();
    public final HashMap<String, OneToMany> oneToManyMap = new HashMap<>();
    public final HashMap<String, ManyToOne> manyToOneMap = new HashMap<>();

    private Table() {
    }

    @SuppressWarnings("unused")
    public static Table get(Class<?> clazz) throws DatabaseException {
        if (clazz == null) {
            throw new NullPointerException("Cannot get table info because the clazz is null");
        }

        var tableInfo = tableInfoMap.get(clazz.getName());

        if (tableInfo == null) {
            tableInfo = new Table();

            tableInfo.setTableName(ClassUtils.getTableName(clazz));
            tableInfo.setClassName(clazz.getName());

            var idField = ClassUtils.getPrimaryKeyField(clazz);
            if (idField != null) {
                var id = new Id();
                id.setColumnName(FieldUtils.getColumnByField(idField));
                id.setFieldName(idField.getName());
                id.setSetMethod(FieldUtils.getFieldSetMethod(clazz, idField));
                id.setGetMethod(FieldUtils.getFieldGetMethod(clazz, idField));
                id.setDataType(idField.getType());

                tableInfo.setId(id);
            } else {
                throw new DatabaseException("the class[" + clazz + "]'s id field is null, you can define '_id' or 'id' property or use annotation @id to solve this exception");
            }

            var columns = ClassUtils.getColumns(clazz);
            if (columns != null) {
                for (var column : columns) {
                    if (column != null)
                        tableInfo.columnMap.put(column.getColumnName(), column);
                }
            }

            var manyToOnes = ClassUtils.getManyToOneList(clazz);
            if (manyToOnes != null) {
                for (var manyToOne : manyToOnes) {
                    if (manyToOne != null)
                        tableInfo.manyToOneMap.put(manyToOne.getColumnName(), manyToOne);
                }
            }

            var oneToManies = ClassUtils.getOneToManyList(clazz);
            if (oneToManies != null) {
                for (var oneToMany : oneToManies) {
                    if (oneToMany != null)
                        tableInfo.oneToManyMap.put(oneToMany.getColumnName(), oneToMany);
                }
            }

            tableInfoMap.put(clazz.getName(), tableInfo);
        }

        return tableInfo;
    }

    public static Table get(String className) {
        try {
            return get(Class.forName(className));
        } catch (ClassNotFoundException | DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getClassName() {
        return className;
    }

    public String getTableName() {
        return tableName;
    }

    public Id getId() {
        return id;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public void setExist(boolean exist) {
        this.isExist = exist;
    }
}
