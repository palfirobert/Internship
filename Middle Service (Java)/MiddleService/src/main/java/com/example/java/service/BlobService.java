package com.example.java.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public final class BlobService {

    /**
     * BlobServiceClient used to interact with the blob.
     */
    private final BlobServiceClient blobServiceClient;

    /**
     * Gets the data from the blob.
     */
    private BinaryData binaryDataLayer;
    /**
     * Gets the blob.
     */
    private BlobClient blobClient;

    /**
     * String containing the name of the container from azure.
     */
    @Value("${azure.blob.container}")
    private String containerName;

    /**
     * @param blobServiceClientInstance - instance of the BlobServiceClient.
     */
    @Autowired
    public BlobService(final BlobServiceClient blobServiceClientInstance) {
        this.blobServiceClient = blobServiceClientInstance;
    }

    private BlobContainerClient containerClient() {
        return blobServiceClient.getBlobContainerClient(containerName);
    }

    /**
     * Function that searches for an image in the blob storage
     * and returns it encoded in base 64.
     *
     * @param blobPath - String with the path to the image.
     * @return - String containing the encoded image.
     */
    public String getImageFromBlobPathsAsBase64(final String blobPath) {
        BlobContainerClient container = containerClient();
        blobClient = container.getBlobClient(blobPath);
        binaryDataLayer = blobClient.downloadContent();
        byte[] imageBytes = binaryDataLayer.toBytes();
        return Base64.encodeBase64String(imageBytes);
    }

    /**
     * Function that receives a list of locations and returns a list of encoded images.
     * @param locations - List opf strings containing the locations from the blob storage.
     * @return - List if encoded images in base 64.
     */
    public List<String> getImagesFromLocations(final List<String> locations) {
        List<String> imagesAsBase64 = new ArrayList<>();

        for (String loc : locations) {
            imagesAsBase64.add(this.getImageFromBlobPathsAsBase64(loc));
        }
        return imagesAsBase64;
    }
}
