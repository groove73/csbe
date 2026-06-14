package com.chargingstation.csbe.config;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Date;

@ApplicationScoped
public class JwtProvider {

    @Inject
    @ConfigProperty(name = "app.jwt.expiration-ms", defaultValue = "604800000")
    long expirationMs;

    @Inject
    @ConfigProperty(name = "app.jwt.secret")
    String guestSecret;

    public String generateGuestToken(String email) {
        long now = System.currentTimeMillis();
        return Jwt.claims()
                .subject(email)
                .issuer("charging-station-guest")
                .claim("is_guest", true)
                .issuedAt(now / 1000)
                .expiresAt((now + expirationMs) / 1000)
                .jws()
                .keyId("guest-key")
                .signWithSecret(guestSecret);
    }
}

