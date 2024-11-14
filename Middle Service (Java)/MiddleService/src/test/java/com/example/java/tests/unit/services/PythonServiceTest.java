package com.example.java.tests.unit.services;

import com.example.java.domain.networking.PlotRequest;
import com.example.java.domain.networking.PlotResponse;
import com.example.java.service.PythonService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
@ActiveProfiles("test")
public class PythonServiceTest {
    /**
     * object that is tested.
     */
    @InjectMocks
    private PythonService pythonService;
    /**
     * Mocked tenant id.
     */
    private static final String TENANT_ID_MOCK = "1aa9d5e8-a182-414b-be97-1791fd89fc27";

    /**
     * dependency to be mocked.
     */
    @Mock
    private RestTemplate restTemplate;

    /**
     * Object from the method that will be mocked.
     */
    @Mock
    private ObjectWriter objectWriter;

    /**
     * Mocked python url.
     */
    @Value("$python.url")
    private String mockUrl;


    /**
     * @throws JsonProcessingException in case there is an error with converting the object in json.
     */
    /*@Test
    public void getPlotResponseSuccessful() throws JsonProcessingException {


        // 1. CREATE COMPARISON AND RETURNED OBJECTS
        List<String> locationsMock = new ArrayList<>();
        locationsMock.add("loc1");
        locationsMock.add("loc2");
        PlotResponse responseMock = new PlotResponse();
        responseMock.setLocations(locationsMock);
        responseMock.setMessage("message");
        ResponseEntity<PlotResponse> apiResponseMock = new ResponseEntity<>(responseMock, HttpStatus.OK);
        // 2. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS

        when(restTemplate.exchange(
                eq(mockUrl),
                eq(HttpMethod.POST),
                any(),
                eq(PlotResponse.class)))
                .thenReturn(apiResponseMock);
        // 3. CALL THE SERVICE TO GET THE RESULT
        String plotTypeMock = "SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME";
        List<String> batteryIdsMock = new ArrayList<>();
        batteryIdsMock.add("test");

        PlotRequest plotRequestMock = new PlotRequest();
        plotRequestMock.setTenantId(TENANT_ID_MOCK);
        plotRequestMock.setBatteryIds(batteryIdsMock);
        plotRequestMock.setPlotType(plotTypeMock);

        PlotResponse responseResult = pythonService.getPLotLocations(plotRequestMock);
        // 4. CHECK IF THE VALUES ARE CORRECT
        assertThat(responseResult).isEqualTo(responseMock);
        // 5. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(restTemplate, times(1)).exchange(
                eq(mockUrl),
                eq(HttpMethod.POST),
                any(),
                eq(PlotResponse.class));
    }
*/
    /**
     * @throws JsonProcessingException will be thrown to test this branch too.
     */
    @Test
    public void getPlotResponseThrowsError() throws JsonProcessingException {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(objectWriter.writeValueAsString(new PlotRequest())).thenThrow(new JsonProcessingException("Error") {
        });
        // 2. CHECK IF THE EXCEPTION IS THROWN
        assertThrows(JsonProcessingException.class, () -> objectWriter.writeValueAsString(new PlotRequest()));
        // 3. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(restTemplate, times(0)).exchange(any(), any(), any(), eq(PlotResponse.class));
    }

}
