package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TenantRequest extends Request {
    /**
     * Number of the page that the user requested.
     */
    private Integer pageNr;
    /**
     * Size of a page that the user requested.
     */
    private Integer pageSize;
    /**
     * The user that requested tenants.
     */
    private String username;
}
