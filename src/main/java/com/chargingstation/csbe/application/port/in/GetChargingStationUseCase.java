package com.chargingstation.csbe.application.port.in;

import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import java.util.List;
import java.util.Map;

public interface GetChargingStationUseCase {
    List<ChargingStation> getStations(String zcode);

    List<Charger> getChargerStatus(String statId);

    Map<String, String> getRegions();
}
