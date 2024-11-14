package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TokenRequest extends Request {
    /**
     * Token to validate account.
     */
    private String token;
}
