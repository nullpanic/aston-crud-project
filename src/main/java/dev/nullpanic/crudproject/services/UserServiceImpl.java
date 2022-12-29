package dev.nullpanic.crudproject.services;

import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.models.User;
import dev.nullpanic.crudproject.persist.repositories.UserDAO;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class UserServiceImpl implements UserService {

    UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Optional<User> getById(Long id) throws SQLException {
        return userDAO.getById(id);
    }

    @Override
    public Set<User> getAll() throws SQLException {
        return userDAO.getAll();
    }

    @Override
    public User create(User user) throws SQLException {
        return userDAO.create(user);
    }

    @Override
    public Boolean update(User user) throws SQLException {
        return userDAO.update(user);
    }

    @Override
    public Boolean delete(Long id) throws SQLException {
        return userDAO.delete(id);
    }

    @Override
    public Set<Role> getUserRoles(Long id) throws SQLException {
        return userDAO.getUserRoles(id);
    }

    @Override
    public Optional<Role> getUserRoleById(Long userId, Long roleId) throws SQLException {
        return userDAO.getUserRoleById(userId, roleId);
    }

    @Override
    public Boolean addRoleToUser(User user, Role role) throws SQLException {
        return userDAO.addRoleToUser(user, role);
    }

    @Override
    public Boolean deleteUserRole(User user, Role role) throws SQLException {
        return userDAO.deleteUserRole(user, role);
    }
}
