package com.chargingstation.csbe.adapter.in.web;

import com.chargingstation.csbe.config.JwtProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/auth/guest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuestAuthController {

    private final JwtProvider jwtProvider;

    @Inject
    public GuestAuthController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }


    @POST
    public GuestAuthResponse login(GuestAuthRequest request) {
        if (request.email() == null || !request.email().contains("@")) {
            throw new WebApplicationException("Invalid email", Response.Status.BAD_REQUEST);
        }
        String token = jwtProvider.generateGuestToken(request.email());
        return new GuestAuthResponse(token);
    }


    public record GuestAuthRequest(String email) {}

    public record GuestAuthResponse(String token) {}
}


