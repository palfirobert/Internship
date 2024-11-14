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
public class RegisterRequest extends Request {
    /**
     * Email/username of the user.
     */
    private String email;
    /**
     * Password of the user.
     */
    private String password;
    /**
     * Family name of the user.
     */
    private String familyName;
    /**
     * Given name of the user.
     */
    private String givenName;
}
