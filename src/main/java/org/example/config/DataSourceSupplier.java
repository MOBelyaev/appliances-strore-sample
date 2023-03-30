package org.example.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Component
@Slf4j
public class DataSourceSupplier {

    @Builder
    private static class DatabaseProperties {
        private String url;
        private String username;
        private String password;
        private boolean init;
        private String initScript;
    }

    private final PGSimpleDataSource dataSource;

    public DataSourceSupplier() {
        DatabaseProperties properties = getDatabaseProperties();

        this.dataSource = new PGSimpleDataSource();

        this.dataSource.setUrl(properties.url);
        this.dataSource.setUser(properties.username);
        this.dataSource.setPassword(properties.password);

        executeLiquibase();
    }

    private DatabaseProperties getDatabaseProperties() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("db/db.properties");
            if (is == null) {
                throw new IllegalStateException("Файл db.properties не найден в ресурсах");
            }
            Properties properties = new Properties();
            properties.load(is);

            return DatabaseProperties.builder()
                    .url(properties.getProperty("url"))
                    .username(properties.getProperty("username"))
                    .password(properties.getProperty("password"))
                    .build();
        }
        catch (Exception e) {
            throw new IllegalStateException("Ошибка при чтении параметров БД", e);
        }
    }

    private void executeLiquibase() {
        try (Connection connection = dataSource.getConnection()) {
            String changeLog = "db/changelog.xml";
            JdbcConnection jdbcConnection = new JdbcConnection(connection);
            try (Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), jdbcConnection)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
            catch (LiquibaseException e) {
                log.error("Liquibase скрипты не исполнены", e);
            }
        }
        catch (SQLException e) {
            log.error("Ошибка при открытии соединения", e);
        }
    }

    public DataSource get() {
        return dataSource;
    }
}
