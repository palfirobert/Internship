package com.example.java.tests.unit.controllers;

import com.example.java.controller.JwtAuthenticationController;
import com.example.java.domain.BatteryUser;
import com.example.java.domain.BatteryUserPrincipal;
import com.example.java.domain.networking.JwtRequest;
import com.example.java.domain.networking.RegisterRequest;
import com.example.java.domain.networking.ResetPasswordEmailRequest;
import com.example.java.domain.networking.ResetPasswordRequest;
import com.example.java.domain.networking.TokenRequest;
import com.example.java.service.UserDetailServiceDB;
import com.example.java.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.InvalidKeyException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.example.java.TestUtils.TestConstants.TOKEN;
import static com.example.java.TestUtils.TestConstants.EMAIL;
import static com.example.java.TestUtils.TestConstants.EMAIL_BOSCH;
import static com.example.java.TestUtils.TestConstants.NAME;
import static com.example.java.TestUtils.TestConstants.NEW_PASSWORD;


@ExtendWith(SpringExtension.class)
@WebMvcTest(JwtAuthenticationController.class)
@Profile("test")
@ActiveProfiles("test")
public class JwtAuthenticationControllerTest {
    /**
     * MockMvc used to mock requests to the controller.
     */
    @Autowired
    private MockMvc mockMvc;
    /**
     * Base url for requests.
     */
    public static final String BASE_URL = "/api/v1/authentication";
    /**
     * Context.
     */
    @Autowired
    private WebApplicationContext context;
    /**
     * Mock for JwtTokenUtils.
     */
    @MockBean
    private JwtTokenUtils jwtTokenUtilsMock;
    /**
     * Mocked UserDetailServiceDb.
     */
    @MockBean
    private UserDetailServiceDB userDetailServiceDBMock;
    /**
     * The controller that we will test and in which we inject mocks.
     */
    @InjectMocks
    private JwtAuthenticationController jwtAuthenticationController;

    /**
     * Setup made before each test.
     */
    @BeforeEach
    public void setup() {
        // we are setting the context before each call
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Successfully test case for login.
     *
     * @throws Exception - exception.
     */
    @Test
    public void loginTestSuccesful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        BatteryUser batteryUser = new BatteryUser(1, "test", "test", "test", "test", true);
        String tokenMok = "test";
        JwtRequest jwtRequestMok = new JwtRequest("test", "test");
        BatteryUserPrincipal batteryUserPrincipal = new BatteryUserPrincipal(batteryUser);

        when(userDetailServiceDBMock.loadUserByUsernameAndPassword(anyString(), anyString()))
                .thenReturn(batteryUserPrincipal);
        when(userDetailServiceDBMock.loadUserByUsername(anyString())).thenReturn(batteryUserPrincipal);
        when(jwtTokenUtilsMock.generateToken(any())).thenReturn(tokenMok);

        mockMvc.perform(post(BASE_URL + "/login").contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(jwtRequestMok)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(tokenMok));
        verify(jwtTokenUtilsMock, times(1))
                .generateToken(batteryUserPrincipal);
        verify(userDetailServiceDBMock, times(1))
                .loadUserByUsernameAndPassword("test", "test");
    }

    /**
     * Failure test case for login.
     */
    @Test
    public void loginTestFailure() {
        BatteryUser batteryUser = new BatteryUser(1, "test", "test", "test", "test", true);
        String tokenMok = "test";
        BatteryUserPrincipal batteryUserPrincipal = new BatteryUserPrincipal(batteryUser);

        when(userDetailServiceDBMock.loadUserByUsernameAndPassword(anyString(), anyString()))
                .thenThrow(new UsernameNotFoundException("No user with name test"));
        when(userDetailServiceDBMock.loadUserByUsername(anyString())).thenReturn(batteryUserPrincipal);
        when(jwtTokenUtilsMock.generateToken(any())).thenReturn(tokenMok);
        ReflectionTestUtils.setField(jwtAuthenticationController, "userDetailServiceDB", userDetailServiceDBMock);
        ReflectionTestUtils.setField(jwtAuthenticationController, "jwtTokenUtils", jwtTokenUtilsMock);

        verify(jwtTokenUtilsMock, times(0)).generateToken(batteryUserPrincipal);
        verify(userDetailServiceDBMock, times(0))
                .loadUserByUsernameAndPassword("test", "test");
    }

    @Test public void logoutTestSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        TokenRequest tokenRequestMock = new TokenRequest(TOKEN);
        mockMvc.perform(post(BASE_URL + "/logout").contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(tokenRequestMock)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        verify(userDetailServiceDBMock, times(1))
                .logout(tokenRequestMock);
    }

    @Test public void registerTestSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        RegisterRequest registerRequest = new RegisterRequest(EMAIL_BOSCH, NEW_PASSWORD, NAME, NAME);

        mockMvc.perform(post(BASE_URL + "/register").contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(registerRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        verify(userDetailServiceDBMock, times(1))
                .register(registerRequest);

    }

    @Test
    public void registerTestFailureEmailInUse() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(EMAIL_BOSCH, NEW_PASSWORD, NAME, NAME);

        ReflectionTestUtils.setField(jwtAuthenticationController, "userDetailServiceDB", userDetailServiceDBMock);
        ReflectionTestUtils.setField(jwtAuthenticationController, "jwtTokenUtils", jwtTokenUtilsMock);

        // Mock the behavior of userDetailServiceDB to throw an exception
        doThrow(new AlreadyUsedException(EMAIL_BOSCH)).when(userDetailServiceDBMock).register(registerRequest);
        // Perform the test
        ResponseEntity<?> response = jwtAuthenticationController.register(registerRequest);

        // Verify the expected behavior
        verify(userDetailServiceDBMock).register(registerRequest);

        // Assert that the response is as expected for a failure scenario
        assert response.getStatusCode() == HttpStatus.CONFLICT;
    }

    @Test
    public void registerTestFailure() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(EMAIL, NEW_PASSWORD, NAME, NAME);

        ReflectionTestUtils.setField(jwtAuthenticationController, "userDetailServiceDB", userDetailServiceDBMock);
        ReflectionTestUtils.setField(jwtAuthenticationController, "jwtTokenUtils", jwtTokenUtilsMock);

        // Mock the behavior of userDetailServiceDB to throw an exception
        doThrow(new InvalidKeyException(EMAIL)).when(userDetailServiceDBMock).register(registerRequest);
        // Perform the test
        ResponseEntity<?> response = jwtAuthenticationController.register(registerRequest);

        // Verify the expected behavior
        verify(userDetailServiceDBMock).register(registerRequest);

        // Assert that the response is as expected for a failure scenario
        assert response.getStatusCode() == HttpStatus.ACCEPTED;
    }

    @Test public void verifyAccountSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        TokenRequest tokenRequest = new TokenRequest(TOKEN);

        mockMvc.perform(post(BASE_URL + "/verify").contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(tokenRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        verify(userDetailServiceDBMock, times(1))
                .verifyUser(tokenRequest);
    }



    @Test
    public void verifyAccountTestFailure() {
        TokenRequest tokenRequest = new TokenRequest(TOKEN);

        ReflectionTestUtils.setField(jwtAuthenticationController, "userDetailServiceDB", userDetailServiceDBMock);
        ReflectionTestUtils.setField(jwtAuthenticationController, "jwtTokenUtils", jwtTokenUtilsMock);

        // Mock the behavior of userDetailServiceDB to throw an exception
        doThrow(new CredentialsExpiredException("Token has expired")).when(userDetailServiceDBMock).verifyUser(tokenRequest);
        // Perform the test
        ResponseEntity<?> response = jwtAuthenticationController.verifyAccount(tokenRequest);

        // Verify the expected behavior
        verify(userDetailServiceDBMock).verifyUser(tokenRequest);

        // Assert that the response is as expected for a failure scenario
        assert response.getStatusCode() == HttpStatus.ACCEPTED;
        assert response.getBody() == "Token has expired";
    }

    @Test public void requestResetPasswordSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ResetPasswordEmailRequest resetPasswordEmailRequest = new ResetPasswordEmailRequest(EMAIL_BOSCH);

        mockMvc.perform(post(BASE_URL + "/request-reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(resetPasswordEmailRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        verify(userDetailServiceDBMock, times(1))
                .requestResetPassword(resetPasswordEmailRequest);
    }

    @Test
    public void requestResetPasswordTestFailureNoEmail() throws Exception {
        ResetPasswordEmailRequest resetPasswordEmailRequest = new ResetPasswordEmailRequest(EMAIL_BOSCH);

        ReflectionTestUtils.setField(jwtAuthenticationController, "userDetailServiceDB", userDetailServiceDBMock);
        ReflectionTestUtils.setField(jwtAuthenticationController, "jwtTokenUtils", jwtTokenUtilsMock);

        // Mock the behavior of userDetailServiceDB to throw an exception
        doThrow(new UsernameNotFoundException(EMAIL_BOSCH)).when(userDetailServiceDBMock).requestResetPassword(resetPasswordEmailRequest);
        // Perform the test
        ResponseEntity<?> response = jwtAuthenticationController.requestResetPassword(resetPasswordEmailRequest);

        // Verify the expected behavior
        verify(userDetailServiceDBMock).requestResetPassword(resetPasswordEmailRequest);

        // Assert that the response is as expected for a failure scenario
        assert response.getStatusCode() == HttpStatus.CONFLICT;
    }

    @Test
    public void requestResetPasswordTestFailure() throws Exception {
        ResetPasswordEmailRequest resetPasswordEmailRequest = new ResetPasswordEmailRequest(EMAIL_BOSCH);

        ReflectionTestUtils.setField(jwtAuthenticationController, "userDetailServiceDB", userDetailServiceDBMock);
        ReflectionTestUtils.setField(jwtAuthenticationController, "jwtTokenUtils", jwtTokenUtilsMock);

        // Mock the behavior of userDetailServiceDB to throw an exception
        doThrow(new Exception()).when(userDetailServiceDBMock).requestResetPassword(resetPasswordEmailRequest);
        // Perform the test
        ResponseEntity<?> response = jwtAuthenticationController.requestResetPassword(resetPasswordEmailRequest);

        // Verify the expected behavior
        verify(userDetailServiceDBMock).requestResetPassword(resetPasswordEmailRequest);


        // Assert that the response is as expected for a failure scenario
        assert response.getStatusCode() == HttpStatus.ACCEPTED;
    }


    @Test public void resetPasswordSuccessful() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(NEW_PASSWORD, TOKEN);

        mockMvc.perform(post(BASE_URL + "/reset-password").contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(resetPasswordRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        verify(userDetailServiceDBMock, times(1))
                .resetPassword(resetPasswordRequest);
    }

    @Test
    public void resetPasswordTestFailure() throws Exception {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(NEW_PASSWORD, TOKEN);


        ReflectionTestUtils.setField(jwtAuthenticationController, "userDetailServiceDB", userDetailServiceDBMock);
        ReflectionTestUtils.setField(jwtAuthenticationController, "jwtTokenUtils", jwtTokenUtilsMock);

        // Mock the behavior of userDetailServiceDB to throw an exception
        doThrow(new CredentialsExpiredException(TOKEN)).when(userDetailServiceDBMock).resetPassword(resetPasswordRequest);
        // Perform the test
        ResponseEntity<?> response = jwtAuthenticationController.resetPassword(resetPasswordRequest);

        // Verify the expected behavior
        verify(userDetailServiceDBMock).resetPassword(resetPasswordRequest);


        // Assert that the response is as expected for a failure scenario
        assert response.getStatusCode() == HttpStatus.ACCEPTED;
    }

}
