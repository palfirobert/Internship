package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatteryRequest extends Request {
    /**
     * String containing the id of the tenant that made the request.
     */
    private String tenantId;
    /**
     * Integer that indicates which page the tenant wants.
     */
    private Integer page;
    /**
     * Integer representing the size of the page.
     */
    private Integer size;
}
