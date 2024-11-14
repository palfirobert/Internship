package com.example.java.tests.unit.controllers;

import com.example.java.TestUtils.TestConstants;
import com.example.java.domain.networking.BatteryRequest;
import com.example.java.domain.networking.BatteryResponse;
import com.example.java.domain.networking.QueueType;
import com.example.java.dto.RequestDto;
import com.example.java.servicebus.RequestProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
@ActiveProfiles("test")
public class BatteryControllerTest {

    /**
     * Mock mvc used to mock requests to specified url.
     */
    private MockMvc mockMvc;
    /**
     * Mock for request processing service.
     */
    @MockBean
    private RequestProcessingService requestProcessingService;

    /**
     * Context.
     */
    @Autowired
    private WebApplicationContext context;
    /**
     * Mocked tenant id.
     */
    private static final String TENANT_ID_MOCK = TestConstants.TENANT_ID_MOCK;
    /**
     * Base url for the requests.
     */
    public static final String BASE_URL = "/api/v1/battery/tenant";
    /**
     * Mapper used to convert objects to jsons.
     */
    private ObjectMapper mapper;
    /**
     * Good request page size.
     */
    private int okSizeMock = TestConstants.GOOD_PAGE_SIZE;
    /**
     * Bad request page size.
     */
    private int failureSizeMock = TestConstants.BAD_PAGE_SIZE;

    /**
     * Setup made before each request.
     */
    @BeforeEach
    public void setup() {
        // we are setting the context before each call
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mapper = new ObjectMapper();
    }

    /**
     * Test for successful getBatteries Case.
     *
     * @throws Exception - thrown.
     */
    @Test
    public void getTenantBatteriesSuccessful() throws Exception {
        List<String> batteries = new ArrayList<>();
        batteries.add("b1");
        batteries.add("b2");
        Page<String> batteriesMock = new PageImpl<>(batteries);
        when(requestProcessingService.processRequest(new RequestDto(
                new BatteryRequest(TENANT_ID_MOCK, 0, okSizeMock), QueueType.BATTERYQUEUE))).
                thenReturn(new BatteryResponse(batteriesMock));
        System.out.println(content());
        mockMvc.perform(get(BASE_URL + "?tenantId=" + TENANT_ID_MOCK)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(batteriesMock)))
                .andExpect(jsonPath("$.content[0]").value(batteries.get(0)));
        verify(requestProcessingService, times(1)).processRequest(
                new RequestDto(new BatteryRequest(TENANT_ID_MOCK, 0, okSizeMock), QueueType.BATTERYQUEUE));


    }

    /**
     * Fail case for get batteryes request.
     *
     * @throws Exception
     */
    @Test
    public void getTenantBatteriesFail() throws Exception {
        List<String> batteries = new ArrayList<>();
        batteries.add("b1");
        batteries.add("b2");
        Page<String> batteriesMock = new PageImpl<>(batteries);
        when(requestProcessingService.processRequest(
                new RequestDto(new BatteryRequest(TENANT_ID_MOCK, 0, failureSizeMock), QueueType.BATTERYQUEUE)))
                .thenReturn(new BatteryResponse(batteriesMock));
        String tokenMock = "for some reason i can t mock what the string to do so i u"
                +
                "sed this very long string in order to test tha app lol";

        mockMvc.perform(get(BASE_URL + "?size=-20")
                .header("Authorization", "Bearer " + tokenMock))
                .andExpect(status().isBadRequest());
        verify(requestProcessingService, times(0)).processRequest(
                new RequestDto(new BatteryRequest(TENANT_ID_MOCK, 0, failureSizeMock), QueueType.BATTERYQUEUE));
    }
}
