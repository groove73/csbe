package com.chargingstation.csbe.adapter.out.external;

import com.chargingstation.csbe.application.port.out.LoadChargingStationPort;
import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import com.chargingstation.csbe.domain.utils.CommonCodeMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChargingStationExternalAdapter implements LoadChargingStationPort {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @Value("${api.charging.key}")
    private String apiKey;

    @Override
    public List<ChargingStation> loadStations(String zcode) {
        try {
            String baseUrl = "http://apis.data.go.kr/B552584/EvCharger/getChargerInfo";
            StringBuilder urlBuilder = new StringBuilder(baseUrl);
            urlBuilder.append("?serviceKey=").append(apiKey);
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&dataType=JSON");

            if (zcode != null && !zcode.isEmpty()) {
                urlBuilder.append("&zcode=").append(zcode);
            }

            @SuppressWarnings("null")
            URI uri = URI.create(urlBuilder.toString());
            String response = restTemplate.getForObject(uri, String.class);
            return parseResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Charger> loadChargerStatus(String statId) {
        try {
            String baseUrl = "http://apis.data.go.kr/B552584/EvCharger/getChargerStatus";
            StringBuilder urlBuilder = new StringBuilder(baseUrl);
            urlBuilder.append("?serviceKey=").append(apiKey);
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&numOfRows=100");
            urlBuilder.append("&dataType=JSON");

            if (statId != null && !statId.isEmpty()) {
                urlBuilder.append("&statId=").append(statId);
            }

            @SuppressWarnings("null")
            URI uri = URI.create(urlBuilder.toString());
            String response = restTemplate.getForObject(uri, String.class);
            return parseChargerResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Charger> parseChargerResponse(String response) {
        List<Charger> chargers = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    chargers.add(Charger.builder()
                            .chgerId(item.path("chgerId").asText())
                            .chgerType(item.path("chgerType").asText())
                            .stat(item.path("stat").asText())
                            .statUpdDt(item.path("statUpdDt").asText())
                            .lastTsdt(item.path("lastTsdt").asText())
                            .lastTedt(item.path("lastTedt").asText())
                            .nowTsdt(item.path("nowTsdt").asText())
                            .method(item.path("method").asText())
                            .location(cleanNull(item.path("location").asText()))
                            .limitYn(item.path("limitYn").asText())
                            .limitDetail(item.path("limitDetail").asText())
                            .chgerTypeNm(CommonCodeMapper.getChargerTypeNm(item.path("chgerType").asText()))
                            .statNm(CommonCodeMapper.getChargerStatusNm(item.path("stat").asText()))
                            .build());
                }
            } else if (!items.isMissingNode()) {
                // Single item case
                chargers.add(Charger.builder()
                        .chgerId(items.path("chgerId").asText())
                        .chgerType(items.path("chgerType").asText())
                        .stat(items.path("stat").asText())
                        .statUpdDt(items.path("statUpdDt").asText())
                        .lastTsdt(items.path("lastTsdt").asText())
                        .lastTedt(items.path("lastTedt").asText())
                        .nowTsdt(items.path("nowTsdt").asText())
                        .output(items.path("output").asText())
                        .method(items.path("method").asText())
                        .location(cleanNull(items.path("location").asText()))
                        .limitYn(items.path("limitYn").asText())
                        .limitDetail(items.path("limitDetail").asText())
                        .chgerTypeNm(CommonCodeMapper.getChargerTypeNm(items.path("chgerType").asText()))
                        .statNm(CommonCodeMapper.getChargerStatusNm(items.path("stat").asText()))
                        .build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return chargers;
    }

    private List<ChargingStation> parseResponse(String response) {
        List<ChargingStation> stations = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("items").path("item");

            Map<String, List<Charger>> stationChargers = new HashMap<>();
            Map<String, JsonNode> stationData = new HashMap<>();

            if (items.isArray()) {
                for (JsonNode item : items) {
                    String statId = item.path("statId").asText();

                    Charger charger = Charger.builder()
                            .chgerId(item.path("chgerId").asText())
                            .chgerType(item.path("chgerType").asText())
                            .stat(item.path("stat").asText())
                            .statUpdDt(item.path("statUpdDt").asText())
                            .lastTsdt(item.path("lastTsdt").asText())
                            .lastTedt(item.path("lastTedt").asText())
                            .nowTsdt(item.path("nowTsdt").asText())
                            .method(item.path("method").asText())
                            .location(cleanNull(item.path("location").asText()))
                            .limitYn(item.path("limitYn").asText())
                            .limitDetail(item.path("limitDetail").asText())
                            .chgerTypeNm(CommonCodeMapper.getChargerTypeNm(item.path("chgerType").asText()))
                            .statNm(CommonCodeMapper.getChargerStatusNm(item.path("stat").asText()))
                            .build();

                    stationChargers.computeIfAbsent(statId, k -> new ArrayList<>()).add(charger);
                    stationData.putIfAbsent(statId, item);
                }
            }

            for (String statId : stationData.keySet()) {
                JsonNode item = stationData.get(statId);
                stations.add(ChargingStation.builder()
                        .statId(statId)
                        .statNm(item.path("statNm").asText())
                        .addr(item.path("addr").asText())
                        .addrDetail(cleanNull(item.path("addrDetail").asText()))
                        .lat(item.path("lat").asText())
                        .lng(item.path("lng").asText())
                        .useTime(item.path("useTime").asText())
                        .bnm(item.path("bnm").asText())
                        .busiId(item.path("busiId").asText())
                        .busiNm(CommonCodeMapper.getBusiNm(item.path("busiId").asText(), item.path("busiNm").asText()))
                        .busiCall(item.path("busiCall").asText())
                        .parkingFree(item.path("parkingFree").asText())
                        .note(item.path("note").asText())
                        .chargers(stationChargers.get(statId))
                        .build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stations;
    }

    private String cleanNull(String value) {
        if (value == null || value.equalsIgnoreCase("null")) {
            return "";
        }
        return value;
    }
}
