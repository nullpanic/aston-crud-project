package dev.nullpanic.crudproject.mappers;

import dev.nullpanic.crudproject.dto.RoleDTO;
import dev.nullpanic.crudproject.dto.UserDTO;
import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.models.User;

public class RoleMapperImpl implements RoleMapper{

    @Override
    public RoleDTO roleToRoleDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .role(role.getRole().toString())
                .build();
    }

    @Override
    public Role roleDTOToRole(RoleDTO roleDTO) {
        return Role.builder()
                .id(roleDTO.getId())
                .role(roleDTO.getRole())
                .build();
    }

    @Override
    public Role roleDTOToRoleWithoutId(RoleDTO roleDTO) {
        return Role.builder()
                .role(roleDTO.getRole())
                .build();
    }

    @Override
    public Role roleDTOToRoleWithId(RoleDTO roleDTO, Long id) {
        return Role.builder()
                .id(id)
                .role(roleDTO.getRole())
                .build();
    }
}
