package com.chargingstation.csbe.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Replicates the main-branch JwtIssuerAuthenticationManagerResolver approach in Quarkus.
 *
 * Two token types are accepted, both signed with app.jwt.secret:
 *   - Supabase tokens  (iss = Supabase URL)  → HS256
 *   - Guest tokens     (iss = "charging-station-guest") → HS512
 *
 * The filter runs before SmallRye JWT, strips the Authorization header so SmallRye JWT
 * is bypassed, and stores guest identity in request properties for the controller.
 */
@Provider
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 100)
public class GuestTokenFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(GuestTokenFilter.class.getName());

    private static final String GUEST_ISSUER = "charging-station-guest";
    static final String GUEST_EMAIL_PROPERTY = "guest.email";

    @Inject
    @ConfigProperty(name = "app.jwt.secret")
    String jwtSecret;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);

        try {
            SignedJWT jwt = SignedJWT.parse(token);
            String issuer = jwt.getJWTClaimsSet().getIssuer();

            if (GUEST_ISSUER.equals(issuer)) {
                // Guest token: HS512
                handleGuestToken(jwt, requestContext);
            } else {
                // Supabase token: HS256 — validate and strip so SmallRye JWT doesn't attempt JWKS
                handleSupabaseToken(jwt, requestContext);
            }
        } catch (Exception e) {
            LOG.warning("Failed to parse JWT: " + e.getMessage());
            // Remove invalid token — @PermitAll endpoints will proceed anonymously
            requestContext.getHeaders().remove("Authorization");
        }
    }

    private void handleGuestToken(SignedJWT jwt, ContainerRequestContext ctx) throws Exception {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA512");
        MACVerifier verifier = new MACVerifier(key, Collections.singleton(JWSAlgorithm.HS512.getName()));

        if (!jwt.verify(verifier)) {
            LOG.warning("Guest token signature invalid");
            ctx.getHeaders().remove("Authorization");
            return;
        }

        String email = jwt.getJWTClaimsSet().getSubject();
        ctx.setProperty(GUEST_EMAIL_PROPERTY, email);
        // Remove from Authorization so SmallRye JWT does not interfere
        ctx.getHeaders().remove("Authorization");
    }

    private void handleSupabaseToken(SignedJWT jwt, ContainerRequestContext ctx) throws Exception {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        MACVerifier verifier = new MACVerifier(key, Collections.singleton(JWSAlgorithm.HS256.getName()));

        if (!jwt.verify(verifier)) {
            LOG.warning("Supabase token signature invalid");
            ctx.getHeaders().remove("Authorization");
            return;
        }
        // Valid Supabase token — remove header (SmallRye JWT not used in this setup)
        ctx.getHeaders().remove("Authorization");
    }
}
