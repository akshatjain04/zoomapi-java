package com.github.dbchar.zoomapi.sqlite.manager;

import com.github.dbchar.zoomapi.sqlite.annotationEntites.Column;
import com.github.dbchar.zoomapi.sqlite.annotationEntites.KeyValue;
import com.github.dbchar.zoomapi.sqlite.annotationEntites.ManyToOne;
import com.github.dbchar.zoomapi.sqlite.annotationEntites.Table;

import java.util.ArrayList;
import java.util.List;

public class SqlBuilder {
    // region Public APIs (Select)

    public static SqlInfo getSelectSqlAsSqlInfo(Class<?> clazz, Object idValue) throws DatabaseException {
        var table = Table.get(clazz);
        var sqlInfo = new SqlInfo();
        sqlInfo.setSql(getSelectSqlByTableName(table.getTableName()) + " WHERE " + table.getId().getColumnName() + "=?");
        sqlInfo.addValue(idValue);
        return sqlInfo;
    }

    public static String getSelectSqlAsString(Class<?> clazz) throws DatabaseException {
        return getSelectSqlByTableName(Table.get(clazz).getTableName());
    }

    // endregion

    // region Public APIs (Insert)

    public static SqlInfo buildInsertSqlAsSqlInfo(Object entity) throws DatabaseException {
        var keyValues = getSaveKeyValueListByEntity(entity);
        var sql = new StringBuilder();
        var sqlInfo = (SqlInfo) null;
        if (!keyValues.isEmpty()) {
            sqlInfo = new SqlInfo();
            sql.append("INSERT INTO ");
            sql.append(Table.get(entity.getClass()).getTableName());
            sql.append(" (");

            for (var keyValue : keyValues) {
                sql.append(keyValue.getKey()).append(",");
                sqlInfo.addValue(keyValue.getValue());
            }

            sql.deleteCharAt(sql.length() - 1);
            sql.append(") VALUES (");

            var length = keyValues.size();
            sql.append("?,".repeat(length));
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");

            sqlInfo.setSql(sql.toString());
        }

        return sqlInfo;
    }

    // endregion

    // region Public APIs (Update)

    public static SqlInfo getUpdateSqlAsSqlInfo(Object entity) throws DatabaseException {
        var table = Table.get(entity.getClass());
        var idValue = table.getId().getValue(entity);

        if (null == idValue) { // primary key cannot be null, or we cannot update row
            throw new DatabaseException("this entity[" + entity.getClass() + "]'s id value is null");
        }

        var keyValues = getSaveKeyValueListByEntity(entity);
        if (keyValues.isEmpty()) {
            return null;
        }

        var sqlInfo = new SqlInfo();
        var sql = new StringBuilder("UPDATE ");
        sql.append(table.getTableName());
        sql.append(" SET ");

        for (var keyValue : keyValues) {
            sql.append(keyValue.getKey()).append("=?,");
            sqlInfo.addValue(keyValue.getValue());
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ").append(table.getId().getColumnName()).append("=?");
        sqlInfo.addValue(idValue);
        sqlInfo.setSql(sql.toString());
        return sqlInfo;
    }

    // endregion

    // region Public APIs (Delete)

    public static SqlInfo buildDeleteSqlAsSqlInfo(Object entity) throws DatabaseException {
        var table = Table.get(entity.getClass());

        var id = table.getId();
        var idValue = id.getValue(entity);

        if (idValue == null) {
            throw new DatabaseException("getDeleteSQL:" + entity.getClass() + " id value is null");
        }

        var sqlInfo = new SqlInfo();
        sqlInfo.setSql(getDeleteSqlByTableName(table.getTableName()) + " WHERE " + id.getColumnName() + "=?");
        sqlInfo.addValue(idValue);

        return sqlInfo;
    }

    // endregion

    // region Public API (Create Table)

    public static String getCreatTableSqlAsString(Class<?> clazz) throws DatabaseException {
        Table table = Table.get(clazz);

        var id = table.getId();
        var sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(table.getTableName());
        sql.append(" ( ");

        Class<?> primaryClazz = id.getDataType();
        if (primaryClazz == int.class || primaryClazz == Integer.class
                || primaryClazz == long.class || primaryClazz == Long.class) {
            sql.append(id.getColumnName()).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        } else {
            sql.append(id.getColumnName()).append(" TEXT PRIMARY KEY,");
        }

        var properties = table.columnMap.values();
        for (var property : properties) {
            sql.append(property.getColumnName());
            Class<?> dataType = property.getDataType();
            if (dataType == int.class || dataType == Integer.class
                    || dataType == long.class || dataType == Long.class) {
                sql.append(" INTEGER");
            } else if (dataType == float.class || dataType == Float.class
                    || dataType == double.class || dataType == Double.class) {
                sql.append(" REAL");
            } else if (dataType == boolean.class || dataType == Boolean.class) {
                sql.append(" NUMERIC");
            } else if (dataType == String.class) {
                sql.append(" TEXT");
            }
            sql.append(",");
        }

        var manyToOnes = table.manyToOneMap.values();
        for (var manyToOne : manyToOnes) {
            sql.append(manyToOne.getColumnName())
                    .append(" INTEGER")
                    .append(",");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" )");

        return sql.toString();
    }

    // endregion

    // region Private APIs

    private static String getSelectSqlByTableName(String tableName) {
        return "SELECT * FROM " + tableName;
    }

    private static String getDeleteSqlByTableName(String tableName) {
        return "DELETE FROM " + tableName;
    }

    private static List<KeyValue> getSaveKeyValueListByEntity(Object entity) throws DatabaseException {
        var keyValues = new ArrayList<KeyValue>();
        var table = Table.get(entity.getClass());

        // add properties
        var properties = table.columnMap.values();
        for (var property : properties) {
            var keyValue = propertyToKeyValue(property, entity);
            if (keyValue != null)
                keyValues.add(keyValue);
        }

        // add foreign key (many to one)
        var manyToOnes = table.manyToOneMap.values();
        for (var manyToOne : manyToOnes) {
            var keyValue = manyToOne2KeyValue(manyToOne, entity);
            if (keyValue != null) {
                keyValues.add(keyValue);
            }
        }

        return keyValues;
    }

    private static KeyValue propertyToKeyValue(Column column, Object entity) {
        var keyValue = (KeyValue) null;
        var propertyColumn = column.getColumnName();
        var value = (Object) column.getValue(entity);
        if (value != null) {
            keyValue = new KeyValue(propertyColumn, value);
        } else {
            if (column.getDefaultValue() != null && !column.getDefaultValue().trim().isEmpty())
                keyValue = new KeyValue(propertyColumn, column.getDefaultValue());
        }
        return keyValue;
    }

    private static KeyValue manyToOne2KeyValue(ManyToOne manyToOne, Object entity) throws DatabaseException {
        var keyValue = (KeyValue) null;
        var manyToOneColumn = manyToOne.getColumnName();
        var manyToOneObject = (Object) manyToOne.getValue(entity);
        if (manyToOneObject != null) {
            var manyToOneValue = Table.get(manyToOneObject.getClass()).getId().getValue(manyToOneObject);
            if (manyToOneColumn != null && manyToOneValue != null) {
                keyValue = new KeyValue(manyToOneColumn, manyToOneValue);
            }
        }

        return keyValue;
    }

    // endregion
}
