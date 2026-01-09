package com.chargingstation.csbe.application.service;

import com.chargingstation.csbe.application.port.in.GetChargingStationUseCase;
import com.chargingstation.csbe.application.port.out.LoadChargingStationPort;
import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import com.chargingstation.csbe.domain.utils.CommonCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChargingStationService implements GetChargingStationUseCase {

    private final LoadChargingStationPort loadChargingStationPort;

    @Override
    public List<ChargingStation> getStations(String zcode) {
        return loadChargingStationPort.loadStations(zcode);
    }

    @Override
    public List<Charger> getChargerStatus(String statId) {
        return loadChargingStationPort.loadChargerStatus(statId);
    }

    @Override
    public Map<String, String> getRegions() {
        return CommonCodeMapper.getRegionMap();
    }
}
