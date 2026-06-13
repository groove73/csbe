package com.chargingstation.csbe.adapter.in.grpc;

import com.chargingstation.csbe.application.port.in.GetChargingStationUseCase;
import com.chargingstation.csbe.domain.Charger;
import com.chargingstation.csbe.domain.ChargingStation;
import com.chargingstation.csbe.grpc.*;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class ChargingStationGrpcServiceImpl implements ChargingStationGrpcService {

    private final GetChargingStationUseCase getChargingStationUseCase;

    @Inject
    public ChargingStationGrpcServiceImpl(GetChargingStationUseCase getChargingStationUseCase) {
        this.getChargingStationUseCase = getChargingStationUseCase;
    }


    @Override
    public Uni<GetStationsResponse> getStations(GetStationsRequest request) {
        return Uni.createFrom().item(() -> {
            List<ChargingStation> domainStations = getChargingStationUseCase.getStations(request.getZcode());
            List<ChargingStationGrpc> grpcStations = domainStations.stream()
                    .map(this::mapStationToGrpc)
                    .collect(Collectors.toList());
            return GetStationsResponse.newBuilder().addAllStations(grpcStations).build();
        });
    }

    @Override
    public Uni<GetRegionsResponse> getRegions(GetRegionsRequest request) {
        return Uni.createFrom().item(() -> {
            var regions = getChargingStationUseCase.getRegions();
            return GetRegionsResponse.newBuilder().putAllRegions(regions).build();
        });
    }

    @Override
    public Uni<GetChargerStatusResponse> getChargerStatus(GetChargerStatusRequest request) {
        return Uni.createFrom().item(() -> {
            List<Charger> domainChargers = getChargingStationUseCase.getChargerStatus(request.getStatId());
            List<ChargerGrpc> grpcChargers = domainChargers.stream()
                    .map(this::mapChargerToGrpc)
                    .collect(Collectors.toList());
            return GetChargerStatusResponse.newBuilder().addAllChargers(grpcChargers).build();
        });
    }

    private ChargingStationGrpc mapStationToGrpc(ChargingStation s) {
        var builder = ChargingStationGrpc.newBuilder()
                .setStatId(clean(s.statId()))
                .setStatNm(clean(s.statNm()))
                .setAddr(clean(s.addr()))
                .setAddrDetail(clean(s.addrDetail()))
                .setLat(clean(s.lat()))
                .setLng(clean(s.lng()))
                .setUseTime(clean(s.useTime()))
                .setBnm(clean(s.bnm()))
                .setBusiId(clean(s.busiId()))
                .setBusiNm(clean(s.busiNm()))
                .setBusiCall(clean(s.busiCall()))
                .setParkingFree(clean(s.parkingFree()))
                .setNote(clean(s.note()));

        if (s.chargers() != null) {
            builder.addAllChargers(s.chargers().stream().map(this::mapChargerToGrpc).collect(Collectors.toList()));
        }
        return builder.build();
    }

    private ChargerGrpc mapChargerToGrpc(Charger c) {
        return ChargerGrpc.newBuilder()
                .setChgerId(clean(c.chgerId()))
                .setChgerType(clean(c.chgerType()))
                .setChgerTypeNm(clean(c.chgerTypeNm()))
                .setStat(clean(c.stat()))
                .setStatNm(clean(c.statNm()))
                .setStatUpdDt(clean(c.statUpdDt()))
                .setLastTsdt(clean(c.lastTsdt()))
                .setLastTedt(clean(c.lastTedt()))
                .setNowTsdt(clean(c.nowTsdt()))
                .setOutput(clean(c.output()))
                .setMethod(clean(c.method()))
                .setLocation(clean(c.location()))
                .setLimitYn(clean(c.limitYn()))
                .setLimitDetail(clean(c.limitDetail()))
                .build();
    }


    private String clean(String val) {
        return val == null ? "" : val;
    }
}
