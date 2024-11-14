package com.example.java.domain.networking;

import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

public final class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    /**
     *Token - String containing the token.
     */
    private final String token;
    /**
     * Seconds value of the session from frontend.
     */
    @Value("${jwt.sessionTime}")
    private long sessionTime;

    /**
     * Constructor that creates a response with the token attached.
     * @param jwtToken - String containing the token.
     * @param sessionTimeInstance
     */
    public JwtResponse(final String jwtToken, final long sessionTimeInstance) {
        this.token = jwtToken;
        this.sessionTime = sessionTimeInstance;
    }

    /**
     * Function that returns the token from the response.
     * @return - String containing the token.
     */
    public String getToken() {
        return token;
    }

    /**
     *
     * @return Session time, used for serialising the object for request.
     */
    public long getSessionTime() {
        return sessionTime;
    }
}
