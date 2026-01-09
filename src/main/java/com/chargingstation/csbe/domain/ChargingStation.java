package com.chargingstation.csbe.domain;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ChargingStation {
    private final String statId; // 충전소ID
    private final String statNm; // 충전소명
    private final String addr; // 주소
    private final String addrDetail; // 주소상세
    private final String lat; // 위도
    private final String lng; // 경도
    private final String useTime; // 이용가능시간
    private final String bnm; // 기관명
    private final String busiId; // 운영기관ID
    private final String busiNm; // 운영기관명
    private final String busiCall; // 운영기관연락처
    private final String parkingFree; // 주차료무료여부
    private final String note; // 안내사항
    private final List<Charger> chargers;
}
