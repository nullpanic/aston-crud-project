package dev.nullpanic.crudproject.services;

import com.zaxxer.hikari.HikariConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HikariConfigServiceImpl implements HikariConfigService {
    @Override
    public HikariConfig getConfigFromProperties(String fileName) {
        HikariConfig config = new HikariConfig();
        Properties properties = new Properties();

        try (InputStream inputStream = this.getClass()
                .getClassLoader().getResourceAsStream(fileName)) {
            properties.load(inputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        config.setDriverClassName(org.postgresql.Driver.class.getName());
        config.setJdbcUrl(properties.getProperty("db.jdbc.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));

        return config;
    }
}
