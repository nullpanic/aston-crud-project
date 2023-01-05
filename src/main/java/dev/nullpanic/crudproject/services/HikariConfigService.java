package dev.nullpanic.crudproject.services;

import com.zaxxer.hikari.HikariConfig;

public interface HikariConfigService {
    HikariConfig getConfigFromProperties(String fileName);
}
