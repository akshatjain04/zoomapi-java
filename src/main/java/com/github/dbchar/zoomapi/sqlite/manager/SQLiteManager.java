package com.github.dbchar.zoomapi.sqlite.manager;

import com.github.dbchar.zoomapi.sqlite.annotationEntites.Table;
import com.github.dbchar.zoomapi.sqlite.annotations.Column;
import com.github.dbchar.zoomapi.sqlite.annotations.Entity;
import com.github.dbchar.zoomapi.sqlite.annotations.Id;
import com.github.dbchar.zoomapi.utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SQLiteManager {

  // region Private Constants

  private static final String JDBC_DRIVER = "org.sqlite.JDBC";
  private static final String DB_CONNECTION_URL_ROOT = "jdbc:sqlite:./";
  private static final String DEFAULT_DB_NAME = "zoom";

  // endregion

  // region Private Properties

  private static final HashMap<String, SQLiteManager> daoMap = new HashMap<>();
  private final String connectionUrl;

  // endregion

  // region Public APIs

  public synchronized static SQLiteManager init() {
    return init(DEFAULT_DB_NAME);
  }

  public synchronized static SQLiteManager init(String databaseName) {
    var dao = daoMap.get(databaseName);
    if (dao == null) {
      dao = new SQLiteManager(databaseName);
      daoMap.put(databaseName, dao);
    }
    return dao;
  }

  public void save(Object entity) throws Exception {
    checkValid(entity.getClass());
    var idField = entity.getClass().getDeclaredField("id");
    idField.setAccessible(true);
    var id = idField.getInt(entity);
    if (findById(id, entity.getClass()) == null) {
      insert(entity);
    } else {
      update(entity);
    }
  }

  public void delete(Object entity) throws Exception {
    checkValid(entity.getClass());
    executeUpdateBySqlInfo(SqlBuilder.buildDeleteSqlAsSqlInfo(entity));
  }

  public <EntityType> List<EntityType> findAll(Class<EntityType> clazz) throws Exception {
    checkValid(clazz);
    return executeQueryBySqlInfo(new SqlInfo(SqlBuilder.getSelectSqlAsString(clazz)), clazz);
  }

  public <EntityType> EntityType findById(Object id, Class<EntityType> clazz) throws Exception {
    checkValid(clazz);
    var results = executeQueryBySqlInfo(SqlBuilder.getSelectSqlAsSqlInfo(clazz, id), clazz);
    return !results.isEmpty() ? results.get(0) : null;
  }

  // endregion

  // region Private APIs (Execute Query Methods)

  private <EntityType> List<EntityType> executeQueryBySqlInfo(SqlInfo sqlInfo, Class<EntityType> clazz) throws Exception {
    if (sqlInfo == null) {
      throw new Exception("sqlInfo is null");
    }

    Logger.printSql(sqlInfo.getSql());

    if (sqlInfo.getBindArgsAsArray() == null) {
      return executeQuery(sqlInfo.getSql(), clazz);
    } else {
      return executeQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray(), clazz);
    }
  }

  private <EntityType> List<EntityType> executeQuery(String query, Class<EntityType> clazz) {
    return executeQuery(query, new Object[]{}, clazz);
  }

  private <EntityType> List<EntityType> executeQuery(String query, Object[] bindArguments, Class<EntityType> clazz) {
    var results = new ArrayList<EntityType>();
    try (var connection = getConnection()) {
      try (var preparedStatement = connection.prepareStatement(query)) {
        setPreparedStatementObjects(preparedStatement, bindArguments);
        try (var resultSet = preparedStatement.executeQuery()) {
          while (resultSet.next()) {
            EntityType entity = clazz.getDeclaredConstructor().newInstance();
            loadResultSetIntoEntity(resultSet, entity);
            results.add(entity);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return results;
  }

  private void loadResultSetIntoEntity(ResultSet resultSet, Object entity) throws IllegalArgumentException, IllegalAccessException, SQLException {
    Class<?> clazz = entity.getClass();

    for (var field : clazz.getDeclaredFields()) {
      field.setAccessible(true);

      // set id column
      var id = field.getAnnotation(Id.class);
      if (id != null) {
        var idValue = resultSet.getObject(id.name());
        field.set(entity, idValue);
      }

      // set other columns
      var column = field.getAnnotation(Column.class);
      if (column != null) {
        var columnValue = resultSet.getObject(column.name());
        field.set(entity, columnValue);
      }
    }
  }

  // endregion

  // region Private APIs (Execute Update Methods)

  private void executeUpdateBySqlInfo(SqlInfo sqlInfo) throws Exception {
    if (sqlInfo == null) {
      throw new Exception("sqlInfo is null");
    }

    Logger.printSql(sqlInfo.getSql());

    if (sqlInfo.getBindArgsAsArray() == null) {
      executeUpdate(sqlInfo.getSql());
    } else {
      executeUpdate(sqlInfo.getSql(), sqlInfo.getBindArgsAsArray());
    }
  }

  private void executeUpdate(String sql) {
    executeUpdate(sql, new Object[]{});
  }

  private void executeUpdate(String sql, Object[] bindArguments) {
    try (var connection = getConnection()) {
      try (var preparedStatement = connection.prepareStatement(sql)) {
        setPreparedStatementObjects(preparedStatement, bindArguments);
        preparedStatement.executeUpdate();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setPreparedStatementObjects(PreparedStatement preparedStatement, Object[] bindArguments) throws SQLException {
    if (bindArguments.length > 0) {
      for (int i = 0; i < bindArguments.length; i++) {
        var arg = bindArguments[i];
        if (arg instanceof String) {
          preparedStatement.setString((i + 1), String.valueOf(arg));
        } else if (arg instanceof Integer) {
          preparedStatement.setInt((i + 1), Integer.parseInt(String.valueOf(arg)));
        } else if (arg instanceof Double) {
          preparedStatement.setDouble((i + 1), Double.parseDouble(String.valueOf(arg)));
        } else {
          preparedStatement.setObject((i + 1), arg);
        }
      }
    }
  }

  // endregion

  // region Private APIs (SQL Related Methods)

  private SQLiteManager(String databaseName) {
    this.connectionUrl = DB_CONNECTION_URL_ROOT + databaseName + ".db";
  }

  private Connection getConnection() throws ClassNotFoundException, SQLException {
    // will create database if not exist
    Class.forName(JDBC_DRIVER);
    return DriverManager.getConnection(connectionUrl);
  }

  private void insert(Object entity) throws Exception {
    checkValid(entity.getClass());
    executeUpdateBySqlInfo(SqlBuilder.buildInsertSqlAsSqlInfo(entity));
  }

  private void update(Object entity) throws Exception {
    checkValid(entity.getClass());
    executeUpdateBySqlInfo(SqlBuilder.getUpdateSqlAsSqlInfo(entity));
  }

  // endregion

  // region Private APIs (Validate Methods)

  private void checkValid(Class<?> clazz) throws Exception {
    // check if the entity is existed
    var entity = clazz.getAnnotation(Entity.class);
    if (entity == null) {
      throw new Exception("class '" + clazz.getName() + "' is not an entity(@Entity)");
    }

    // check if the table is existed
    // if a table is not existed, then create a new one
    if (!tableIsExist(Table.get(clazz))) {
      executeUpdateBySqlInfo(new SqlInfo(SqlBuilder.getCreatTableSqlAsString(clazz)));
    }
  }

  private boolean tableIsExist(Table table) {
    if (table.isExist()) {
      return true;
    }

    var sql = "SELECT COUNT(*) AS c FROM sqlite_master WHERE type ='table' AND name ='" + table.getTableName() + "' ";
    Logger.printSql(sql);
    try (var connection = getConnection()) {
      try (var statement = connection.createStatement()) {
        try (var resultSet = statement.executeQuery(sql)) {
          if (resultSet.getInt("c") > 0) {
            // find a table, update table info and return true
            table.setExist(true);
            return true;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
  }
  // endregion
}
