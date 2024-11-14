package com.example.java.controller;

import com.example.java.domain.networking.ImageRequest;
import com.example.java.domain.networking.ImageResponse;
import com.example.java.domain.networking.PlotRequest;
import com.example.java.domain.networking.QueueType;
import com.example.java.dto.RequestDto;
import com.example.java.servicebus.RequestProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/blob")
@Slf4j
public class BlobController {
    /**
     * Service used to process the requests.
     */
    private final RequestProcessingService processingService;

    /**
     * Constructor used to instantiate a BlobController.
     *
     * @param processingServiceInstance - BlobService.
     */
    @Autowired
    public BlobController(final RequestProcessingService processingServiceInstance) {
        this.processingService = processingServiceInstance;
    }

    /**
     * Function that handles an plot request.
     *
     * @param plotRequest - PlotRequest.
     * @return HttpResponse with the images or ErrorResponse with the error code and the error message.
     */
    @PostMapping(value = "/plots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getPlotImages(@RequestBody final PlotRequest plotRequest) {
        log.info("Tenant " + plotRequest.getTenantId()
                +
                " requested " + plotRequest.getPlotType()
                +
                " plot for batteries " + plotRequest.getBatteryIds());

        ImageResponse response = (ImageResponse) processingService.processRequest(
                new RequestDto(
                        new ImageRequest(plotRequest), QueueType.PLOTTINGQUEUE));
        return ResponseEntity.ok().
                body(response.getImages());
    }
}
