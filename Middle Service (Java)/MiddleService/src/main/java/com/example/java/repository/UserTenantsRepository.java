package com.example.java.repository;

import com.example.java.domain.UserTenants;
import com.example.java.domain.UserTenantsPrimaryKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTenantsRepository extends JpaRepository<UserTenants, UserTenantsPrimaryKey> {

    /**
     *
     * @param pageable Pageable object
     * @param userId Id of the user
     * @return Tenants
     */
    Page<UserTenants> findAllByUserBatteryId(Pageable pageable, Integer userId);

}
