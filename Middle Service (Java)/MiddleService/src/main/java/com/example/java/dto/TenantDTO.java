package com.example.java.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonSerialize
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class TenantDTO {
    /**
     * The id of the tenant that is wrapped around by the dto.
     */
    private String tenantId;

}
