package common.dto;

import common.Role;

import java.util.List;

public record AuthenticatedUser(
        String id,
        String email,
        List<Role> roles
) {
}
