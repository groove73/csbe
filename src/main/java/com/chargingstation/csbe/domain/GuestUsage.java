package com.chargingstation.csbe.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "guest_usage")
public class GuestUsage {

    @Id
    private String id;

    private int searchCount;

    private OffsetDateTime lastSearchAt;

    private OffsetDateTime windowStartedAt;

    protected GuestUsage() {
    }

    public GuestUsage(String id, int searchCount, OffsetDateTime lastSearchAt, OffsetDateTime windowStartedAt) {
        this.id = id;
        this.searchCount = searchCount;
        this.lastSearchAt = lastSearchAt;
        this.windowStartedAt = windowStartedAt;
    }

    public String getId() {
        return id;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public OffsetDateTime getLastSearchAt() {
        return lastSearchAt;
    }

    public OffsetDateTime getWindowStartedAt() {
        return windowStartedAt;
    }

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


