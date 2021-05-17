package ru.itis.kpfu;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.itis.kpfu.domain.criteria.Criteria;
import ru.itis.kpfu.domain.criteria.CriteriaBuilder;
import ru.itis.kpfu.domain.data.PostgreSQLDatabase;
import ru.itis.kpfu.entity.Beat;
import ru.itis.kpfu.domain.jdbc.EntityManager;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws NoSuchFieldException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/db.properties"));
            DataSource dataSource = getDataSource(properties);
            EntityManager manager = new EntityManager(dataSource, new PostgreSQLDatabase());
            String tableName = "my_beatss";
//        manager.createTable(tableName, Beat.class);
//        System.out.println(manager.findById(tableName, Beat.class, Long.class, 3L));
            manager.save(tableName ,new Beat(1337L,"Azat Beat",12L));
            Criteria criteria = new CriteriaBuilder()
                    .equals("title","34")
                    .and()
                    .greaterThan("id",23)
                    .or()
                    .lessThan("price",25)
                    .build();
            System.out.println("<------------------------------------------------------------------->");
            manager.findAll(tableName,Beat.class,criteria).get().stream().forEach(System.out::println);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    private static DataSource getData(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setDriverClassName(properties.getProperty("db.driver.classname"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.hikari.max-pool-size")));
        return new HikariDataSource(config);
    }
}
