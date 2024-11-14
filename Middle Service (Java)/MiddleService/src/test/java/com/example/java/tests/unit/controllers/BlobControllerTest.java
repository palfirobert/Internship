package com.example.java.tests.unit.controllers;

import com.example.java.domain.networking.ImageRequest;
import com.example.java.domain.networking.ImageResponse;
import com.example.java.domain.networking.PlotRequest;
import com.example.java.domain.networking.QueueType;
import com.example.java.domain.networking.Request;
import com.example.java.dto.RequestDto;
import com.example.java.servicebus.RequestProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
@ActiveProfiles("test")
public class BlobControllerTest {
    /**
     * MockMvc used to mock requests to the controller.
     */
    @Autowired
    private MockMvc mockMvc;
    /**
     * Base url made to make requests.
     */
    public static final String BASE_URL = "/api/blob/plots";
    /**
     * Context.
     */
    @Autowired
    private WebApplicationContext context;
    /**
     * Mock for RequestProcessingService.
     */
    @MockBean
    private RequestProcessingService processingService;

    /**
     * Setup madbe fore each request.
     */
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Successful case test.
     *
     * @throws Exception - exception.
     */
    @Test
    @DisplayName("Test for getPlotImage in the BlobController")
    public void getPlotImagesTestSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        List<String> imgs = new ArrayList<>();
        imgs.add("test1");
        imgs.add("test2");
        imgs.add("test3");
        Request requestMock = new ImageRequest(new PlotRequest());
        ImageResponse responseMock = new ImageResponse(imgs);
        RequestDto requestDtoMock = new RequestDto(requestMock, QueueType.PLOTTINGQUEUE);
        when(processingService.processRequest(any())).thenReturn(responseMock);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(ow.writeValueAsString(requestMock))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(responseMock.getImages()));
        verify(processingService, times(1)).processRequest(requestDtoMock);
    }

    /**
     * Test made for the failure case.
     *
     * @throws Exception - exception.
     */
    @Test
    public void getPlotImagesTestFailure() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        List<String> imgs = new ArrayList<>();
        imgs.add("test1");
        imgs.add("test2");
        imgs.add("test3");
        String requestMock = "cox";
        ImageResponse responseMock = new ImageResponse(imgs);
        when(processingService.processRequest(any())).thenReturn(responseMock);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(ow.writeValueAsString(requestMock))
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }
}
