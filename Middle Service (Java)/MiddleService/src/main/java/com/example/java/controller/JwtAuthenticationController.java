package com.example.java.controller;

import com.example.java.domain.BatteryUserPrincipal;
import com.example.java.domain.networking.JwtRequest;
import com.example.java.domain.networking.JwtResponse;
import com.example.java.domain.networking.RegisterRequest;
import com.example.java.domain.networking.ResetPasswordEmailRequest;
import com.example.java.domain.networking.ResetPasswordRequest;
import com.example.java.domain.networking.TokenRequest;
import com.example.java.service.UserDetailServiceDB;
import com.example.java.utils.JwtTokenUtils;
import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/v1/authentication")
@Slf4j
public final class JwtAuthenticationController {
    /**
     * Utils classed used to work with the JWT.
     */
    private final JwtTokenUtils jwtTokenUtils;
    /**
     * Service used to check if a user is existent in our app
     * and if its password was correct.
     */
    private final UserDetailServiceDB userDetailServiceDB;

    /**
     * Constructor method.
     *
     * @param tokenUtils        - JwtTokenUtils
     * @param userDetailService - UserDetailServiceDB
     */
    @Autowired
    public JwtAuthenticationController(final JwtTokenUtils tokenUtils,
                                       final UserDetailServiceDB userDetailService) {

        this.jwtTokenUtils = tokenUtils;
        this.userDetailServiceDB = userDetailService;
    }

    /**
     * @param authenticationRequest - JwtRequest, with the data for a login
     * @return HttpResponse of error or ok with the token
     */
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody final JwtRequest authenticationRequest) {

        log.info("New login request for user " + authenticationRequest.getEmail());

        return ResponseEntity.ok(new JwtResponse(jwtTokenUtils.generateToken((BatteryUserPrincipal)
                this.userDetailServiceDB.loadUserByUsernameAndPassword(
                        authenticationRequest.getEmail(), authenticationRequest.getPassword())),
                jwtTokenUtils.getJWT_TOKEN_VALIDITY()));
    }

    /**
     * @param tokenRequest - Token.
     * @return HttpResponse of ok
     */
    @PostMapping(value = "/logout")
    public ResponseEntity<?> logout(@RequestBody final TokenRequest tokenRequest) {
        log.info("New logout request for user " + jwtTokenUtils.getUsernameFromToken(tokenRequest.getToken()));
        this.userDetailServiceDB.logout(tokenRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * @param registerRequest - Register data.
     * @return HttpResponse of error or ok
     */
    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Validated @RequestBody final RegisterRequest registerRequest) {
        log.info("New register request for user " + registerRequest.getEmail());

        try {
            userDetailServiceDB.register(registerRequest);
        } catch (AlreadyUsedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.accepted().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    /**
     * @param tokenRequest - Request that contains token needed to verify account
     * @return HttpResponse of error or ok.
     */
    @PostMapping(value = "/verify")
    public ResponseEntity<?> verifyAccount(@RequestBody final TokenRequest tokenRequest) {

        try {
            userDetailServiceDB.verifyUser(tokenRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.accepted().body(e.getMessage());
        }
    }

    /**
     * @param request - Email of account to reset password to.
     * @return HttpResponse of error or ok
     */
    @PostMapping(value = "/request-reset-password")
    public ResponseEntity<?> requestResetPassword(@RequestBody final ResetPasswordEmailRequest request) {
        log.info("New request to reset password for user " + request.getEmail());

        try {
            userDetailServiceDB.requestResetPassword(request);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.accepted().body(e.getMessage());
        }
    }

    /**
     * @param request - Token, new password.
     * @return HttpResponse of error or ok
     */
    @PostMapping(value = "/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody final ResetPasswordRequest request) {
        log.info("New password request for user " + jwtTokenUtils.getUsernameFromToken(request.getToken()));

        try {
            userDetailServiceDB.resetPassword(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.accepted().body(e.getMessage());
        }
    }

}
