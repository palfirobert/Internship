package com.example.java.domain.networking;

import com.example.java.dto.TenantDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TenantResponse extends Response {
    /**
     * A page with the tenants that the user requested.
     */
    private Page<TenantDTO> tenants;
}
