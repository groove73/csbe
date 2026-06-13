package com.chargingstation.csbe.application.service;

import com.chargingstation.csbe.application.port.out.GuestUsageRepository;
import com.chargingstation.csbe.domain.GuestUsage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class GuestUsageService {

    private final GuestUsageRepository repository;

    @Inject
    public GuestUsageService(GuestUsageRepository repository) {
        this.repository = repository;
    }


    @Transactional
    public void checkAndIncrement(String guestId) {
        if (guestId == null || guestId.isBlank()) {
            throw new WebApplicationException("Guest ID is required", Response.Status.BAD_REQUEST);
        }

        GuestUsage usage = repository.findByIdOptional(guestId)
                .orElseGet(() -> {
                    GuestUsage nu = GuestUsage.createNew(guestId);
                    repository.persist(nu);
                    return nu;
                });

        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        if (usage.getWindowStartedAt() != null &&
                usage.getWindowStartedAt().plusMinutes(1).isAfter(now) &&
                usage.getSearchCount() >= 5) {
            throw new WebApplicationException("Guest search limit reached (5 per minute)", 429);
        }

        usage.incrementCount();
        repository.persist(usage);
    }
}

