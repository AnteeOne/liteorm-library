package ru.itis.kpfu.domain.jdbc;

import ru.itis.kpfu.domain.criteria.Criteria;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcDispatcher {

    private final DataSource dataSource;

    public JdbcDispatcher(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object ... args) throws SQLException {
        try (PreparedStatement statement = getPreparedStatement(dataSource.getConnection(), sql, args);
             ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()) {
                    return Optional.of(rowMapper.mapRow(resultSet));
                }
        }
        return Optional.empty();
    }

    public <T> Optional<List<T>> queryForObjects(String sql, RowMapper<T> rowMapper, Object ... args) throws SQLException {
        List<T> resultList = new ArrayList();
        try (PreparedStatement statement = getPreparedStatement(dataSource.getConnection(), sql, args);
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                resultList.add(rowMapper.mapRow(resultSet));
            }
        }
        return Optional.of(resultList);
    }

    public <T> Optional<List<T>> queryForCriteriaObjects(String sql, RowMapper<T> rowMapper, Criteria criteria) throws SQLException {
        List<T> resultList = new ArrayList();
        try (PreparedStatement statement = getPreparedStatement(dataSource.getConnection(), sql + criteria.toString(), criteria.getValues());
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                resultList.add(rowMapper.mapRow(resultSet));
            }
        }
        return Optional.of(resultList);
    }

    public void execute(String sql, Object ... args) throws SQLException {
        try (PreparedStatement statement = getPreparedStatement(dataSource.getConnection(), sql, args)){
            statement.execute();
        }
    }

    private PreparedStatement getPreparedStatement(Connection connection, String sql, Object[] args) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            statement.setObject(i+1, args[i]);
        }
        return statement;
    }

    private PreparedStatement getPreparedStatement(Connection connection, String sql, ArrayList<Object> values) throws SQLException {
        System.out.println(sql);
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i+1, values.get(i));
        }
        System.out.println(statement.toString());
        return statement;
    }
}
