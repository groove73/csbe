package com.chargingstation.csbe.adapter.in.web;

import com.chargingstation.csbe.config.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/guest")
@RequiredArgsConstructor
public class GuestAuthController {

    private final JwtProvider jwtProvider;

    @PostMapping
    public GuestAuthResponse login(@RequestBody GuestAuthRequest request) {
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        String token = jwtProvider.generateGuestToken(request.getEmail());
        return new GuestAuthResponse(token);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestAuthRequest {
        private String email;
    }

    @Data
    @AllArgsConstructor
    public static class GuestAuthResponse {
        private String token;
    }
}
