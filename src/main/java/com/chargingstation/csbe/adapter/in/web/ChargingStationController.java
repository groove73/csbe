package com.chargingstation.csbe.adapter.in.web;

import com.chargingstation.csbe.application.port.in.GetChargingStationUseCase;
import com.chargingstation.csbe.application.service.GuestUsageService;
import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class ChargingStationController {

    private final GetChargingStationUseCase getChargingStationUseCase;
    private final GuestUsageService guestUsageService;

    @GetMapping
    public List<ChargingStation> getStations(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String zcode) {

        if (jwt != null && Boolean.TRUE.equals(jwt.getClaim("is_guest"))) {
            String email = jwt.getSubject();
            guestUsageService.checkAndIncrement(email);
        }

        return getChargingStationUseCase.getStations(zcode);
    }

    @GetMapping("/regions")
    public Map<String, String> getRegions() {
        return getChargingStationUseCase.getRegions();
    }

    @GetMapping("/{statId}/chargers")
    public List<Charger> getChargerStatus(@PathVariable String statId) {
        return getChargingStationUseCase.getChargerStatus(statId);
    }
}
