package dev.nullpanic.crudproject.persist.repositories;


import dev.nullpanic.crudproject.persist.models.Role;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public interface RoleDAO {
    Optional<Role> get(Long id) throws SQLException;

    Set<Role> getAll() throws SQLException;

    Role create(Role role) throws SQLException;

    boolean update(Role role) throws SQLException;

    boolean delete(Role role) throws SQLException;
}
