package com.chargingstation.csbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
        private String jwkSetUri;

        @Value("${app.jwt.secret}")
        private String guestSecret;

        @Bean
        @org.springframework.core.annotation.Order(1)
        public SecurityFilterChain publicFilterChain(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/api/stations/regions", "/api/auth/guest")
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
                return http.build();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .authenticationManagerResolver(authenticationManagerResolver()));

                return http.build();
        }

        @Bean
        public org.springframework.security.authentication.AuthenticationManagerResolver<jakarta.servlet.http.HttpServletRequest> authenticationManagerResolver() {
                java.util.Map<String, org.springframework.security.authentication.AuthenticationManager> authenticationManagers = new java.util.HashMap<>();

                // Supabase (HS256 with Shared Secret)
                SecretKey supabaseKey = new SecretKeySpec(guestSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
                org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider supabaseProvider = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider(
                                NimbusJwtDecoder.withSecretKey(supabaseKey)
                                                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                                                .build());
                authenticationManagers.put("https://xvzylaubzixvlrzjrdon.supabase.co/auth/v1",
                                supabaseProvider::authenticate);

                // Guest
                SecretKey key = new SecretKeySpec(guestSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
                org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider guestProvider = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider(
                                NimbusJwtDecoder.withSecretKey(key)
                                                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS512)
                                                .build());
                authenticationManagers.put("charging-station-guest", guestProvider::authenticate);

                return new org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver(
                                authenticationManagers::get);
        }

        @Value("${cors.allowed-origins:http://localhost:3000}")
        private String allowedOrigins;

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Guest-ID"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
