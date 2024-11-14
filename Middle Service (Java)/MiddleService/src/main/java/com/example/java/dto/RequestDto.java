package com.example.java.dto;

import com.example.java.domain.networking.QueueType;
import com.example.java.domain.networking.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    /**
     * The request that was made.
     */
    private Request request;
    /**
     * The queue type in witch the request will go.
     */
    private QueueType queueType;
}
