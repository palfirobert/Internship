package com.example.java.domain.networking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class PlotRequest extends Request {
    /**
     * Tenant id.
     */
    private String tenantId;
    /**
     * List of batteries that need plot.
     */
    private List<String> batteryIds;
    /**
     * Plot type.
     */
    private String plotType;

}
