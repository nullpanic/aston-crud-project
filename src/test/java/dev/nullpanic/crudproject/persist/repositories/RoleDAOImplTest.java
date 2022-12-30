package dev.nullpanic.crudproject.persist.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.nullpanic.crudproject.persist.models.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class RoleDAOImplTest {
    private RoleDAO roleDAO;

    @Container
    private final GenericContainer<?> postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:13.3"))
            .withDatabaseName("crud_db")
            .withUsername("admin")
            .withPassword("qwerty123")
            .withInitScript("db/init_script.sql")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

    private DataSource dataSource;

    private Role userRole;
    private Role adminRole;

    @BeforeEach
    public void init() throws SQLException {
        HikariConfig config = new HikariConfig();
        var jdbcContainer = (JdbcDatabaseContainer<?>) postgresContainer;
        config.setJdbcUrl(jdbcContainer.getJdbcUrl());
        config.setUsername(jdbcContainer.getUsername());
        config.setPassword(jdbcContainer.getPassword());
        config.setDriverClassName(jdbcContainer.getDriverClassName());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        roleDAO = new RoleDAOImpl(dataSource);

        userRole = Role.builder()
                .role("User")
                .build();
        adminRole = Role.builder()
                .role("Admin")
                .build();

        roleDAO.create(userRole);
        roleDAO.create(adminRole);
    }

    @Test
    public void testCreate_WhenArgExist_ShouldCreateRole() throws SQLException {
        Role roleUser = Role.builder()
                .role("Programmer")
                .build();

        roleUser = roleDAO.create(roleUser);

        assertEquals(roleUser.getId(), 3);
    }

    @Test
    public void testGetAll_WhenInvoked_ShouldGetAllRoles() throws SQLException {
        Set<Role> roles = roleDAO.getAll();

        assertEquals(roles.size(), 2);
    }

    @Test
    void testUpdate_WhenArgExist_ShouldReturnTrue() throws SQLException {
        adminRole.setRole("Programmer");
        assertTrue(roleDAO.update(adminRole));
    }

    @Test
    void testDelete_WhenArgExist_ShouldReturnTrue() throws SQLException {
        assertTrue(roleDAO.delete(userRole));
    }
}