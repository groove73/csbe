package com.chargingstation.csbe.application.port.out;

import com.chargingstation.csbe.domain.GuestUsage;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuestUsageRepository implements PanacheRepositoryBase<GuestUsage, String> {
}


