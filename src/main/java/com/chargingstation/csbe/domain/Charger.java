package com.chargingstation.csbe.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Charger {
    private final String chgerId; // 충전기ID
    private final String chgerType; // 충전기타입
    private final String chgerTypeNm; // 충전기타입명
    private final String stat; // 충전기상태
    private final String statNm; // 충전기상태명
    private final String statUpdDt; // 상태갱신일시
    private final String lastTsdt; // 마지막 충전시작일시
    private final String lastTedt; // 마지막 충전종료일시
    private final String nowTsdt; // 충전중 시작일시
    private final String output; // 충전용량
    private final String method; // 충전방식
    private final String location; // 상세위치
    private final String limitYn; // 이용제한여부
    private final String limitDetail; // 이용제한사유
}
