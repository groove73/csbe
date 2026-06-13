package com.chargingstation.csbe.domain;

public record Charger(
    String chgerId,      // 충전기ID
    String chgerType,    // 충전기타입
    String chgerTypeNm,  // 충전기타입명
    String stat,         // 충전기상태
    String statNm,       // 충전기상태명
    String statUpdDt,    // 상태갱신일시
    String lastTsdt,     // 마지막 충전시작일시
    String lastTedt,     // 마지막 충전종료일시
    String nowTsdt,      // 충전중 시작일시
    String output,       // 충전용량
    String method,       // 충전방식
    String location,     // 상세위치
    String limitYn,      // 이용제한여부
    String limitDetail   // 이용제한사유
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String chgerId;
        private String chgerType;
        private String chgerTypeNm;
        private String stat;
        private String statNm;
        private String statUpdDt;
        private String lastTsdt;
        private String lastTedt;
        private String nowTsdt;
        private String output;
        private String method;
        private String location;
        private String limitYn;
        private String limitDetail;

        public Builder chgerId(String chgerId) { this.chgerId = chgerId; return this; }
        public Builder chgerType(String chgerType) { this.chgerType = chgerType; return this; }
        public Builder chgerTypeNm(String chgerTypeNm) { this.chgerTypeNm = chgerTypeNm; return this; }
        public Builder stat(String stat) { this.stat = stat; return this; }
        public Builder statNm(String statNm) { this.statNm = statNm; return this; }
        public Builder statUpdDt(String statUpdDt) { this.statUpdDt = statUpdDt; return this; }
        public Builder lastTsdt(String lastTsdt) { this.lastTsdt = lastTsdt; return this; }
        public Builder lastTedt(String lastTedt) { this.lastTedt = lastTedt; return this; }
        public Builder nowTsdt(String nowTsdt) { this.nowTsdt = nowTsdt; return this; }
        public Builder output(String output) { this.output = output; return this; }
        public Builder method(String method) { this.method = method; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder limitYn(String limitYn) { this.limitYn = limitYn; return this; }
        public Builder limitDetail(String limitDetail) { this.limitDetail = limitDetail; return this; }

        public Charger build() {
            return new Charger(chgerId, chgerType, chgerTypeNm, stat, statNm, statUpdDt, lastTsdt, lastTedt, nowTsdt, output, method, location, limitYn, limitDetail);
        }
    }
}

