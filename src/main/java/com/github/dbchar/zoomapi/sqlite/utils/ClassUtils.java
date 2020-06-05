package com.github.dbchar.zoomapi.sqlite.utils;

import com.github.dbchar.zoomapi.sqlite.annotationEntites.Column;
import com.github.dbchar.zoomapi.sqlite.annotationEntites.ManyToOne;
import com.github.dbchar.zoomapi.sqlite.annotationEntites.OneToMany;
import com.github.dbchar.zoomapi.sqlite.annotations.Id;
import com.github.dbchar.zoomapi.sqlite.annotations.Table;
import com.github.dbchar.zoomapi.sqlite.manager.DatabaseException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {
    // region Public APIs

    public static String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null || table.name().trim().isEmpty()) {
            // when we don't get name from reflection, we use class name as default name and replace '.' to '_'
            return clazz.getName().replace('.', '_');
        }
        return table.name();
    }

    public static Field getPrimaryKeyField(Class<?> clazz) {
        var primaryKeyField = (Field) null;
        var fields = clazz.getDeclaredFields();

        for (var field : fields) {
            // get annotation ID
            if (field.getAnnotation(Id.class) != null) {
                primaryKeyField = field;
                break;
            }
        }

        if (primaryKeyField == null) {
            for (var field : fields) {
                // try '_id'
                if ("_id".equals(field.getName())) {
                    primaryKeyField = field;
                    break;
                }
            }
        }

        if (primaryKeyField == null) {
            for (var field : fields) {
                // try 'id'
                if ("id".equals(field.getName())) {
                    primaryKeyField = field;
                    break;
                }
            }
        }

        return primaryKeyField;
    }

    public static String getPrimaryKeyFieldName(Class<?> clazz) {
        var field = getPrimaryKeyField(clazz);
        return field == null ? null : field.getName();
    }

    public static List<Column> getColumns(Class<?> clazz) {
        var columns = new ArrayList<Column>();
        try {
            var fields = clazz.getDeclaredFields();
            var primaryKeyFieldName = getPrimaryKeyFieldName(clazz);
            for (Field field : fields) {
                if (FieldUtils.isBaseDateType(field)) {

                    if (field.getName().equals(primaryKeyFieldName)) { // filter primary key
                        continue;
                    }

                    var column = new Column();
                    column.setColumnName(FieldUtils.getColumnByField(field));
                    column.setFieldName(field.getName());
                    column.setDataType(field.getType());
                    column.setDefaultValue(FieldUtils.getColumnDefaultValue(field));
                    column.setSetMethod(FieldUtils.getFieldSetMethod(clazz, field));
                    column.setGetMethod(FieldUtils.getFieldGetMethod(clazz, field));
                    column.setField(field);
                    columns.add(column);
                }
            }
            return columns;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static List<ManyToOne> getManyToOneList(Class<?> clazz) {
        var manyToOnes = new ArrayList<ManyToOne>();
        try {
            var fields = clazz.getDeclaredFields();
            for (var field : fields) {
                if (FieldUtils.isManyToOne(field)) {
                    var manyToOne = new ManyToOne();
                    manyToOne.setManyClass(field.getType());
                    manyToOne.setColumnName(FieldUtils.getColumnByField(field));
                    manyToOne.setFieldName(field.getName());
                    manyToOne.setDataType(field.getType());
                    manyToOne.setSetMethod(FieldUtils.getFieldSetMethod(clazz, field));
                    manyToOne.setGetMethod(FieldUtils.getFieldGetMethod(clazz, field));
                    manyToOnes.add(manyToOne);
                }
            }
            return manyToOnes;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static List<OneToMany> getOneToManyList(Class<?> clazz) throws DatabaseException {
        var oneToManies = new ArrayList<OneToMany>();
        try {
            var fields = clazz.getDeclaredFields();
            for (var field : fields) {
                if (FieldUtils.isOneToMany(field)) {
                    var oneToMany = new OneToMany();
                    oneToMany.setColumnName(FieldUtils.getColumnByField(field));
                    oneToMany.setFieldName(field.getName());

                    var type = field.getGenericType();
                    if (type instanceof ParameterizedType) {
                        var parameterizedType = (ParameterizedType) field.getGenericType();
                        // if parameterizedType == 2 -> LazyLoader
                        if (parameterizedType.getActualTypeArguments().length == 1) {
                            var parameterizedClazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            if (parameterizedClazz != null)
                                oneToMany.setOneClass(parameterizedClazz);
                        } else {
                            var pClazz = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                            if (pClazz != null)
                                oneToMany.setOneClass(pClazz);
                        }
                    } else {
                        throw new DatabaseException("getOneToManyList Exception:" + field.getName() + "'s type is null");
                    }

                    // modify the bug of parameterizedType, field.getClass return Field
                    oneToMany.setDataType(field.getType());
                    oneToMany.setSetMethod(FieldUtils.getFieldSetMethod(clazz, field));
                    oneToMany.setGetMethod(FieldUtils.getFieldGetMethod(clazz, field));
                    oneToManies.add(oneToMany);
                }
            }
            return oneToManies;
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage(), e);
        }
    }

    // endregion
}
