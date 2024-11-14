package com.example.java.service;

import com.example.java.domain.BatteryUser;
import com.example.java.domain.Tenant;
import com.example.java.domain.UserTenants;
import com.example.java.domain.UserTenantsPrimaryKey;
import com.example.java.dto.TenantDTO;
import com.example.java.repository.BatteryUserRepository;
import com.example.java.repository.TenantRepository;
import com.example.java.repository.UserTenantsRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserTenantsService {
    /**
     * Repository used to get data.
     */
    private final UserTenantsRepository userTenantsRepository;

    private final TenantRepository tenantRepository;

    private final BatteryUserRepository batteryUserRepository;

    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     *  @param userTenantsRepositoryInstance Parameter injected.
     * @param tenantRepository
     * @param batteryUserRepository
     */
    @Autowired
    public UserTenantsService(final UserTenantsRepository userTenantsRepositoryInstance, TenantRepository tenantRepository, BatteryUserRepository batteryUserRepository) {
        this.userTenantsRepository = userTenantsRepositoryInstance;
        this.tenantRepository = tenantRepository;
        this.batteryUserRepository = batteryUserRepository;
    }

    /**
     *
     * @param pageNumber Number of the page (default 0)
     * @param pageSize Number of elements on the page (default 20)
     * @param username Username of the tenant
     * @return Tenants
     */
    public final Page<TenantDTO> getAllTenants(final int pageNumber, final int pageSize, final String username) {
        if (username.isEmpty()) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            List<TenantDTO> tenants = new ArrayList<>();
            for (Tenant tenant: tenantRepository.findAll(pageable)) {
                tenants.add(new TenantDTO(tenant.getTenantId()));
            }
            return new PageImpl<>(tenants, pageable, tenants.size());
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<TenantDTO> tenants = new ArrayList<>();
        for (UserTenants tenant: userTenantsRepository.findAllByUserBatteryId(pageable, Integer.valueOf(username))) {
            tenants.add(new TenantDTO(tenant.getTenant().getTenantId()));
        }
        return new PageImpl<>(tenants, pageable, tenants.size());
    }

    /**
     * Assign tenants to a specific user.
     * @param email - Email od the user.
     * @param tenants - List of tenants to be assigned.
     */
    public final void assignTenants(final String email, final List<String> tenants) {
        Optional<BatteryUser> user = batteryUserRepository.findByEmail(email);

        if (user.isEmpty()) {
            logger.info("No user with email " + email);
            throw new UsernameNotFoundException(email);
        }
        for (String tenantDTO : tenants) {
            Tenant tenant = new Tenant(tenantDTO);
            UserTenantsPrimaryKey primaryKey = new UserTenantsPrimaryKey(user.get().getId(), tenant.getTenantId());
            UserTenants userTenant = new UserTenants(primaryKey, user.get(), tenant);
            try {
                userTenantsRepository.save(userTenant);
                logger.info("New assignment " + userTenant);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
