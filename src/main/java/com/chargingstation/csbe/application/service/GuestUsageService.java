package com.chargingstation.csbe.application.service;

import com.chargingstation.csbe.application.port.out.GuestUsageRepository;
import com.chargingstation.csbe.domain.GuestUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class GuestUsageService {

    private final GuestUsageRepository repository;

    @Transactional
    public void checkAndIncrement(String guestId) {
        if (guestId == null || guestId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guest ID is required");
        }

        GuestUsage usage = repository.findById(guestId)
                .orElseGet(() -> GuestUsage.createNew(guestId));

        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        if (usage.getWindowStartedAt() != null &&
                usage.getWindowStartedAt().plusMinutes(1).isAfter(now) &&
                usage.getSearchCount() >= 5) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Guest search limit reached (5 per minute)");
        }

        usage.incrementCount();
        repository.save(usage);
    }
}
