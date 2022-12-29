package dev.nullpanic.crudproject.persist.repositories;

import dev.nullpanic.crudproject.configs.DataSource;
import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.models.User;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDAOImpl implements UserDAO {

    private static final String SQL_GET_USER_ROLES = """
            SELECT
                roles.id,
                roles.name
            FROM user_roles
                INNER JOIN roles on user_roles.role_id = roles.id
            WHERE
                user_id = ?
            """;

    private static final String SQL_GET_USER_ROLE_BY_ID = """
                SELECT
                    roles.name
                FROM
                    user_roles
                    left join roles on user_roles.role_id = roles.id
                WHERE
                    user_id = ?
                    AND role_id = ?
            """;

    @Override
    public Optional<User> getById(Long id) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(User.builder()
                    .id(resultSet.getLong("id"))
                    .name(resultSet.getString("name"))
                    .build());
        }
    }

    @Override
    public Set<User> getAll() throws SQLException {
        try (Connection connection = DataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

            Set<User> users = new HashSet<>();

            while (resultSet.next()) {
                users.add(User.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("name"))
                        .build());
            }

            return users;
        }
    }

    @Override
    public User create(User user) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users(name) VALUES (?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating user failed, id not generated");
                }
                user.setId(generatedKeys.getLong("id"));
            }
            return user;
        }
    }

    @Override
    public Boolean update(User user) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET name = ? WHERE id = ?")) {
            statement.setString(1, user.getName());
            statement.setLong(2, user.getId());

            int affectedRows = statement.executeUpdate();

            return affectedRows != 0;
        }
    }

    @Override
    public Boolean delete(Long id) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {

            statement.setLong(1, id);

            return statement.executeUpdate() != 0;

        }
    }

    @Override
    public Set<Role> getUserRoles(Long userId) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_ROLES)) {
            statement.setLong(1, userId);

            ResultSet resultSet = statement.executeQuery();
            Set<Role> roles = new HashSet<>();

            while (resultSet.next()) {
                roles.add(Role.builder()
                        .id(resultSet.getLong("id"))
                        .role(resultSet.getString("name"))
                        .build());
            }

            return roles;
        }
    }


    @Override
    public Optional<Role> getUserRoleById(Long userId, Long roleId) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_GET_USER_ROLE_BY_ID)) {
            statement.setLong(1, userId);
            statement.setLong(2, roleId);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(Role.builder()
                    .role(resultSet.getString("name"))
                    .build());
        }
    }

    @Override
    public Boolean addRoleToUser(User user, Role role) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO user_roles VALUES (?,?)")) {
            statement.setLong(1, user.getId());
            statement.setLong(2, role.getId());

            return statement.executeUpdate() != 0;
        }
    }

    @Override
    public Boolean deleteUserRole(User user, Role role) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM user_roles WHERE user_id = ? AND role_id = ?")) {
            statement.setLong(1, user.getId());
            statement.setLong(2, role.getId());

            return statement.executeUpdate() != 0;
        }
    }
}
