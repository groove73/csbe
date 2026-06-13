package com.chargingstation.csbe.domain;

import java.util.List;

public record ChargingStation(
    String statId,      // 충전소ID
    String statNm,      // 충전소명
    String addr,        // 주소
    String addrDetail,  // 주소상세
    String lat,         // 위도
    String lng,         // 경도
    String useTime,     // 이용가능시간
    String bnm,         // 기관명
    String busiId,      // 운영기관ID
    String busiNm,      // 운영기관명
    String busiCall,    // 운영기관연락처
    String parkingFree, // 주차료무료여부
    String note,        // 안내사항
    List<Charger> chargers
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String statId;
        private String statNm;
        private String addr;
        private String addrDetail;
        private String lat;
        private String lng;
        private String useTime;
        private String bnm;
        private String busiId;
        private String busiNm;
        private String busiCall;
        private String parkingFree;
        private String note;
        private List<Charger> chargers;

        public Builder statId(String statId) { this.statId = statId; return this; }
        public Builder statNm(String statNm) { this.statNm = statNm; return this; }
        public Builder addr(String addr) { this.addr = addr; return this; }
        public Builder addrDetail(String addrDetail) { this.addrDetail = addrDetail; return this; }
        public Builder lat(String lat) { this.lat = lat; return this; }
        public Builder lng(String lng) { this.lng = lng; return this; }
        public Builder useTime(String useTime) { this.useTime = useTime; return this; }
        public Builder bnm(String bnm) { this.bnm = bnm; return this; }
        public Builder busiId(String busiId) { this.busiId = busiId; return this; }
        public Builder busiNm(String busiNm) { this.busiNm = busiNm; return this; }
        public Builder busiCall(String busiCall) { this.busiCall = busiCall; return this; }
        public Builder parkingFree(String parkingFree) { this.parkingFree = parkingFree; return this; }
        public Builder note(String note) { this.note = note; return this; }
        public Builder chargers(List<Charger> chargers) { this.chargers = chargers; return this; }

        public ChargingStation build() {
            return new ChargingStation(statId, statNm, addr, addrDetail, lat, lng, useTime, bnm, busiId, busiNm, busiCall, parkingFree, note, chargers);
        }
    }
}

