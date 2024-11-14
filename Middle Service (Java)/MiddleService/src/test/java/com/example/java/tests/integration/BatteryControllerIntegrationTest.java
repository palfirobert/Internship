package com.example.java.tests.integration;


import com.example.java.TestUtils.TestConstants;
import com.example.java.domain.Battery;
import com.example.java.domain.Tenant;
import com.example.java.repository.BatteryRepository;
import com.example.java.repository.TenantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
@ActiveProfiles("test")
public class BatteryControllerIntegrationTest {
    /**
     * MockMvc used to mock requests to the specified url.
     */
    private MockMvc mockMvc;
    /**
     *
     */
    @Autowired
    private BatteryRepository batteryRepository;
    /**
     *
     */
    @Autowired
    private TenantRepository tenantRepository;
    /**
     *
     */
    private Tenant tenantMock;
    /**
     *
     */
    private Battery batteryMock;
    /**
     *
     */
    @Autowired
    private WebApplicationContext context;
    /**
     *
     */
    private static final String TENANT_ID_MOCK = TestConstants.TENANT_ID_MOCK;
    /**
     *
     */
    private static final String BATTERY_ID_MOCK = TestConstants.BATTERY_ID_MOCK;
    /**
     *
     */
    public static final String BASE_URL = "/api/v1/battery/tenant?tenantId=" + TENANT_ID_MOCK;

    /**
     * Setup made before each test.
     */
    @BeforeEach
    public void setup() {
        // we are setting the context before each call
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        tenantMock = new Tenant(TENANT_ID_MOCK);
        batteryMock = new Battery(BATTERY_ID_MOCK, TestConstants.BATTERY_TYPE_MOCK, tenantMock);
        tenantRepository.save(tenantMock);
        batteryRepository.save(batteryMock);
        System.out.println(batteryRepository.findAll());
    }

    /**
     * Cleanup made after each request.
     */
    @AfterEach
    public void cleanup() {
        batteryRepository.delete(batteryMock);
        tenantRepository.delete(tenantMock);
    }

    /**
     * Method used to test the succesfull case for a Get request on the controller.
     *
     * @throws Exception - thrown.
     */
    @Test
    public void getTenantBatteriesTestSuccesfull() throws Exception {

        List<String> batteries = new ArrayList<>();
        batteries.add(batteryMock.getBatteryId());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0]").value(batteries.get(0)));

    }
    /**
     * Method used to test the failure case for a Get request on the controller.
     *
     * @throws Exception - thrown.
     */
    @Test
    public void getTenantBatteriesTestFailure() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "&size=cox")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }
}
