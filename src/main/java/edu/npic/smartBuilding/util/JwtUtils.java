package edu.npic.smartBuilding.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final JwtDecoder jwtDecoder;

    public String getEmailFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getId();
        }catch (JwtException e) {
            log.error("Failed to decode JWT token", e);
            return null;
        }
    }

}
