package com.chargingstation.csbe.adapter.in.web;

import com.chargingstation.csbe.application.port.in.GetChargingStationUseCase;
import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // For development, allow all origins
public class ChargingStationController {

    private final GetChargingStationUseCase getChargingStationUseCase;

    @GetMapping
    public List<ChargingStation> getStations(
            @RequestParam(required = false) String zcode) {
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
