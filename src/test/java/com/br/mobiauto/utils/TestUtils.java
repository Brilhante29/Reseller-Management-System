package com.br.mobiauto.utils;

import java.time.Duration;
import java.time.Instant;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {
    public static String objectToJson(Object obj) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateToken(String subject, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        var expiresIn = Instant.now().plus(Duration.ofHours(2));
        return JWT.create()
                .withIssuer("mobiauto")
                .withSubject(subject)
                .withExpiresAt(expiresIn)
                .withClaim("role", "ROLE_ADMIN")
                .sign(algorithm);
    }
}
