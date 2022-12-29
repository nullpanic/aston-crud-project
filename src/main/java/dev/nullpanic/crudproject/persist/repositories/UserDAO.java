package dev.nullpanic.crudproject.persist.repositories;

import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.models.User;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public interface UserDAO {
    Optional<User> getById(Long id) throws SQLException;

    Set<User> getAll() throws SQLException;

    User create(User user) throws SQLException;

    Boolean update(User user) throws SQLException;

    Boolean delete(Long id) throws SQLException;

    Set<Role> getUserRoles(Long id) throws SQLException;

    Optional<Role> getUserRoleById(Long userId, Long roleId) throws SQLException;

    Boolean addRoleToUser(User user, Role role) throws SQLException;

    Boolean deleteUserRole(User user, Role role) throws SQLException;

}
