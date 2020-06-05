package com.github.dbchar.zoomapi.sqlite.utils;

import com.github.dbchar.zoomapi.sqlite.annotations.Column;
import com.github.dbchar.zoomapi.sqlite.annotations.Id;
import com.github.dbchar.zoomapi.sqlite.annotations.ManyToOne;
import com.github.dbchar.zoomapi.sqlite.annotations.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FieldUtils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // region Public APIs

    public static Date stringToDateTime(String dateString) {
        if (dateString != null) {
            try {
                return DATE_FORMAT.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getColumnByField(Field field) {
        var column = field.getAnnotation(Column.class);
        if (column != null && !column.name().trim().isEmpty()) {
            return column.name();
        }

        var manyToOne = field.getAnnotation(ManyToOne.class);
        if (manyToOne != null && !manyToOne.column().trim().isEmpty()) {
            return manyToOne.column();
        }

        var oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany != null && !oneToMany.manyColumn().trim().isEmpty()) {
            return oneToMany.manyColumn();
        }

        var id = field.getAnnotation(Id.class);
        if (id != null && !id.name().isEmpty()) {
            return id.name();
        }

        return field.getName();
    }

    public static String getColumnDefaultValue(Field field) {
        var column = field.getAnnotation(Column.class);
        if (column != null && !column.defaultValue().trim().isEmpty()) {
            return column.defaultValue();
        }
        return null;
    }

    public static Method getFieldGetMethod(Class<?> clazz, Field field) {
        var fieldName = field.getName();
        var method = (Method) null;
        if (field.getType() == boolean.class) {
            method = getBooleanFieldGetMethod(clazz, fieldName);
        }
        if (method == null) {
            method = getFieldGetMethod(clazz, fieldName);
        }
        return method;
    }

    public static Method getFieldGetMethod(Class<?> clazz, String fieldName) {
        var methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getFieldSetMethod(Class<?> clazz, Field field) {
        var fieldName = field.getName();
        var methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return clazz.getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            if (field.getType() == boolean.class) {
                return getBooleanFieldSetMethod(clazz, field);
            }
        }
        return null;
    }

    public static Method getBooleanFieldGetMethod(Class<?> clazz, String fieldName) {
        var methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        if (isISStart(fieldName)) {
            methodName = fieldName;
        }
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getBooleanFieldSetMethod(Class<?> clazz, Field field) {
        var fieldName = field.getName();
        var methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        if (isISStart(field.getName())) {
            methodName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
        }
        try {
            return clazz.getDeclaredMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isBaseDateType(Field field) {
        Class<?> clazz = field.getType();
        return clazz.equals(String.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Date.class) ||
                clazz.equals(java.sql.Date.class) ||
                clazz.isPrimitive();
    }

    public static boolean isManyToOne(Field field) {
        return field.getAnnotation(ManyToOne.class) != null;
    }

    public static boolean isOneToMany(Field field) {
        return field.getAnnotation(OneToMany.class) != null;
    }

    // endregion

    // region Private Methods

    private static boolean isISStart(String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return false;
        }

        // start with 'is' and the following character is upper case (eg. isHidden)
        return fieldName.startsWith("is") && !Character.isLowerCase(fieldName.charAt(2));
    }

    // endregion
}
