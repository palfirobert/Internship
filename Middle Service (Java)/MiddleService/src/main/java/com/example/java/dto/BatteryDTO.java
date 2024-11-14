package com.example.java.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class BatteryDTO {
    /**
     * Field for the battery id.
     */
    private String batteryId;
    /**
     * Field for battery type.
     */
    private String batteryType;
    /**
     * Field for tenant id.
     */
    private String tenantId;

}
