package common.userDetails;

import common.dto.GetUserByEmailResponse;
import common.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
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

        if (response == null) {
            throw new NotFoundException("User not found");
        }

        return new RemoteUserPrincipal(
                response.id(),
                response.email(),
                response.password(),
                response.roles(),
                response.isVerified()
        );
    }
}