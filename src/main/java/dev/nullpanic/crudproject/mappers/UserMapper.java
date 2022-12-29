package dev.nullpanic.crudproject.mappers;

import dev.nullpanic.crudproject.dto.UserDTO;
import dev.nullpanic.crudproject.persist.models.User;

public interface UserMapper {
    User userDTOToUser(UserDTO userDTO);

    UserDTO userToUserDTO(User user);

    User userDTOToUserWithoutId(UserDTO userDTO);

    User userDTOToUserWithId(UserDTO userDTO, Long id);
}
