package com.example.java.tests.unit.controllers;

import com.example.java.TestUtils.TestConstants;
import com.example.java.controller.UserTenantsController;
import com.example.java.domain.networking.AssignTenantsRequest;
import com.example.java.domain.networking.QueueType;
import com.example.java.domain.networking.Request;
import com.example.java.domain.networking.Response;
import com.example.java.domain.networking.TenantRequest;
import com.example.java.domain.networking.TenantResponse;
import com.example.java.dto.RequestDto;
import com.example.java.dto.TenantDTO;
import com.example.java.servicebus.RequestProcessingService;
import com.example.java.utils.JwtTokenUtils;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.example.java.TestUtils.TestConstants.EMAIL;
import static com.example.java.TestUtils.TestConstants.EMAIL_BOSCH;
import static com.example.java.TestUtils.TestConstants.TOKEN;
import static com.example.java.TestUtils.TestConstants.TOKEN_INDEX_FOR_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
@ActiveProfiles("test")
public class UserTenantsControllerTest {
    /**
     * MockMvc used to send requests.
     */
    @Autowired
    private MockMvc mockMvc;
    /**
     * Context.
     */
    @Autowired
    private WebApplicationContext context;
    /**
     * Mocked RequestProcessingService.
     */
    @MockBean
    private RequestProcessingService processingService;
    /**
     * Mocked token utils.
     */
    @MockBean
    private JwtTokenUtils tokenUtils;
    /**
     * Controller to be tested.
     */
    @Autowired
    private UserTenantsController userTenantsController;
    /**
     * Url used for requests.
     */
    private static final String BASE_URL = "http://localhost:8080/api/v1/tenants";
    private static final String URL_PAGE = "?page=-1&size=-20v";

    /**
     * Setup made before each request.
     */
    @BeforeEach
    public void setup() {
        // we are setting the context before each call
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Succesful test case.
     *
     * @throws Exception - exception.
     */
    @Test
    @DisplayName("Get tenants test")
    public void getAllTenantsOfSpecificUserTestSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        List<TenantDTO> listaMock = new ArrayList<>();
        listaMock.add(new TenantDTO("test"));
        Page<TenantDTO> tenantsMock = new PageImpl<>(listaMock);

        Request requestMock = new TenantRequest(1, TestConstants.GOOD_PAGE_SIZE, "test");
        TenantResponse responseMock = new TenantResponse(tenantsMock);
        RequestDto requestDtoMock = new RequestDto(requestMock, QueueType.TENANTQUEUE);

        String usernameFromTokenMock = "test";
        String userIdFromTokenMock = "test";
        String tokenMock = "for some reason i can t mock what the string to do so i used "
                +
                "this very long string in order to test tha app lol";

        when(processingService.processRequest(any())).thenReturn(responseMock);
        when(tokenUtils.getUsernameFromToken(anyString())).thenReturn(usernameFromTokenMock);
        when(tokenUtils.getUserIdFromToken(anyString())).thenReturn(userIdFromTokenMock);
        ReflectionTestUtils.setField(userTenantsController, "processingService", processingService);
        ReflectionTestUtils.setField(userTenantsController, "jwtTokenUtils", tokenUtils);

        ResponseEntity<Page<TenantDTO>> actualResponse = userTenantsController
                .getAllTenantsOfSpecificUser(1, TestConstants.GOOD_PAGE_SIZE, "for som" + tokenMock);

        assertThat(actualResponse.getBody()).isEqualTo(responseMock.getTenants());

        verify(processingService, times(1)).processRequest(requestDtoMock);
        verify(tokenUtils, times(1)).getUsernameFromToken(tokenMock);
    }

    /**
     * Failure test case.
     *
     * @throws Exception - exception.
     */
    @Test
    public void getAllTenantsOfSpecificUserTestFailure() throws Exception {
        List<TenantDTO> listaMock = new ArrayList<>();
        listaMock.add(new TenantDTO("test"));
        Page<TenantDTO> tenantsMock = new PageImpl<>(listaMock);

        Request requestMock = new TenantRequest(1, TestConstants.GOOD_PAGE_SIZE, "test");
        TenantResponse responseMock = new TenantResponse(tenantsMock);
        RequestDto requestDtoMock = new RequestDto(requestMock, QueueType.TENANTQUEUE);

        String usernameFromTokenMock = "test";
        String userIdFromTokenMock = "test";
        String tokenMock = "for some reason i can t mock what the string to do so i used "
                +
                "this very long string in order to test tha app lol";

        when(processingService.processRequest(any())).thenReturn(responseMock);
        when(tokenUtils.getUsernameFromToken(anyString())).thenReturn(usernameFromTokenMock);
        when(tokenUtils.getUserIdFromToken(anyString())).thenReturn(userIdFromTokenMock);
        ReflectionTestUtils.setField(userTenantsController, "processingService", processingService);
        ReflectionTestUtils.setField(userTenantsController, "jwtTokenUtils", tokenUtils);

        mockMvc.perform(get(BASE_URL + URL_PAGE)
                .header("Authorization", "Bearer " + tokenMock))
                .andExpect(status().isBadRequest());

        verify(processingService, times(0)).processRequest(requestDtoMock);
        verify(tokenUtils, times(0)).getUsernameFromToken(tokenMock);
    }

    /**
     * Succesful test case.
     *
     * @throws Exception - exception.
     */
    @Test
    @DisplayName("Get tenants test")
    public void getAllTenantsTestSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        List<TenantDTO> listaMock = new ArrayList<>();
        listaMock.add(new TenantDTO("test"));
        Page<TenantDTO> tenantsMock = new PageImpl<>(listaMock);

        Request requestMock = new TenantRequest(1, TestConstants.GOOD_PAGE_SIZE, "");
        TenantResponse responseMock = new TenantResponse(tenantsMock);
        RequestDto requestDtoMock = new RequestDto(requestMock, QueueType.TENANTQUEUE);

        String usernameFromTokenMock = "";
        String userIdFromTokenMock = "test";
        String tokenMock = "for some reason i can t mock what the string to do so i used "
                +
                "this very long string in order to test tha app lol";

        when(processingService.processRequest(any())).thenReturn(responseMock);
        when(tokenUtils.getUsernameFromToken(anyString())).thenReturn(usernameFromTokenMock);
        when(tokenUtils.getUserIdFromToken(anyString())).thenReturn(userIdFromTokenMock);
        ReflectionTestUtils.setField(userTenantsController, "processingService", processingService);
        ReflectionTestUtils.setField(userTenantsController, "jwtTokenUtils", tokenUtils);

        ResponseEntity<Page<TenantDTO>> actualResponse = userTenantsController
                .getAllTenants(1, TestConstants.GOOD_PAGE_SIZE, "for som" + tokenMock);

        assertThat(actualResponse.getBody()).isEqualTo(responseMock.getTenants());

        verify(processingService, times(1)).processRequest(requestDtoMock);
        verify(tokenUtils, times(1)).getUsernameFromToken(tokenMock);
    }

    /**
     * Failure test case.
     *
     * @throws Exception - exception.
     */
    @Test
    public void getAllTenantsTestFailure() throws Exception {
        List<TenantDTO> listMock = new ArrayList<>();
        listMock.add(new TenantDTO("test"));
        Page<TenantDTO> tenantsMock = new PageImpl<>(listMock);

        Request requestMock = new TenantRequest(1, TestConstants.GOOD_PAGE_SIZE, "");
        TenantResponse responseMock = new TenantResponse(tenantsMock);
        RequestDto requestDtoMock = new RequestDto(requestMock, QueueType.TENANTQUEUE);

        String usernameFromTokenMock = "";
        String userIdFromTokenMock = "test";
        String tokenMock = "for some reason i can t mock what the string to do so i used "
                +
                "this very long string in order to test tha app lol";

        when(processingService.processRequest(any())).thenReturn(responseMock);
        when(tokenUtils.getUsernameFromToken(anyString())).thenReturn(usernameFromTokenMock);
        when(tokenUtils.getUserIdFromToken(anyString())).thenReturn(userIdFromTokenMock);
        ReflectionTestUtils.setField(userTenantsController, "processingService", processingService);
        ReflectionTestUtils.setField(userTenantsController, "jwtTokenUtils", tokenUtils);

        mockMvc.perform(get(BASE_URL + "/all" + URL_PAGE)
                        .header("Authorization", "Bearer " + tokenMock))
                .andExpect(status().isBadRequest());

        verify(processingService, times(0)).processRequest(requestDtoMock);
        verify(tokenUtils, times(0)).getUsernameFromToken(tokenMock);
    }

    /**
     * Succesful test case.
     *
     * @throws Exception - exception.
     */
    @Test
    @DisplayName("Get tenants test")
    public void assignTenantsTestSuccessful() throws Exception {
        List<String> listMock = new ArrayList<>();
        listMock.add("test");

        Request requestMock = new AssignTenantsRequest(listMock, EMAIL_BOSCH);
        Response responseMock = new Response(requestMock.getRequestId());
        RequestDto requestDtoMock = new RequestDto(requestMock, QueueType.TENANTQUEUE);

        String userIdFromTokenMock = "test";

        when(processingService.processRequest(any())).thenReturn(responseMock);
        when(tokenUtils.getUsernameFromToken(anyString())).thenReturn(EMAIL_BOSCH);
        when(tokenUtils.getUserIdFromToken(anyString())).thenReturn(userIdFromTokenMock);
        ReflectionTestUtils.setField(userTenantsController, "processingService", processingService);
        ReflectionTestUtils.setField(userTenantsController, "jwtTokenUtils", tokenUtils);

        ResponseEntity<?> actualResponse = userTenantsController
                .assignTenants(listMock, TOKEN);

        assert actualResponse.getStatusCode() == HttpStatus.OK;

        verify(processingService, times(1)).processRequest(requestDtoMock);
        verify(tokenUtils, times(1)).getUsernameFromToken(TOKEN.substring(TOKEN_INDEX_FOR_ID));
    }

    /**
     * Failure test case.
     */
    @Test
    public void assignTenantsTestFailure() {
        List<String> listMock = new ArrayList<>();
        listMock.add("test");

        Request requestMock = new AssignTenantsRequest(listMock, EMAIL_BOSCH);
        RequestDto requestDtoMock = new RequestDto(requestMock, QueueType.TENANTQUEUE);
        String userIdFromTokenMock = "test";

        when(processingService.processRequest(any())).thenThrow(new UsernameNotFoundException(EMAIL));
        when(tokenUtils.getUsernameFromToken(anyString())).thenReturn(EMAIL);
        when(tokenUtils.getUserIdFromToken(anyString())).thenReturn(userIdFromTokenMock);

        ReflectionTestUtils.setField(userTenantsController, "processingService", processingService);
        ReflectionTestUtils.setField(userTenantsController, "jwtTokenUtils", tokenUtils);

        verify(processingService, times(0)).processRequest(requestDtoMock);
        verify(tokenUtils, times(0)).getUsernameFromToken(TOKEN);
    }
}
