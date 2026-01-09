package com.chargingstation.csbe.application.port.out;

import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import java.util.List;

public interface LoadChargingStationPort {
    List<ChargingStation> loadStations(String zcode);

    List<Charger> loadChargerStatus(String statId);
}
