package com.example.java.tests.integration;

import com.example.java.controller.JwtAuthenticationController;
import com.example.java.domain.BatteryUser;
import com.example.java.domain.BatteryUserPrincipal;
import com.example.java.domain.networking.JwtRequest;
import com.example.java.domain.networking.RegisterRequest;
import com.example.java.domain.networking.ResetPasswordEmailRequest;
import com.example.java.domain.networking.ResetPasswordRequest;
import com.example.java.domain.networking.TokenRequest;
import com.example.java.repository.BatteryUserRepository;
import com.example.java.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.java.TestUtils.TestConstants.EMAIL_BOSCH;
import static com.example.java.TestUtils.TestConstants.NAME;
import static com.example.java.TestUtils.TestConstants.NEW_PASSWORD;
import static com.example.java.TestUtils.TestConstants.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Profile("test")
@ActiveProfiles("test")
public class JwtAuthControllerIntegrationTest {

    /**
     * MockMvc used to mock requests to controller.
     */
    private MockMvc mockMvc;
    /**
     * Context.
     */
    @Autowired
    private WebApplicationContext context;
    /**
     * Token utils used to work with the token.
     */
    @Autowired
    private JwtTokenUtils tokenUtils;
    /**
     * Battery user repo used to return users.
     */
    @Autowired
    private BatteryUserRepository batteryUserRepository;
    /**
     * Base url used to make requests.
     */
    private static final String BASE_URL = "/api/v1/authentication";
    /**
     * Mapper used to write objects as json.
     */
    private ObjectMapper ow;
    /**
     * BatterUser mock.
     */
    private BatteryUser batteryUserMock;
    /**
     * Password encoder used to generate a good password for the mocked user.
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    /**
     * Controller to be tested.
     */
    @Autowired
    private JwtAuthenticationController jwtAuthenticationController;

    /**
     * Setup made before each request.
     */
    @BeforeEach
    public void setup() {
        // we are setting the context before each call
        ow = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        batteryUserMock = new BatteryUser();
        batteryUserMock.setEmail("test");
        batteryUserMock.setPassword(passwordEncoder.encode("Pa2!aaa"));
        batteryUserMock.setVerified(true);
        batteryUserRepository.save(batteryUserMock);
    }

    /**
     * Cleanup made after each request.
     */
    @AfterEach
    public void cleanup() {
        batteryUserRepository.deleteAll();
    }

    /**
     * Successful case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void loginTestSuccessful() throws Exception {
        JwtRequest jwtRequestMock = new JwtRequest(batteryUserMock.getEmail(), "Pa2!aaa");
        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));

        MvcResult result = mockMvc.perform(post(BASE_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ow.writeValueAsString(jwtRequestMock)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject jsonToken = (JSONObject) parser.parse(result.getResponse().getContentAsString());
        String responseToken = jsonToken.getAsString("token");
        System.out.println(responseToken);

        assertThat(tokenUtils.getUsernameFromToken(responseToken))
                .isEqualTo(tokenUtils.getUsernameFromToken(tokenMock));
    }

    /**
     * Failure case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void loginTestFailure() {
        String badUsernameMock = "cox";
        JwtRequest jwtRequestMock = new JwtRequest(badUsernameMock, "test");

        assertThrows(UsernameNotFoundException.class,
                () -> jwtAuthenticationController.login(jwtRequestMock));
    }

    /**
     * Successful case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void logoutTestSuccessful() throws Exception {
        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));
        TokenRequest tokenRequest = new TokenRequest(tokenMock);

        MvcResult result = mockMvc.perform(post(BASE_URL + "/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(tokenRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assert result.getResponse().getContentAsString().equals("");
    }

    /**
     * Successful case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void registerTestSuccessful() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(EMAIL_BOSCH, NEW_PASSWORD, NAME, NAME);

        MvcResult result = mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(registerRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assert result.getResponse().getContentAsString().equals("");
    }

    /**
     * Failure case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void registerTestFailure() {
        RegisterRequest registerRequest = new RegisterRequest("test", NEW_PASSWORD, NAME, NAME);

        ResponseEntity<?> response = jwtAuthenticationController.register(registerRequest);

        assert response.getStatusCode() == HttpStatus.CONFLICT;
    }

    /**
     * Successful case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void verifyAccountSuccessful() throws Exception {
        batteryUserMock.setVerified(false);
        batteryUserRepository.save(batteryUserMock);
        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));
        TokenRequest tokenRequest = new TokenRequest(tokenMock);

        MvcResult result = mockMvc.perform(post(BASE_URL + "/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(tokenRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assert result.getResponse().getContentAsString().equals("");
    }

    /**
     * Failure case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void verifyAccountTestFailure() {
        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));
        TokenRequest tokenRequest = new TokenRequest(tokenMock);

        ResponseEntity<?> response = jwtAuthenticationController.verifyAccount(tokenRequest);

        assert response.getStatusCode() == HttpStatus.ACCEPTED;
        assert response.getBody() == "Account already verified";
    }

    /**
     * Successful case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void requestResetPasswordSuccessful() throws Exception {
        ResetPasswordEmailRequest resetPasswordEmailRequest = new ResetPasswordEmailRequest("test");

        MvcResult result = mockMvc.perform(post(BASE_URL + "/request-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(resetPasswordEmailRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assert result.getResponse().getContentAsString().equals("");
    }

    /**
     * Failure case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void requestResetPasswordTestFailure() {
        ResetPasswordEmailRequest resetPasswordEmailRequest = new ResetPasswordEmailRequest("no");

        ResponseEntity<?> response = jwtAuthenticationController.requestResetPassword(resetPasswordEmailRequest);

        assert response.getStatusCode() == HttpStatus.CONFLICT;
    }

    /**
     * Successful case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void resetPasswordSuccessful() throws Exception {
        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(NEW_PASSWORD, tokenMock);

        MvcResult result = mockMvc.perform(post(BASE_URL + "/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(resetPasswordRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isAccepted())
                .andReturn();

        assert result.getResponse().getContentAsString().equals("");
    }

    /**
     * Failure case test.
     *
     * @throws Exception - exception.
     */
    @Test
    public void resetPasswordTestFailure() {
        String tokenMock = tokenUtils.generateToken(new BatteryUserPrincipal(batteryUserMock));
        //tokenUtils.invalidateToken(tokenMock);
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(PASSWORD, tokenMock);

        ResponseEntity<?> response = jwtAuthenticationController.resetPassword(resetPasswordRequest);

        assert response.getStatusCode() == HttpStatus.ACCEPTED;
    }
}
