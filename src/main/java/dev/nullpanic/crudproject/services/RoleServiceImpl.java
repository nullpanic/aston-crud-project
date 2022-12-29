package dev.nullpanic.crudproject.services;

import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.repositories.RoleDAO;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class RoleServiceImpl implements RoleService {

    RoleDAO roleDAO;

    public RoleServiceImpl(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    public Optional<Role> get(Long id) throws SQLException {
        return roleDAO.get(id);
    }

    @Override
    public Set<Role> getAll() throws SQLException {
        return roleDAO.getAll();
    }

    @Override
    public Role create(Role role) throws SQLException {
        return roleDAO.create(role);
    }

    @Override
    public boolean update(Role role) throws SQLException {
        return roleDAO.update(role);
    }

    @Override
    public boolean delete(Role role) throws SQLException {
        return roleDAO.delete(role);
    }
}
