package common.dto;

import common.Role;

import java.util.List;

public record GetUserByEmailResponse(
        String id,
        String email,
        String password,
        List<Role> roles,
        Boolean isVerified
) {
}
