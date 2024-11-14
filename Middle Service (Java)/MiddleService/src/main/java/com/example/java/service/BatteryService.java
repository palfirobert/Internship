package com.example.java.service;

import com.example.java.domain.Battery;
import com.example.java.domain.Tenant;
import com.example.java.repository.BatteryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatteryService {
    /**
     * Repository to access battery data.
     */
    private final BatteryRepository batteryRepository;
    /**
     *
     * @param batteryRepositoryInstance Injecting the repository for this class
     */
    @Autowired
    public BatteryService(final BatteryRepository batteryRepositoryInstance) {
        this.batteryRepository = batteryRepositoryInstance;
    }

    /**
     *
     * @param tenantId Tenant id
     * @param pageNumber Number of the page we want to find data
     * @param pageSize Number of elements on the page
     * @return Paginated list of batteries
     */
    public final Page<String> getTenantBatteries(final String tenantId, final int pageNumber, final int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<String> batteries = new ArrayList<>();

        for (Battery battery : batteryRepository.findAllByTenant(new Tenant(tenantId), pageable)) {
            batteries.add(battery.getBatteryId());
        }

        return new PageImpl<>(batteries, pageable, batteries.size());
    }

}
