package dev.nullpanic.crudproject.mappers;

import dev.nullpanic.crudproject.dto.RoleDTO;
import dev.nullpanic.crudproject.persist.models.Role;

public interface RoleMapper {
    RoleDTO roleToRoleDTO(Role role);

    Role roleDTOToRole(RoleDTO roleDTO);

    Role roleDTOToRoleWithoutId(RoleDTO roleDTO);

    Role roleDTOToRoleWithId(RoleDTO roleDTO, Long id);
}
