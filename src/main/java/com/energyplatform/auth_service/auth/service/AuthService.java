package com.energyplatform.auth_service.auth.service;

import com.energyplatform.auth_service.auth.dto.LoginRequest;
import com.energyplatform.auth_service.auth.dto.RegisterRequest;
import com.energyplatform.auth_service.auth.entity.User;
import com.energyplatform.auth_service.auth.repository.RoleRepository;
import com.energyplatform.auth_service.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    // =========================
    // REGISTER
    // =========================

    public Mono<User> register(
            RegisterRequest request) {

        return userRepository
                .existsByUsername(request.username())

                .flatMap(exists -> {

                    if (exists) {

                        return Mono.error(
                                new IllegalStateException(
                                        "Username already exists"
                                )
                        );
                    }

                    User user = new User();

                    user.setUsername(
                            request.username()
                    );

                    user.setPassword(
                            passwordEncoder.encode(
                                    request.password()
                            )
                    );

                    // New users are USER
                    user.setRole(request.role());


                    return userRepository.save(user);
                });
    }


    // =========================
    // LOGIN
    // =========================

    public Mono<String> login(
            LoginRequest request) {

        return userRepository
                .findByUsername(request.username())

                .switchIfEmpty(
                        Mono.error(
                                new BadCredentialsException(
                                        "Invalid username or password"
                                )
                        )
                )

                .filter(user ->
                        passwordEncoder.matches(
                                request.password(),
                                user.getPassword()
                        )
                )

                .switchIfEmpty(
                        Mono.error(
                                new BadCredentialsException(
                                        "Invalid username or password"
                                )
                        )
                )

                .flatMap(user ->

                        roleRepository
                                .findByName(user.getRole())

                                .switchIfEmpty(
                                        Mono.error(
                                                new IllegalStateException(
                                                        "Role not found: "
                                                                + user.getRole()
                                                )
                                        )
                                )

                                .map(role ->
                                        jwtService.generateToken(
                                                user,
                                                role
                                        )
                                )
                );
    }
}
