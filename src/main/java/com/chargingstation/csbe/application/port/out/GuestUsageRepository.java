package com.chargingstation.csbe.application.port.out;

import com.chargingstation.csbe.domain.GuestUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestUsageRepository extends JpaRepository<GuestUsage, String> {
}
