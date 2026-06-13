package com.chargingstation.csbe.adapter.in.web;

import com.chargingstation.csbe.application.port.in.GetChargingStationUseCase;
import com.chargingstation.csbe.application.service.GuestUsageService;
import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

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

    @GET
    @PermitAll
    public List<ChargingStation> getStations(@QueryParam("zcode") String zcode,
                                              @Context ContainerRequestContext requestContext) {
        // GuestTokenFilter extracts guest email into request property "guest.email"
        String guestEmail = (String) requestContext.getProperty("guest.email");
        if (guestEmail != null) {
            guestUsageService.checkAndIncrement(guestEmail);
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
