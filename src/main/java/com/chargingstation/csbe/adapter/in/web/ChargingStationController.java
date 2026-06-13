package com.chargingstation.csbe.adapter.in.web;

import com.chargingstation.csbe.application.port.in.GetChargingStationUseCase;
import com.chargingstation.csbe.application.service.GuestUsageService;
import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;

@Path("/api/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChargingStationController {

    private final GetChargingStationUseCase getChargingStationUseCase;
    private final GuestUsageService guestUsageService;

    @Inject
    public ChargingStationController(GetChargingStationUseCase getChargingStationUseCase, GuestUsageService guestUsageService) {
        this.getChargingStationUseCase = getChargingStationUseCase;
        this.guestUsageService = guestUsageService;
    }


    @Inject
    JsonWebToken jwt;

    @GET
    @PermitAll
    public List<ChargingStation> getStations(@QueryParam("zcode") String zcode) {
        if (jwt != null && jwt.getClaimNames() != null && jwt.containsClaim("is_guest")) {
            Boolean isGuest = jwt.getClaim("is_guest");
            if (Boolean.TRUE.equals(isGuest)) {
                String email = jwt.getSubject();
                guestUsageService.checkAndIncrement(email);
            }
        }

        return getChargingStationUseCase.getStations(zcode);
    }

    @GET
    @Path("/regions")
    @PermitAll
    public Map<String, String> getRegions() {
        return getChargingStationUseCase.getRegions();
    }

    @GET
    @Path("/{statId}/chargers")
    @PermitAll
    public List<Charger> getChargerStatus(@PathParam("statId") String statId) {
        return getChargingStationUseCase.getChargerStatus(statId);
    }
}

