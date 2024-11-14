package com.example.java.tests.unit.services;

import com.example.java.domain.Battery;
import com.example.java.domain.Tenant;
import com.example.java.repository.BatteryRepository;
import com.example.java.service.BatteryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
@ActiveProfiles("test")
public class BatteryServiceTest {
    /**
     * Mocking the service that we want to test.
     */
    @InjectMocks
    private BatteryService batteryService;
    /**
     * Mocking the repository dependency.
     */
    @Mock
    private BatteryRepository batteryRepository;

    /**
     * Value for tenant id for testing.
     */
    private static final String TENANT_ID = "1aa9d5e8-a182-414b-be97-1791fd89fc27";

    /**
     * Value for page number (chose the default value).
     */
    private static final int PAGE_NUMBER = 0;

    /**
     * Value for number of elements on page (chose default values).
     */
    private static final int PAGE_SIZE = 20;

    /**
     * Verifying if the method works as it should and has 100% coverage.
     */
    @Test
    public void getTenantBatteriesSuccessful() {
        // 1. CREATE COMPARISON AND RETURNED OBJECTS
        Battery testBattery = new Battery("1L", "batteryType", new Tenant(TENANT_ID));
        var list = new ArrayList<Battery>();
        list.add(testBattery);
        // 2. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(batteryRepository.findAllByTenant(any(), any())).thenReturn(list);
        // 3. CALL THE SERVICE TO GET THE RESULT
        batteryService.getTenantBatteries(TENANT_ID, PAGE_NUMBER, PAGE_SIZE);
        // 4. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryRepository, times(1)).findAllByTenant(any(), any());

    }


}
