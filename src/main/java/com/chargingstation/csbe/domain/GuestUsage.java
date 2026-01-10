package com.chargingstation.csbe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "guest_usage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuestUsage {

    @Id
    private String id;

    private int searchCount;

    private OffsetDateTime lastSearchAt;

    private OffsetDateTime windowStartedAt;

    public void incrementCount() {
        OffsetDateTime now = OffsetDateTime.now();
        if (windowStartedAt == null || windowStartedAt.plusMinutes(1).isBefore(now)) {
            this.windowStartedAt = now;
            this.searchCount = 1;
        } else {
            this.searchCount++;
        }
        this.lastSearchAt = now;
    }

    public static GuestUsage createNew(String guestId) {
        OffsetDateTime now = OffsetDateTime.now();
        return new GuestUsage(guestId, 0, now, now);
    }
}
