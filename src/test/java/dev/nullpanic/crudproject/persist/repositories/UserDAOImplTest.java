package dev.nullpanic.crudproject.persist.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.models.User;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDAOImplTest {
    @Container
    private final GenericContainer<?> postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:13.3"))
            .withDatabaseName("crud_db")
            .withUsername("admin")
            .withPassword("qwerty123")
            .withInitScript("db/init_script.sql")
            .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

    private DataSource dataSource;

    private UserDAO userDAO;
    private RoleDAO roleDAO;
    private User userJessy;
    private User userCassandra;
    private Role roleProgrammer;
    private Role roleAdmin;

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
        userDAO = new UserDAOImpl(dataSource);
        roleDAO = new RoleDAOImpl(dataSource);

        userJessy = User.builder()
                .id(1L)
                .name("Jessy")
                .build();
        userCassandra = User.builder()
                .id(2L)
                .name("Casandra")
                .build();

        roleAdmin = Role.builder()
                .id(1L)
                .role("Admin")
                .build();
        roleProgrammer = Role.builder()
                .id(2L)
                .role("Programmer")
                .build();

        userDAO.create(userJessy);
        userDAO.create(userCassandra);

        roleDAO.create(roleProgrammer);
        roleDAO.create(roleAdmin);

        userDAO.addRoleToUser(userJessy, roleAdmin);

    }

    @Test
    public void testGetById_WhenArgsExist_ShouldGetUser() throws SQLException {
        assertEquals(Optional.of(userJessy), userDAO.getById(1L));
    }

    @Test
    public void testCreate_WhenArgExist_ShouldCreateRole() throws SQLException {
        User userJimmy = User.builder()
                .name("Jimmy")
                .build();

        User user = userDAO.create(userJimmy);

        assertEquals(user.getId(), 3);
    }

    @Test
    public void testGetAll_WhenInvoked_ShouldGetAllRoles() throws SQLException {
        Set<User> user = userDAO.getAll();

        assertEquals(user.size(), 2);
    }

    @Test
    void testUpdate_WhenArgExist_ShouldReturnTrue() throws SQLException {
        userJessy.setName("Huan Pedro de Ramos");
        assertTrue(userDAO.update(userJessy));
    }

    @Test
    void testDelete_WhenArgExist_ShouldReturnTrue() throws SQLException {
        assertTrue(userDAO.delete(userJessy.getId()));
    }

    @Test
    void testGetUserRole_WhenArgExist_ShouldReturnAllUserRoles() throws SQLException {
        Set<Role> userRoles = userDAO.getUserRoles(userJessy.getId());
        assertEquals(userRoles.size(), 1);
    }

    @Test
    void testAddRoleToUser_WhenArgsExist_ShouldReturnTrue() throws SQLException {
        assertTrue(userDAO.addRoleToUser(userJessy, roleProgrammer));
    }

    @Test
    void testDeleteUserRole_WhenArgsExist_ShouldReturnTrue() throws SQLException {
        assertTrue(userDAO.deleteUserRole(userJessy, roleAdmin));
    }
}