package com.energyplatform.auth_service.auth.service;
import com.energyplatform.auth_service.auth.entity.Role;
import com.energyplatform.auth_service.auth.entity.User;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final SecretKey jwtSecretKey;

    private static final long EXPIRATION_SECONDS = 3600;

    public String generateToken(
            User user,
            Role role) {

        Instant now = Instant.now();

        List<String> permissions =
                role.getPermissions();

        return Jwts.builder()

                // username
                .subject(user.getUsername())

                // role
                .claim("role", role.getName())

                // permissions
                .claim("permissions", permissions)

                // issued time
                .issuedAt(Date.from(now))

                // expiration
                .expiration(
                        Date.from(
                                now.plusSeconds(
                                        EXPIRATION_SECONDS
                                )
                        )
                )

                // sign JWT
                .signWith(jwtSecretKey)

                .compact();
    }

    public long getExpirationSeconds() {
        return EXPIRATION_SECONDS;
    }
}