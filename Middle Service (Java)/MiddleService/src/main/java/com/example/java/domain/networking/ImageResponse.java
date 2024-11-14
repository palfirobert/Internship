package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse extends Response {
    /**
     * List of images converted to strings.
     */
    private List<String> images;
}
