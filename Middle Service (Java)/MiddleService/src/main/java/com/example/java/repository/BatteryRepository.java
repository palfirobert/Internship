package com.example.java.repository;

import com.example.java.domain.Battery;
import com.example.java.domain.Tenant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, String> {
    /**
     * @param tenant   The tenant that has the batteries
     * @param pageable Object for pagination
     * @return List of batteries
     */
    List<Battery> findAllByTenant(Tenant tenant, Pageable pageable);
}
