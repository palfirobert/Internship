package com.example.java.domain.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    /**
     * email - String containing the email of the user that wants to log in.
     */
    private String email;
    /**
     * password - String containing the password of the user that wants to log in.
     */
    private String password;

}
