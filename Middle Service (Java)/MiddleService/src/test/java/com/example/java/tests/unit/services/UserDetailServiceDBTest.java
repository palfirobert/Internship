package com.example.java.tests.unit.services;

import com.example.java.domain.BatteryUser;
import com.example.java.domain.BatteryUserPrincipal;
import com.example.java.domain.networking.RegisterRequest;
import com.example.java.domain.networking.ResetPasswordEmailRequest;
import com.example.java.domain.networking.ResetPasswordRequest;
import com.example.java.domain.networking.TokenRequest;
import com.example.java.repository.BatteryUserRepository;
import com.example.java.service.MailService;
import com.example.java.service.UserDetailServiceDB;
import com.example.java.utils.JwtTokenUtils;
import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.InvalidKeyException;
import java.util.Optional;

import static com.example.java.TestUtils.TestConstants.EMAIL;
import static com.example.java.TestUtils.TestConstants.EMAIL_BOSCH;
import static com.example.java.TestUtils.TestConstants.NAME;
import static com.example.java.TestUtils.TestConstants.NEW_PASSWORD;
import static com.example.java.TestUtils.TestConstants.PASSWORD;
import static com.example.java.TestUtils.TestConstants.PATH;
import static com.example.java.TestUtils.TestConstants.TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
@ActiveProfiles("test")
public class UserDetailServiceDBTest {
    /**
     * Service that we want to test.
     */
    @InjectMocks
    private UserDetailServiceDB userDetailServiceDB;

    /**
     * Mock dependency of the mail service.
     */
    @Mock
    private MailService mailServiceMock;
    /**
     * Mock dependency of the repository.
     */
    @Mock
    private BatteryUserRepository batteryUserRepositoryMock;
    /**
     * Mocking encoder dependency.
     */
    @Mock
    private BCryptPasswordEncoder encoder;

    /**
     * Mocked token utils.
     */
    @Mock
    private JwtTokenUtils tokenUtils;

    /**
     * URL value for test.
     */
    private static final String URL = "http://localhost:4200/path?token=token_test";
    private final String URL_BASE = "http://localhost:4200";
    /**
     * User for every test.
     */
    private BatteryUser batteryUserMock;
    private ResetPasswordRequest resetPasswordRequestMock;
    private ResetPasswordEmailRequest resetPasswordEmailRequestMock;
    private BatteryUserPrincipal batteryUserPrincipalMock;
    private TokenRequest tokenRequestMock;
    private RegisterRequest registerRequestMock;

    @Mock
    private UriComponentsBuilder uriComponentsBuilder;



    /**
     * Create a user before every test.
     */
    @BeforeEach
    public final void createBatteryUserMock() {
        // 0. CREATE COMPARISON AND RETURNED OBJECTS
        batteryUserMock = new BatteryUser();
        batteryUserMock.setEmail(EMAIL_BOSCH);
        batteryUserMock.setPassword(PASSWORD);
        batteryUserMock.setVerified(true);
        batteryUserMock.setGivenName(NAME);
        batteryUserMock.setFamilyName(NAME);

        resetPasswordEmailRequestMock = new ResetPasswordEmailRequest(EMAIL);
        resetPasswordRequestMock = new ResetPasswordRequest(EMAIL, TOKEN);
        batteryUserPrincipalMock = new BatteryUserPrincipal(batteryUserMock);
        tokenRequestMock = new TokenRequest(TOKEN);

        registerRequestMock = new RegisterRequest(EMAIL_BOSCH, PASSWORD, NAME, NAME);
    }

    /**
     * Verify if user is loaded with the correct credentials and every method is called correctly.
     */
    @Test
    public void loadUserByUsernameAndPasswordSuccessful() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        when(encoder.matches(any(String.class), any(String.class))).thenReturn(true);
        // 2. CALL THE SERVICE TO GET THE RESULT
        UserDetails result = userDetailServiceDB.loadUserByUsernameAndPassword(EMAIL, PASSWORD);
        // 3. CHECK IF THE VALUES ARE CORRECT
        assertThat(result.getUsername()).isEqualTo(batteryUserMock.getEmail());
        assertThat(result.getPassword()).isEqualTo(batteryUserMock.getPassword());
        // 4. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
    }

    /**
     * Verify if user is not found and throws exception.
     */
    @Test
    public void loadUserByUsernameAndPasswordFailedUserEmpty() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.empty());
        // 2. CHECK IF THE VALUES ARE CORRECT
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailServiceDB.loadUserByUsernameAndPassword(EMAIL, PASSWORD));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
        verify(encoder, times(0)).matches(any(String.class), any(String.class));
    }

    /**
     * verify if credentials are not matching and throws error.
     */
    @Test
    public void loadUserByUsernameAndPasswordFailedPasswordNotMatching() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        when(encoder.matches(any(String.class), any(String.class))).thenReturn(false);
        // 2. CHECK IF THE EXCEPTION IS THROWN
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailServiceDB.loadUserByUsernameAndPassword(EMAIL, PASSWORD));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
        verify(encoder, times(1)).matches(any(String.class), any(String.class));
    }

    /**
     * Verify if user is not found and throws exception.
     */
    @Test
    public void loadUserByUsernameFailedUserEmpty() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.empty());
        // 2. CHECK IF THE EXCEPTION IS THROWN
        assertThrows(UsernameNotFoundException.class, () -> userDetailServiceDB.loadUserByUsername(EMAIL));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
    }

    /**
     * Verify if user account is verified and throws exception.
     */
    @Test
    public void loadUserByUsernameAndPasswordFailedAccountNotVerified() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        batteryUserMock.setVerified(false);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        when(encoder.matches(any(String.class), any(String.class))).thenReturn(true);
        // 2. CHECK IF THE VALUES ARE CORRECT
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailServiceDB.loadUserByUsernameAndPassword(EMAIL, PASSWORD));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
        verify(encoder, times(1)).matches(any(String.class), any(String.class));
    }

    /**
     * Verify if user is loaded with the correct credentials and every method is called correctly.
     */
    @Test
    public void loadUserByUsernameSuccessful() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        // 2. CALL THE SERVICE TO GET THE RESULT
        UserDetails result = userDetailServiceDB.loadUserByUsername(EMAIL);
        // 3. CHECK IF THE VALUES ARE CORRECT
        assertThat(result.getUsername()).isEqualTo(batteryUserMock.getEmail());
        // 4. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
    }

    @Test void requestResetPasswordNoUser() {
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.empty());
        // 2. CHECK IF THE EXCEPTION IS THROWN
        assertThrows(UsernameNotFoundException.class, () -> userDetailServiceDB.requestResetPassword(resetPasswordEmailRequestMock));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
    }

    @Test void requestResetPasswordSuccessful() throws Exception {
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        BatteryUserPrincipal batteryUserPrincipal = new BatteryUserPrincipal(batteryUserMock);

        when(tokenUtils.generateToken(any(BatteryUserPrincipal.class))).thenReturn(TOKEN);
        ReflectionTestUtils.setField(userDetailServiceDB, "baseUrl", URL_BASE);
        ReflectionTestUtils.setField(userDetailServiceDB, "resetPassword", PATH);


        userDetailServiceDB.requestResetPassword(resetPasswordEmailRequestMock);
        //(mailServiceMock, times(1)).sendMail(anyString(), anyString(), anyString());
    }

    @Test void resetPasswordNoUser() {
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.empty());
        // 2. CHECK IF THE EXCEPTION IS THROWN
        assertThrows(UsernameNotFoundException.class, () -> userDetailServiceDB.resetPassword(resetPasswordRequestMock));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
    }

    @Test void resetPasswordInvalidToken() {
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));

        when(tokenUtils.isTokenValid(TOKEN)).thenReturn(false);
        assertThrows(CredentialsExpiredException.class, () -> userDetailServiceDB.resetPassword(resetPasswordRequestMock));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
        verify(batteryUserRepositoryMock, times(0)).save(batteryUserMock);
    }

    @Test void resetPasswordInvalidPassword() {
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));

        when(tokenUtils.isTokenValid(TOKEN)).thenReturn(true);
        //when(userDetailServiceDB.validatePassword(PASSWORD)).thenReturn(false);
        assertThrows(InvalidKeyException.class, () -> userDetailServiceDB.resetPassword(resetPasswordRequestMock));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
        verify(batteryUserRepositoryMock, times(0)).save(batteryUserMock);
    }

    @Test void resetPasswordSuccessful() throws Exception {
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        when(tokenUtils.isTokenValid(TOKEN)).thenReturn(true);
        resetPasswordRequestMock.setPassword(NEW_PASSWORD);
        userDetailServiceDB.resetPassword(resetPasswordRequestMock);
        verify(batteryUserRepositoryMock, times(1)).save(batteryUserMock);
        verify(tokenUtils, times(1)).invalidateToken(TOKEN);
    }

    @Test
    void logout() {
        userDetailServiceDB.logout(tokenRequestMock);
        verify(tokenUtils, times(1)).invalidateToken(TOKEN);
    }

    @Test void verifyUserInvalidToken(){
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);

        when(tokenUtils.isTokenValid(TOKEN)).thenReturn(false);
        assertThrows(CredentialsExpiredException.class, () -> userDetailServiceDB.verifyUser(tokenRequestMock));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(0)).findByEmail(EMAIL);
    }

    @Test void verifyUserNoUser() {
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);

        when(tokenUtils.isTokenValid(TOKEN)).thenReturn(true);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailServiceDB.verifyUser(tokenRequestMock));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
        verify(batteryUserRepositoryMock, times(0)).save(batteryUserMock);
    }

    @Test void verifyUserAccountAlreadyVerified() {
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);

        when(tokenUtils.isTokenValid(TOKEN)).thenReturn(true);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        assertThrows(UsernameNotFoundException.class, () -> userDetailServiceDB.verifyUser(tokenRequestMock));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(batteryUserRepositoryMock, times(1)).findByEmail(EMAIL);
        verify(batteryUserRepositoryMock, times(0)).save(batteryUserMock);
    }

    @Test void verifyUserSuccessful() {
        when(tokenUtils.getUsernameFromToken(TOKEN)).thenReturn(EMAIL);

        when(tokenUtils.isTokenValid(TOKEN)).thenReturn(true);
        when(batteryUserRepositoryMock.findByEmail(EMAIL)).thenReturn(Optional.of(batteryUserMock));
        batteryUserMock.setVerified(false);

        userDetailServiceDB.verifyUser(tokenRequestMock);
        verify(batteryUserRepositoryMock, times(1)).save(batteryUserMock);
    }

    @Test void registerEmailInUse() {
        when(batteryUserRepositoryMock.findByEmail(anyString())).thenReturn(Optional.of(batteryUserMock));

        assertThrows(AlreadyUsedException.class, () -> userDetailServiceDB.register(registerRequestMock));
        verify(batteryUserRepositoryMock, times(0)).save(batteryUserMock);

    }

    @Test void registerInvalidEmail() {
        when(batteryUserRepositoryMock.findByEmail(anyString())).thenReturn(Optional.empty());
        registerRequestMock.setEmail(EMAIL);
        assertThrows(InvalidKeyException.class, () -> userDetailServiceDB.register(registerRequestMock));
        verify(batteryUserRepositoryMock, times(0)).save(batteryUserMock);

    }

    @Test void registerInvalidPassword() {
        when(batteryUserRepositoryMock.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidKeyException.class, () -> userDetailServiceDB.register(registerRequestMock));
        verify(batteryUserRepositoryMock, times(0)).save(batteryUserMock);

    }

    @Test void registerSuccessful() throws Exception {
        when(batteryUserRepositoryMock.findByEmail(anyString())).thenReturn(Optional.empty());

        when(encoder.encode(anyString())).thenReturn(PASSWORD);
        BatteryUser userMock = new BatteryUser(registerRequestMock.getEmail(), PASSWORD, registerRequestMock.getFamilyName(), registerRequestMock.getGivenName());

        batteryUserMock.setVerified(false);
        batteryUserMock.setPassword(userMock.getPassword());
        batteryUserMock.setEmail(userMock.getEmail());
        when(batteryUserRepositoryMock.save(userMock)).thenReturn(batteryUserMock);

        when(tokenUtils.generateToken(any(BatteryUserPrincipal.class))).thenReturn(TOKEN);
        ReflectionTestUtils.setField(userDetailServiceDB, "baseUrl", URL_BASE);
        ReflectionTestUtils.setField(userDetailServiceDB, "resetPassword", PATH);

        registerRequestMock.setPassword(NEW_PASSWORD);
        userDetailServiceDB.register(registerRequestMock);

        verify(batteryUserRepositoryMock, times(1)).save(batteryUserMock);
    }

}
