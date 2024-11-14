package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ValidateAccountRequest extends Request {
    /**
     * Email of the user that has validated his account.
     */
    private String email;
    /**
     * Token to validate account.
     */
    private String token;
}
