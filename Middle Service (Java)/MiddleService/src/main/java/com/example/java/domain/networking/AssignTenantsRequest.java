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
public class AssignTenantsRequest extends Request {
    /**
     * List of tenants.
     */
    private List<String> tenantsList;
    /**
     * User`s email.
     */
    private String email;
}
