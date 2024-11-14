package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class BatteryResponse extends Response {
    /**
     * A page with strings that represents the batterIds.
     */
    private Page<String> batteries;
}
