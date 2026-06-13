package com.chargingstation.csbe.config;

import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Base64;
import java.util.logging.Logger;

/**
 * Intercepts requests before SmallRye JWT validation.
 * Guest tokens are signed with HMAC-512 (app.jwt.secret), not RSA/JWKS like Supabase tokens.
 * This filter detects guest tokens by peeking at the JWT payload (without verification),
 * validates them manually, and removes them from the Authorization header so SmallRye JWT
 * won't attempt RSA verification (which would fail and return 401).
 * The guest email is stored as a request property for downstream use.
 */
@Provider
@ApplicationScoped
@Priority(Priorities.AUTHENTICATION - 100)
public class GuestTokenFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(GuestTokenFilter.class.getName());
    private static final String GUEST_ISSUER = "charging-station-guest";
    private static final String GUEST_EMAIL_PROPERTY = "guest.email";

    @Inject
    @ConfigProperty(name = "app.jwt.secret")
    String guestSecret;

    @Inject
    JWTParser jwtParser;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        if (!isGuestToken(token)) {
            return; // Leave Supabase tokens alone for SmallRye JWT to process
        }

        // It's a guest token — validate with HMAC secret and extract email
        try {
            JsonWebToken jwt = jwtParser.verify(token, guestSecret);
            String email = jwt.getSubject();
            // Store guest email for the controller to read
            requestContext.setProperty(GUEST_EMAIL_PROPERTY, email);
            // Remove Authorization header so SmallRye JWT won't try RSA verification
            requestContext.getHeaders().remove("Authorization");
        } catch (Exception e) {
            LOG.warning("Invalid guest token: " + e.getMessage());
            // Remove the invalid header - SmallRye JWT won't see it, @PermitAll allows anonymous
            requestContext.getHeaders().remove("Authorization");
        }
    }

    /**
     * Peeks at the JWT payload (without verification) to check if issuer is the guest issuer.
     */
    private boolean isGuestToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return false;
            String payload = new String(Base64.getUrlDecoder().decode(padBase64(parts[1])));
            return payload.contains("\"" + GUEST_ISSUER + "\"");
        } catch (Exception e) {
            return false;
        }
    }

    private String padBase64(String base64) {
        int padding = 4 - (base64.length() % 4);
        if (padding < 4) base64 += "=".repeat(padding);
        return base64;
    }
}
