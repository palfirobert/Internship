package com.example.java.domain.networking;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlotResponse extends Response {
    /**
     * Message of the response (success/failed).
     */
    private String message;

    /**
     * List of the plot locations in blob storage in azure.
     */
    private List<String> locations;

}
