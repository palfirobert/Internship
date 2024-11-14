package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PythonAuthRequest extends Request {

    /**
     * The username of the account.
     */
    private String username;
    /**
     * The password of tha account.
     */
    private String password;
}
