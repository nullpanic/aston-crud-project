package dev.nullpanic.crudproject.persist.repositories;

import dev.nullpanic.crudproject.configs.DataSource;
import dev.nullpanic.crudproject.persist.models.Role;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RoleDAOImpl implements RoleDAO {

    @Override
    public Optional<Role> get(Long id) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM roles WHERE id = ?")) {
            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            return Optional.of(Role.builder()
                    .id(resultSet.getLong("id"))
                    .role(resultSet.getString("name"))
                    .build());
        }
    }

    @Override
    public Set<Role> getAll() throws SQLException {
        try (Connection connection = DataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM roles");
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
    public Role create(Role role) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO roles(name) VALUES (?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, role.getRole());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Creating user failed, id not generated");
                }
                role.setId(generatedKeys.getLong("id"));
            }

            return role;
        }
    }

    @Override
    public boolean update(Role role) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE roles SET name = ? WHERE id = ?")) {
            statement.setString(1, role.getRole());
            statement.setLong(2, role.getId());

            int affectedRows = statement.executeUpdate();

            return affectedRows != 0;
        }
    }

    @Override
    public boolean delete(Role role) throws SQLException {
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM roles WHERE id = ?")) {
            statement.setLong(1, role.getId());

            return statement.executeUpdate() != 0;
        }
    }
}
