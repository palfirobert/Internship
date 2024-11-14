package com.example.java.tests.unit.services;

import com.example.java.domain.BatteryUser;
import com.example.java.domain.Tenant;
import com.example.java.domain.UserTenants;
import com.example.java.repository.BatteryUserRepository;
import com.example.java.repository.TenantRepository;
import com.example.java.repository.UserTenantsRepository;
import com.example.java.service.UserTenantsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
@ActiveProfiles("test")
public class UserTenantServiceTest {
    /**
     * Object to be tested.
     */
    @InjectMocks
    private UserTenantsService service;
    /**
     * Repository used for getting the mocked data.
     */
    @Mock
    private UserTenantsRepository userTenantsRepository;
    /**
     * Repository used for getting the mocked data.
     */
    @Mock
    private BatteryUserRepository batteryUserRepository;
    /**
     * Repository used for getting the mocked data.
     */
    @Mock
    private TenantRepository tenantsRepository;
    /**
     * List with the tenants used for testing.
     */
    private List<UserTenants> tenantsUserListMock;
    /**
     * List with the tenants used for testing.
     */
    private List<Tenant> tenantsListMock;
    /**
     * Username value for test.
     */
    private static final String EMAIL = "email_test";
    private BatteryUser batteryUserMock;
    /**
     * Create the tenant list.
     */
    @BeforeEach
    public void getTenantsListMock() {
        // 0. CREATE COMPARISON AND RETURNED OBJECTS
        tenantsUserListMock = new ArrayList<>();
        UserTenants tenants = new UserTenants();
        Tenant t1 = new Tenant();
        t1.setTenantId("1");
        tenants.setTenant(t1);
        tenantsUserListMock.add(tenants);

        tenantsListMock = new ArrayList<>();
        tenantsListMock.add(t1);

        // 0. CREATE COMPARISON AND RETURNED OBJECTS
        batteryUserMock = new BatteryUser();
        batteryUserMock.setEmail(EMAIL);
    }

    /**
     * Verify if the tenants are taken as they should.
     */
    @Test
    public void getAllTenantsUserSuccessful() {
        // 1. CREATE COMPARISON AND RETURNED OBJECTS
        Page<UserTenants> pageMock = new PageImpl<>(tenantsUserListMock);
        // 2. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(userTenantsRepository.findAllByUserBatteryId(any(), any())).thenReturn(pageMock);
        // 3. CALL THE SERVICE TO GET THE RESULT
        service.getAllTenants(1, 2, "1");
        // 4. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(userTenantsRepository, times(1)).findAllByUserBatteryId(any(), any());
    }

    /**
     * Verify if the username can't be converted in integer.
     */
    @Test
    public void getAllTenantsUserUsernameIncorrectFormat() {
        // 1. CHECK IF THE EXCEPTION IS THROWN
        assertThrows(NumberFormatException.class,
                () -> service.getAllTenants(1, 2, "dad"));
        // 2. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(userTenantsRepository, times(0)).findAllByUserBatteryId(any(), any());
    }

    @Test
    public void getAllTenantsSuccessful() {
        // 1. CREATE COMPARISON AND RETURNED OBJECTS
        Page<Tenant> pageMock = new PageImpl<>(tenantsListMock);
        Pageable pageable = PageRequest.of(1, 2);
        // 2. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(tenantsRepository.findAll(pageable)).thenReturn(pageMock);
        // 3. CALL THE SERVICE TO GET THE RESULT
        service.getAllTenants(1, 2, "");
        // 4. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(tenantsRepository, times(1)).findAll(pageable);
    }

    @Test void assignTenantsNoUser() {
        when(batteryUserRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.assignTenants(EMAIL, any()));
        verify(userTenantsRepository, times((0))).save(any());
    }

    @Test void assignTenantsSuccessful() {
        when(batteryUserRepository.findByEmail(anyString())).thenReturn(Optional.of(batteryUserMock));
        List<String> tenantsList = new ArrayList<>();
        tenantsList.add("1");
        service.assignTenants(EMAIL, tenantsList);
        verify(userTenantsRepository, times((tenantsList.size()))).save(any());
    }

}
