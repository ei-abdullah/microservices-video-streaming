package dev.videostreaming.microservice.authservice.service;

import common.dto.GetUserByEmailResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class RemoteUserDetailsService implements UserDetailsService {
    private final RestTemplate restTemplate;

    @Override
    public @Nullable UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        GetUserByEmailResponse response = restTemplate.getForObject(
                "http://user-service/api/v1/user?email={email}",
                GetUserByEmailResponse.class,
                email
        );

        return User
                .withUsername(response.email())
                .password(response.password())
                .roles(response
                        .roles()
                        .stream()
                        .map(Enum::name)
                        .toArray(String[]::new)
                )
                .build();
    }
}