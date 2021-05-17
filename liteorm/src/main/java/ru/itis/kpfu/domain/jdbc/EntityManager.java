package ru.itis.kpfu.domain.jdbc;

import java.util.List;

import ru.itis.kpfu.domain.criteria.Criteria;
import ru.itis.kpfu.domain.data.IDatabase;
import ru.itis.kpfu.domain.converter.FieldToStringConverter;
import ru.itis.kpfu.domain.exceptions.UnfamiliarTypeException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Optional;

public class EntityManager {

    private final JdbcDispatcher jdbcDispatcher;
    private final FieldToStringConverter converterForDB;

    public EntityManager(DataSource dataSource , IDatabase dataBase) {
        converterForDB = new FieldToStringConverter(dataBase);
        jdbcDispatcher = new JdbcDispatcher(dataSource);
    }

    private String SQL_CREATE_TABLE = "create table " + TABLE_NAME_PATTERN + " (" + FIELD_NAME_WITH_TYPE_PATTERN + ")";

    public <T> void createTable(String tableName , Class<T> entityClass) {
        try {
            jdbcDispatcher.execute(insertValues(SQL_CREATE_TABLE , entityClass , null , tableName));
        } catch (SQLException | UnfamiliarTypeException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private final String SQL_INSERT = "insert into " + TABLE_NAME_PATTERN + " (" + FIELD_NAME_PATTERN + ") " + "values" + " (" + FIELD_VALUES + ")";

    public void save(String tableName , Object entity) {
        try {
            jdbcDispatcher.execute(insertValues(SQL_INSERT , entity.getClass() , entity , tableName));
        } catch (SQLException | IllegalAccessException | UnfamiliarTypeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private final String SQL_SELECT_BY_ID = "select " + FIELD_NAME_PATTERN + " from " + TABLE_NAME_PATTERN + " where id = ?";

    private final String SQL_SELECT_ALL = "select * from " + TABLE_NAME_PATTERN;

    public <T, ID> Optional<T> findById(String tableName , Class<T> resultType , Class<ID> idType , ID idValue) {
        try {
            return jdbcDispatcher.queryForObject(insertValues(SQL_SELECT_BY_ID , resultType , null , tableName) , getRowMapper(resultType) , idValue);
        } catch (SQLException | UnfamiliarTypeException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T> Optional<List<T>> findAll(String tableName , Class<T> resultType) {
        try {
            return jdbcDispatcher.queryForObjects(insertValues(SQL_SELECT_ALL , resultType , null , tableName),getRowMapper(resultType));
        } catch (IllegalAccessException | UnfamiliarTypeException | SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T> Optional<List<T>> findAll(String tableName , Class<T> resultType, Criteria criteria) {
        try {
            return jdbcDispatcher.queryForCriteriaObjects(insertValues(SQL_SELECT_ALL , resultType , null , tableName),getRowMapper(resultType),criteria);
        } catch (IllegalAccessException | UnfamiliarTypeException | SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private <T> String insertValues(String sql , Class<T> resultType , Object object , String tableName) throws UnfamiliarTypeException, IllegalAccessException {
        StringBuilder builder = new StringBuilder(sql);
        while (builder.indexOf(TABLE_NAME_PATTERN) != -1) {
            replacePatternOnValue(builder , TABLE_NAME_PATTERN , tableName);
        }
        while (builder.indexOf(FIELD_NAME_PATTERN) != -1) {
            replacePatternOnValue(builder , FIELD_NAME_PATTERN , converterForDB.addValuesInSqlStatement(false , resultType.getDeclaredFields()));
        }
        while (builder.indexOf(FIELD_NAME_WITH_TYPE_PATTERN) != -1) {
            replacePatternOnValue(builder , FIELD_NAME_WITH_TYPE_PATTERN , converterForDB.addValuesInSqlStatement(true , resultType.getDeclaredFields()));
        }
        while (builder.indexOf(FIELD_VALUES) != -1) {
            if (object != null) {
                replacePatternOnValue(builder , FIELD_VALUES , converterForDB.getStringObjectValues(resultType.getDeclaredFields() , object));
            } else {
                throw new IllegalStateException("object is null");
            }
        }
        return builder.toString();
    }

    private final String TNP = ":table";
    private final String FNP = ":fieldsName";
    private final String FNTP = ":fields";
    private final String FV = ":fieldValues";

    private void replacePatternOnValue(StringBuilder builder , String pattern , String value) {
        int startIndex = builder.indexOf(pattern);
        int endIndex = startIndex + pattern.length();
        builder.replace(startIndex , endIndex , value);
    }

    private <T> RowMapper<T> getRowMapper(Class<T> resultType) {
        return result -> {
            T object;
            try {
                object = resultType.getConstructor().newInstance();
                Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    field.set(object , result.getObject(field.getName()));
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
            return object;
        };
    }
}
