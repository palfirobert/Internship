package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request implements Serializable {
    /**
     * Unique identifier used to keep track of each request.
     */
    private String requestId;
}
