package com.example.java.controller;

import com.example.java.domain.networking.AssignTenantsRequest;
import com.example.java.domain.networking.QueueType;
import com.example.java.domain.networking.TenantRequest;
import com.example.java.domain.networking.TenantResponse;
import com.example.java.dto.RequestDto;
import com.example.java.dto.TenantDTO;
import com.example.java.servicebus.RequestProcessingService;
import com.example.java.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/api/v1/tenants")
@Slf4j
public class UserTenantsController {
    /**
     * Service for the logic of this controller.
     */
    private final RequestProcessingService processingService;

    /**
     * Token used for security.
     */
    private final JwtTokenUtils jwtTokenUtils;


    /**
     * Value of the id in token.
     */
    private static final int TOKEN_INDEX_FOR_ID = 7;

    /**
     * @param processingServiceInstance Injecting service
     * @param jwtTokenUtilsInstance     Injecting token
     */
    @Autowired
    public UserTenantsController(final RequestProcessingService processingServiceInstance,
                                 final JwtTokenUtils jwtTokenUtilsInstance) {
        this.processingService = processingServiceInstance;
        this.jwtTokenUtils = jwtTokenUtilsInstance;
    }

    /**
     * @param page      Page number
     * @param size      Number of elements on the page
     * @param userToken Token of the user used for getting data and security
     * @return Tenant details paginated
     */
    @GetMapping
    public final ResponseEntity<Page<TenantDTO>> getAllTenantsOfSpecificUser(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestHeader("Authorization") final String userToken) {

        log.info("User " + jwtTokenUtils.getUsernameFromToken(userToken.substring(TOKEN_INDEX_FOR_ID))
                + " has requested his tenants");
        TenantResponse response = (TenantResponse) processingService.processRequest(
                new RequestDto(
                        new TenantRequest(page, size,
                                jwtTokenUtils.getUserIdFromToken(userToken.substring(TOKEN_INDEX_FOR_ID))),
                        QueueType.TENANTQUEUE));
        return ResponseEntity.ok(response.getTenants());
    }


    /**
     * @param page      Page number
     * @param size      Number of elements on the page
     * @param userToken Token of the user used for getting data and security
     * @return Tenant details paginated
     */
    @GetMapping(value = "/all")
    public final ResponseEntity<Page<TenantDTO>> getAllTenants(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestHeader("Authorization") final String userToken) {

        log.info("User " + jwtTokenUtils.getUsernameFromToken(userToken.substring(TOKEN_INDEX_FOR_ID))
                + " has requested all tenants");
        TenantResponse response = (TenantResponse) processingService.processRequest(
                new RequestDto(
                        new TenantRequest(page, size, ""),
                        QueueType.TENANTQUEUE));
        return ResponseEntity.ok(response.getTenants());
    }

    /**
     * @param tenantsList - List of tenants.
     * @param userToken - User`s token.
     * @return HttpResponse of error or ok.
     */
    @PostMapping(value = "/assign", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<?> assignTenants(
            @RequestBody final List<String> tenantsList,
            @RequestHeader("Authorization") final String userToken) {

        String email = jwtTokenUtils.getUsernameFromToken(userToken.substring(TOKEN_INDEX_FOR_ID));
        log.info("User " + email + " has requested to get tenants assigned");

        processingService.processRequest(new RequestDto(new AssignTenantsRequest(
                tenantsList, email), QueueType.TENANTQUEUE));

        return ResponseEntity.ok().build();
    }

}
