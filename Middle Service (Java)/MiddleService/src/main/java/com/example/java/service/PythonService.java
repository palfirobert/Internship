package com.example.java.service;

import com.example.java.domain.networking.PlotRequest;
import com.example.java.domain.networking.PlotResponse;
import com.example.java.domain.networking.PythonAuthRequest;
import com.example.java.domain.networking.PythonAuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class PythonService {


    /**
     * Variable that makes api calls.
     */

    private final RestTemplate restTemplate;
    /**
     * Mocked python url.
     */
    @Value("${python.url}")
    private String pythonUrl;

    /**
     * The auth token.
     */
    private String token;
    /**
     * Username for authentication.
     */
    @Value("${python.account.username}")
    private String username;
    /**
     * Password for authentication.
     */
    @Value("${python.account.password}")
    private String password;

    /**
     * Initialise variable for calling rest.
     *
     * @param restTemplateInstance is a rest template bean
     */
    @Autowired
    public PythonService(final RestTemplate restTemplateInstance) {
        this.restTemplate = restTemplateInstance;
        token = null;
    }

    /**
     * @param plotRequest Request from the frontend
     * @return Returns response with the locations of the plots in the blob storage
     * @throws JsonProcessingException Exception if the data can't be converted to json
     */
    public final PlotResponse getPLotLocations(final PlotRequest plotRequest) throws JsonProcessingException {

        // connect to the plotting endpoint
        String pythonMicroserviceUrl = pythonUrl; // Replace with your Python microservice URL

        // convert object to json
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(plotRequest);

        // make the object to be a request
        HttpHeaders authHeader = new HttpHeaders() {
            {
                set("Authorization", "Bearer " + token);
            }
        };


        HttpEntity<String> requestAux = new HttpEntity<>(json, authHeader);

        ResponseEntity<PlotResponse> response;
        // make the call to the python service
        try {
            response = restTemplate.exchange(
                    pythonMicroserviceUrl + "/plot",
                    HttpMethod.POST,
                    requestAux,
                    PlotResponse.class
            );
        } catch (Exception e) {
            HttpHeaders authHeader2 = new HttpHeaders() {
                {
                    set("Content-Type", "application/json");
                }
            };

            PythonAuthRequest authRequest = new PythonAuthRequest(username, password);

            String authJson = ow.writeValueAsString(authRequest);

            HttpEntity<String> requestAuxForAuth = new HttpEntity<>(authJson, authHeader2);

            ResponseEntity<PythonAuthResponse> pythonAuthResponse = restTemplate.exchange(
                    pythonMicroserviceUrl + "/authenticate",
                    HttpMethod.POST,
                    requestAuxForAuth,
                    PythonAuthResponse.class);

            token = pythonAuthResponse.getBody().getAccess();
            authHeader = new HttpHeaders() {
                {
                    set("Authorization", "Bearer " + token);
                }
            };
            requestAux = new HttpEntity<>(json, authHeader);
            response = restTemplate.exchange(
                    pythonMicroserviceUrl + "/plot",
                    HttpMethod.POST,
                    requestAux,
                    PlotResponse.class
            );
        }
        // return the response with the locations of the plots
        return response.getBody();
    }


}
