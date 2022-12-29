package dev.nullpanic.crudproject.persist.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    private Long id;
    private String role;
    private Set<User> users;
}
