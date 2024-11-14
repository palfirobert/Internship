package com.example.java.tests.unit.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.example.java.service.BlobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Profile("test")
@ActiveProfiles("test")
public class BlobServiceTest {
    /**
     * Object to be tested.
     */
    @InjectMocks
    private BlobService service;

    /**
     * Blob service with the proxy injected that is mocked.
     */
    @Mock
    private BlobServiceClient blobServiceClientMocked;
    /**
     * Container used for testing.
     */
    @Mock
    private BlobContainerClient containerMocked;
    /**
     * Object to get the mocked data from the blob.
     */
    @Mock
    private BinaryData binaryDataLayerMocked;
    /**
     * Blob that is mocked.
     */
    @Mock
    private BlobClient blobClientMock;

    /**
     * Image in byte format for testing.
     */
    private final byte[] imageBytesMock = "img".getBytes();

    /**
     * Verify if the image is taken from the blob storage with no errors.
     */
    @Test
    public void getImageFromBlobPathsAsBase64Successful() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(blobServiceClientMocked.getBlobContainerClient(any())).thenReturn(containerMocked);
        when(containerMocked.getBlobClient(any())).thenReturn(blobClientMock);
        when(blobClientMock.downloadContent()).thenReturn(binaryDataLayerMocked);
        when(binaryDataLayerMocked.toBytes()).thenReturn(imageBytesMock);
        ReflectionTestUtils.setField(service, "binaryDataLayer", binaryDataLayerMocked);
        ReflectionTestUtils.setField(service, "blobClient", blobClientMock);
        // 2. CALL THE SERVICE TO GET THE RESULT
        String result = service.getImageFromBlobPathsAsBase64(any(String.class));
        // 3. CHECK IF THE VALUES ARE CORRECT
        assertThat(result).isEqualTo(Base64.encodeBase64String(imageBytesMock));
        // 4. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(blobServiceClientMocked, times(1)).getBlobContainerClient(any());
        verify(containerMocked, times(1)).getBlobClient(any());
        verify(blobClientMock, times(1)).downloadContent();
        verify(binaryDataLayerMocked, times(1)).toBytes();
    }

    /**
     * Case if the path from the method is wrong.
     */
    @Test
    public void getImageFromBlobPathsAsBase64WrongPath() {
        // 1. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(blobServiceClientMocked.getBlobContainerClient(any())).thenReturn(containerMocked);
        // 2. CALL THE SERVICE TO GET THE RESULT
        service = new BlobService(blobServiceClientMocked);
        // 3. CHECK IF THE VALUES ARE CORRECT
        assertThrows(NullPointerException.class,
                () -> service.getImageFromBlobPathsAsBase64("WRONG PATH"));
        // 4. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(blobClientMock, times(0)).downloadContent();
        verify(binaryDataLayerMocked, times(0)).toBytes();
    }

    /**
     * Case if the container is not valid.
     */
    @Test
    public void getImageFromBlobPathsAsBase64WrongContainerName() {
        // 1. CHECK IF THE EXCEPTION IS THROWN
        assertThrows(NullPointerException.class,
                () -> service.getImageFromBlobPathsAsBase64("WRONG PATH"));
        // 2. VERIFY IF THE MOCKED BEANS ARE CALLED AS THEY SHOULD
        verify(containerMocked, times(0)).getBlobClient(any());
        verify(blobClientMock, times(0)).downloadContent();
        verify(binaryDataLayerMocked, times(0)).toBytes();
    }

    /**
     * Verify if the images are converted and returned as they should.
     */
    @Test
    public void getImagesFromLocationsSuccessful() {
        // 1. CREATE COMPARISON AND RETURNED OBJECTS
        List<String> locationsMock = new ArrayList<>();
        locationsMock.add("loc1");
        // 2. WE IMPOSE THE BEHAVIOR OF THE MOCKED OBJECTS
        when(blobServiceClientMocked.getBlobContainerClient(any())).thenReturn(containerMocked);
        when(containerMocked.getBlobClient(any())).thenReturn(blobClientMock);
        when(blobClientMock.downloadContent()).thenReturn(binaryDataLayerMocked);
        when(binaryDataLayerMocked.toBytes()).thenReturn(imageBytesMock);
        ReflectionTestUtils.setField(service, "binaryDataLayer", binaryDataLayerMocked);
        ReflectionTestUtils.setField(service, "blobClient", blobClientMock);
        // 3. CALL THE SERVICE TO GET THE RESULT
        List<String> result = service.getImagesFromLocations(locationsMock);
        // 4. CHECK IF THE VALUES ARE CORRECT
        assertThat(result.get(0)).isEqualTo(Base64.encodeBase64String(imageBytesMock));
    }
}
