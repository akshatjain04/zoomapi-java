package com.github.dbchar.zoomapi.sqlite.annotationEntites;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.github.dbchar.zoomapi.sqlite.utils.FieldUtils.stringToDateTime;

public class Column {
    private String fieldName;
    private String columnName;
    private String defaultValue;
    private Class<?> dataType;
    private Field field;

    private Method getMethod;
    private Method setMethod;

    public void setValue(Object receiver, Object value) {
        if (setMethod != null && value != null) {
            try {
                if (dataType == String.class) {
                    setMethod.invoke(receiver, value.toString());
                } else if (dataType == int.class || dataType == Integer.class) {
                    setMethod.invoke(receiver, Integer.parseInt(value.toString()));
                } else if (dataType == float.class || dataType == Float.class) {
                    setMethod.invoke(receiver, Float.parseFloat(value.toString()));
                } else if (dataType == double.class || dataType == Double.class) {
                    setMethod.invoke(receiver, Double.parseDouble(value.toString()));
                } else if (dataType == long.class || dataType == Long.class) {
                    setMethod.invoke(receiver, Long.parseLong(value.toString()));
                } else if (dataType == java.util.Date.class || dataType == java.sql.Date.class) {
                    setMethod.invoke(receiver, stringToDateTime(value.toString()));
                } else if (dataType == boolean.class || dataType == Boolean.class) {
                    setMethod.invoke(receiver, "1".equals(value.toString()));
                } else {
                    setMethod.invoke(receiver, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                field.setAccessible(true);
                field.set(receiver, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Object obj) {
        if (obj != null && getMethod != null) {
            try {
                return (T) getMethod.invoke(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public Field getField() {
        return field;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }
}
