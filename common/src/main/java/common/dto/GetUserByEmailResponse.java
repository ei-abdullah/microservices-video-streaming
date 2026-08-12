package common.dto;

import common.Role;

import java.util.List;

public record GetUserByEmailResponse(
        String email,
        String password,
        List<Role> roles
) {
}
