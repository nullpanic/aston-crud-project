package dev.nullpanic.crudproject.mappers;

import dev.nullpanic.crudproject.dto.UserDTO;
import dev.nullpanic.crudproject.persist.models.User;

public class UserMapperImpl implements UserMapper {
    @Override
    public User userDTOToUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .build();
    }

    @Override
    public UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    @Override
    public User userDTOToUserWithoutId(UserDTO userDTO) {
        return User.builder()
                .name(userDTO.getName())
                .build();
    }

    @Override
    public User userDTOToUserWithId(UserDTO userDTO, Long id) {
        return User.builder()
                .id(id)
                .name(userDTO.getName())
                .build();
    }
}
