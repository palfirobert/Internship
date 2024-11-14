package com.example.java.service;

import com.example.java.domain.BatteryUser;
import com.example.java.domain.BatteryUserPrincipal;
import com.example.java.domain.networking.RegisterRequest;
import com.example.java.domain.networking.ResetPasswordEmailRequest;
import com.example.java.domain.networking.ResetPasswordRequest;
import com.example.java.domain.networking.TokenRequest;
import com.example.java.repository.BatteryUserRepository;
import lombok.extern.slf4j.Slf4j;
import com.example.java.utils.JwtTokenUtils;
import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.InvalidKeyException;
import java.util.Optional;

@Service
@Slf4j
public class UserDetailServiceDB implements UserDetailsService {

    /**
     * Base url.
     */
    @Value("${url.base}")
    private String baseUrl;

    /**
     * Confirm email url.
     */
    @Value("${url.confirmEmail}")
    private String confirmEmail;

    /**
     * Reset password url.
     */
    @Value("${url.resetPassword}")
    private String resetPassword;
    /**
     * BatteryUserRepository used to interact with the users from the db.
     */
    private final BatteryUserRepository batteryUserRepository;

    /**
     * PasswordEncrypter used to check password and encrypt it.
     */
    private final BCryptPasswordEncoder encoder;

    /**
     * Mail service.
     */
    private final MailService mailService;

    /**
     * Utils classed used to work with the JWT.
     */
    private final JwtTokenUtils jwtTokenUtils;



    /**
     * @param batteryUserRepositoryInstance repository used for accessing data
     * @param batteryEncoder encoder injected
     * @param mailServiceInstance mail service injected
     * @param jwtTokenUtilsInstance jwt token injected
     */
    @Autowired
    public UserDetailServiceDB(final BatteryUserRepository batteryUserRepositoryInstance,
                               final BCryptPasswordEncoder batteryEncoder, final MailService mailServiceInstance,
                               final JwtTokenUtils jwtTokenUtilsInstance) {
        this.batteryUserRepository = batteryUserRepositoryInstance;
        this.encoder = batteryEncoder;
        this.mailService = mailServiceInstance;
        this.jwtTokenUtils = jwtTokenUtilsInstance;
    }


    /**
     * Function that loads a user from the db and checks if the password matches.
     *
     * @param username - String containing the user`s username.
     * @param password - String containing the user`s password.
     * @return - BatteryUserPrincipal wrapped around the user.
     * @throws UsernameNotFoundException - Exception thrown in case of no such user in the db.
     */
    public UserDetails loadUserByUsernameAndPassword(final String username, final String password)
            throws UsernameNotFoundException {
        Optional<BatteryUser> user = batteryUserRepository.findByEmail(username);
        if (user.isEmpty()) {
            log.info("No such user " + username);
            throw new UsernameNotFoundException(username);
        }

        if (!encoder.matches(password, user.get().getPassword())) {
            log.info("Password not matching for user " + username);
            throw new UsernameNotFoundException(username);

        }

        if (!user.get().isVerified()) {
            log.info("Account not verified: " + username);
            throw new UsernameNotFoundException(username);
        }
        log.info("Successful login for user " + username);
        return new BatteryUserPrincipal(user.get());
    }

    /**
     * Function that loads a user by the username from the db.
     *
     * @param username - String username.
     * @return BatteryUserPrincipal for the user with the username given.
     * @throws UsernameNotFoundException - Exception thrown in case of no such user in the db.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<BatteryUser> user = batteryUserRepository.findByEmail(username);
        if (user.isEmpty()) {
            log.info("No such user " + username);
            throw new UsernameNotFoundException(username);
        } else {
            return new BatteryUserPrincipal(user.get());
        }
    }

    /**
     * Resets the password of an account.
     * @param request - Token and new password.
     * @throws Exception - In case of user not found, invalid password, expired token.
     */
    public void resetPassword(@Valid final ResetPasswordRequest request) throws Exception {
        String email = jwtTokenUtils.getUsernameFromToken(request.getToken());
        Optional<BatteryUser> user = batteryUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.info("No such user " + email);
            throw new UsernameNotFoundException(email);
        }

        if (Boolean.FALSE.equals(jwtTokenUtils.isTokenValid(request.getToken()))) {
            log.info("Token expired for reset password for user " + email);
            throw new CredentialsExpiredException("Token has expired");
        }


        if (!this.validatePassword(request.getPassword())) {
            log.info("Invalid password for reset password for user " + email);
            throw new InvalidKeyException("`Invalid password`");
        }

        user.get().setPassword(encoder.encode(request.getPassword()));
        batteryUserRepository.save(user.get());

        new Thread(() ->
                mailService.sendPasswordResetConfirmationEmail(user.get().getFamilyName() + " " + user.get().getGivenName(), email)
        ).start();

        jwtTokenUtils.invalidateToken(request.getToken());
    }

    /**
     * Process the request to reset password.
     * @param request - Email of user that want to reset password.
     * @throws Exception - In case of user not found.
     */
    public void requestResetPassword(final ResetPasswordEmailRequest request) throws Exception {

        Optional<BatteryUser> user = batteryUserRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            log.info("No account associated with " + request.getEmail());
            throw new UsernameNotFoundException("No account associated with " + request.getEmail());
        }

        BatteryUserPrincipal batteryUserPrincipal = new BatteryUserPrincipal(user.get());
        String resetPasswordUrl = this.generateURLConfirmEmail(batteryUserPrincipal, resetPassword);

        new Thread(
                () -> mailService.sendResetPasswordMail(user.get().getFamilyName() + " " + user.get().getGivenName(), request.getEmail(), resetPasswordUrl)
        ).start();

    }

    /**
     * Register new user.
     * @param request - User data: name, email, password.
     * @throws Exception - In case of email already in use, invalid email, invalid password.
     */
    public void register(@Valid final RegisterRequest request) throws Exception {
        Optional<BatteryUser> user = batteryUserRepository.findByEmail(request.getEmail());
        if (user.isPresent()) {
            log.info("Email already in use " + request.getEmail());
            throw new AlreadyUsedException(request.getEmail());
        }

        if (!request.getEmail().endsWith("@bosch.com")) {
            log.info("Invalid email for register " + request.getEmail());
            throw new InvalidKeyException("Invalid email");
        }

        if (!this.validatePassword(request.getPassword())) {
            log.info("Invalid password for register for user " + request.getEmail());
            throw new InvalidKeyException("Invalid password");
        }

        BatteryUser newUser = new BatteryUser(request.getEmail(),
                encoder.encode(request.getPassword()),
                request.getFamilyName(),
                request.getGivenName());
        BatteryUser savedUser = batteryUserRepository.save(newUser);
        BatteryUserPrincipal batteryUserPrincipal = new BatteryUserPrincipal(savedUser);
        String confirmUrl = this.generateURLConfirmEmail(batteryUserPrincipal, confirmEmail);

        new Thread(() ->
                mailService.sendConfirmationEmail(request.getFamilyName() + " " + request.getGivenName(), request.getEmail(), confirmUrl)
        ).start();
    }

    /**
     * Verify an account.
     * @param token - Token to verify account.
     */
    public void verifyUser(final TokenRequest token) {
        String email = jwtTokenUtils.getUsernameFromToken(token.getToken());

        if (Boolean.FALSE.equals(jwtTokenUtils.isTokenValid(token.getToken()))) {
            log.info("Token expired for verify account for user " + email);
            throw new CredentialsExpiredException("Token has expired");
        }

        Optional<BatteryUser> user = batteryUserRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.info("No account associated with " + email);
            throw new UsernameNotFoundException(email);
        }
        if (user.get().isVerified()) {
            log.info("Account already verified " + email);
            throw new UsernameNotFoundException("Account already verified");
        }
        user.get().setVerified(true);
        batteryUserRepository.save(user.get());
    }

    /**
     * Generates URL for verify account.
     * @param batteryUserPrincipal - User details to generate token with.
     * @param path - Path to frontend url.
     * @return - URL.
     */
    protected String generateURLConfirmEmail(final BatteryUserPrincipal batteryUserPrincipal, final String path) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(path)
                .queryParam("token", jwtTokenUtils.generateToken(batteryUserPrincipal))
                .toUriString();
    }

    /**
     * Validates a password.
     * @param password - Password.
     * @return - True or false.
     */
    @SuppressWarnings("checkstyle:WhitespaceAround")
    protected boolean validatePassword(final String password) {
        return password.matches(".*[0-9].*")
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[!@%&?].*")
                && password.matches("^[^*$^(){}\\[\\]:;<>,./~_+\\-=|]+$")
                && password.matches("^[^\\s]+$")
                && password.length()>6;
    }

    /**
     * Logout.
     * @param token - Token.
     */
    public void logout(final TokenRequest token) {
        jwtTokenUtils.invalidateToken(token.getToken());
    }
}
