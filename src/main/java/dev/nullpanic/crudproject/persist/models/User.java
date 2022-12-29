package dev.nullpanic.crudproject.persist.models;

import lombok.*;

import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String name;
    private Set<Role> roles;
}
