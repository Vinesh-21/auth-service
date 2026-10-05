package com.energyplatform.auth_service.auth.controller;

import com.energyplatform.auth_service.auth.dto.AuthResponse;
import com.energyplatform.auth_service.auth.dto.LoginRequest;
import com.energyplatform.auth_service.auth.dto.RegisterRequest;
import com.energyplatform.auth_service.auth.dto.UserResponse;
import com.energyplatform.auth_service.auth.entity.User;
import com.energyplatform.auth_service.auth.service.AuthService;
import com.energyplatform.auth_service.auth.service.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtService jwtService;


    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public Mono<ResponseEntity<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        return authService
                .register(request)

                .map(this::toUserResponse)

                .map(userResponse ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(userResponse)
                );
    }


    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return authService
                .login(request)

                .map(token ->
                        ResponseEntity.ok(
                                new AuthResponse(
                                        token,
                                        "Bearer",
                                        jwtService
                                                .getExpirationSeconds()
                                )
                        )
                );
    }


    // =========================
    // USER RESPONSE
    // =========================

    private UserResponse toUserResponse(
            User user) {

        return new UserResponse(

                user.getId(),

                user.getUsername(),

                user.getRole()

        );
    }
}