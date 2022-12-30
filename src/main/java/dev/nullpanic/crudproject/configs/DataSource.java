package dev.nullpanic.crudproject.configs;

import com.zaxxer.hikari.HikariConfig;
import lombok.Data;


@Data
public class DataSource {
    public static HikariConfig config = new HikariConfig();

    static {
        config.setDriverClassName(org.postgresql.Driver.class.getName());
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/crud_db");
        config.setUsername("admin");
        config.setPassword("qwerty123");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }


}
