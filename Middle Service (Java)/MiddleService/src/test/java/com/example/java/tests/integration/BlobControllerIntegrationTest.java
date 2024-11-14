package com.example.java.tests.integration;


import com.example.java.TestUtils.TestConstants;
import com.example.java.domain.networking.PlotRequest;
import com.example.java.service.BlobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
@ActiveProfiles("test")
public class BlobControllerIntegrationTest {
    /**
     * MockMvc used to mock requests to the controller.
     */
    private MockMvc mockMvc;
    /**
     * Context.
     */
    @Autowired
    private WebApplicationContext context;
    /**
     * Blob service used to make requests.
     */
    @Autowired
    private BlobService blobService;
    /**
     * Mocked Tenant id.
     */
    private static final String TENANT_ID_MOCK = TestConstants.TENANT_ID_MOCK;

    /**
     * Url to send requests to.
     */
    public static final String BASE_URL = "/api/blob/plots";
    /**
     * Mapper used to write objects as json.
     */
    private ObjectMapper ow;


    /**
     * Dependency of the tests used to mock responses from outside services.
     */
    private WireMockServer wireMockServer;

    /**
     * Setup made before each request.
     */
    @BeforeEach
    public void setup() {
        // we are setting the context before each call
        ow = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .port(TestConstants.MOCKED_PYTHON_PORT));
        wireMockServer.start();
        configureFor("localhost", TestConstants.PYTHON_PORT);
    }

    /**
     * Cleanup made after each request.
     */
    @AfterEach
    public void cleanup() {
        wireMockServer.stop();
    }

    /**
     * Test for the successful case.
     *
     * @throws Exception - exception.
     */
    @SuppressWarnings("checkstyle:OperatorWrap")
    @Test
    public void getPlotImagesTestSuccesfull() throws Exception {
        List<String> batteryIdsMock = new ArrayList<>();
        batteryIdsMock.add("test");

        PlotRequest plotRequestMock = new PlotRequest();
        plotRequestMock.setTenantId(TENANT_ID_MOCK);
        plotRequestMock.setBatteryIds(batteryIdsMock);
        plotRequestMock.setPlotType(TestConstants.PLOT_TYPE_MOCK);

        String responseMock = TestConstants.RESPONSE_MESSAGE_MOCK;

        String mockImage = blobService.getImageFromBlobPathsAsBase64(TestConstants.MOCKED_IMAGE_URL);

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject jsonResponseMock = (JSONObject) parser.parse(responseMock);

        wireMockServer.stubFor(WireMock.post("/plot")
                .willReturn(jsonResponse(jsonResponseMock.toJSONString(), TestConstants.GOOD_REQUEST_STATUS)));

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(plotRequestMock)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(mockImage));
    }

    /**
     * Failure case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void getPlotImagesTestFailure() throws Exception {

        //Failure case request.
        String plotRequestMock = "cox";

        String responseMock = TestConstants.RESPONSE_MESSAGE_MOCK;

        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject jsonResponseMock = (JSONObject) parser.parse(responseMock);

        wireMockServer.stubFor(WireMock.post("/plot")
                .willReturn(jsonResponse(jsonResponseMock.toJSONString(), TestConstants.GOOD_REQUEST_STATUS)));

        mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(plotRequestMock)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }


}
