package org.example.config;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

        if (properties.init) {
            executeInitializingQueries(properties.initScript);
        }
    }

    private DatabaseProperties getDatabaseProperties() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");
            if (is == null) {
                throw new IllegalStateException("Файл db.properties не найден в ресурсах");
            }
            Properties properties = new Properties();
            properties.load(is);

            return DatabaseProperties.builder()
                    .url(properties.getProperty("url"))
                    .username(properties.getProperty("username"))
                    .password(properties.getProperty("password"))
                    .init(Boolean.parseBoolean(properties.getProperty("init")))
                    .initScript(properties.getProperty("init-script"))
                    .build();
        }
        catch (Exception e) {
            throw new IllegalStateException("Ошибка при чтении параметров БД", e);
        }
    }

    private void executeInitializingQueries(String script) {
        log.info("Начало инициализации БД");
        List<String> queries = getInitializingQueries(script);
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                for (String query : queries) {
                    statement.addBatch(query);
                }
                statement.executeBatch();
            }
            catch (SQLException e) {
                throw new IllegalStateException("Ошибка при инициализации БД");
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("Ошибка при открытии соединения для инициализации БД");
        }
    }

    private List<String> getInitializingQueries(String scriptPath) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(scriptPath);
        if(is == null) {
            throw new IllegalStateException("Не могу извлечъ SQL файл для инициализации");
        }
        try {
            String source = new String(is.readAllBytes());
            is.close();
            ArrayList<String> queries = new ArrayList<>();
            for (String query : StringUtils.split(source, ';')) {
                queries.add(StringUtils.trim(query));
            }
            return queries;
        }
        catch (IOException e) {
            throw new IllegalStateException("Ошибка при чтении файла инициализации");
        }
    }

    public DataSource get() {
        return dataSource;
    }
}
