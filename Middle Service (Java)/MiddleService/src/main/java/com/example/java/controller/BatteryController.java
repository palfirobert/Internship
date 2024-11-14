package com.example.java.controller;

import com.example.java.domain.networking.BatteryRequest;
import com.example.java.domain.networking.BatteryResponse;
import com.example.java.domain.networking.QueueType;
import com.example.java.dto.RequestDto;
import com.example.java.servicebus.RequestProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/api/v1/battery")
@Slf4j
public class BatteryController {
    /**
     * Battery service for the logic of the controller.
     */
    private final RequestProcessingService processingService;

    /**
     * @param processingServiceInstance The injected service for this class.
     */
    @Autowired
    public BatteryController(final RequestProcessingService processingServiceInstance) {
        this.processingService = processingServiceInstance;
    }

    /**
     * @param tenantId The id of the tenant
     * @param page     The number of the page where we want to find the information
     * @param size     Number of elements on the page.
     * @return The id's of the batteries for the tenant
     */
    @GetMapping("/tenant")
    public final ResponseEntity<Page<String>> getTenantBatteries(@RequestParam final String tenantId,
                                                                 @RequestParam(defaultValue = "0") final int page,
                                                                 @RequestParam(defaultValue = "20") final int size) {
        log.info("Tenant " + tenantId + " requested owned batteries");

        BatteryResponse response = (BatteryResponse) processingService.processRequest(
                new RequestDto(new BatteryRequest(tenantId, page, size), QueueType.BATTERYQUEUE));
        return ResponseEntity.ok(response.getBatteries());
    }

}
