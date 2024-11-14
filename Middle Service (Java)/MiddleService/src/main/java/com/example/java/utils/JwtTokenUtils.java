package com.example.java.utils;

import com.example.java.domain.BatteryUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtils implements Serializable {
    /**
     * Serial value.
     */
    private static final long serialVersionUID = -2550185165626007488L;



    /**
     * String containing the secret that will be used to sign the token.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Constant that defines the period of time that the token is valid for.
     */
    @SuppressWarnings("checkstyle:MemberName")
    @Value("${jwt.sessionTime}")
    private long JWT_TOKEN_VALIDITY;

    private Map<String, List<String>> invalidatedTokens = new HashMap<>();

    /**
     * Function that returns the username of the user encrypted in the token.
     *
     * @param token - string containing the token.
     * @return string containing the username from the token.
     */
    public String getUsernameFromToken(final String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     *
     * @return Time of the jwt token.
     */
    @SuppressWarnings("checkstyle:MethodName")
    public long getJWT_TOKEN_VALIDITY() {
        return JWT_TOKEN_VALIDITY;
    }

    /**
     * Function that return the id of the user encrypted in the token.
     *
     * @param token - string containing the token.
     * @return string containing the id of the user encrypted in the token.
     */
    public String getUserIdFromToken(final String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    /**
     * Function that returns the expiration date of the token.
     *
     * @param token - string containing the token.
     * @return Date that represents the expiration date of the token.
     */
    public Date getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Function that returns a claim from the token.
     *
     * @param token          - string containing the token.
     * @param claimsResolver - function that resolves the claims.
     * @param <T>            - type of the wanted claim.
     * @return - the wanted claim with type T.
     */
    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Function that return claims from the token.
     *
     * @param token - string containing the token.
     * @return Claims - claims from the token.
     */
    private Claims getAllClaimsFromToken(final String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Function that checks if the token has expired.
     *
     * @param token - string containing the token.
     * @return return true if the token is expired or false otherwise.
     */
    public Boolean isTokenExpired(final String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Function that receives a UserDetails and returns a token.
     *
     * @param userDetails - UserDetails that will be encrypted in the token.
     * @return string containing a token for that user.
     */
    public String generateToken(final BatteryUserPrincipal userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("userid", userDetails.getUserId());
        return doGenerateToken(claims, userDetails.getUsername(), userDetails.getUserId().toString());
    }


    /**
     * Function that generates the token.
     * //while creating the token -
     * //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
     * //2. Sign the JWT using the HS512 algorithm and secret key.
     * //3. According to JWS Compact Serialization
     * (https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     * //   compaction of the JWT to a URL-safe string
     *
     * @param claims  - the claims that will be stored in the token.
     * @param subject - the subject of the token.
     * @param userId  - the id of the user that wil lbe stored in the token.
     * @return returns a string that represents the token.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private String doGenerateToken(final Map<String, Object> claims, final String subject, final String userId) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .setId(userId).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * Function that validates the token.
     *
     * @param token       - string containing the token that needs to be validated.
     * @param userDetails - UserDetails containing the user.
     * @return true if token is valid or false if the token is invalid.
     */
    public Boolean validateToken(final String token, final UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token))
                && !isUsed(token);
    }

    /**
     * Validates a token.
     * @param token - Token.
     * @return True or false.
     */
    public Boolean isTokenValid(final String token) {
        System.out.println(isTokenExpired(token));
        System.out.println(isUsed(token));
        return !isTokenExpired(token) && !isUsed(token);
    }


    boolean isUsed(final String token) {
        if(this.invalidatedTokens.containsKey(getUsernameFromToken(token))) {
            return this.invalidatedTokens.get(getUsernameFromToken(token)).contains(token);
        }
        return false;
    }

    /**
     * Invalidate a token after using it.
     * @param token - Token.
     */
    public void invalidateToken(final String token) {
        if (this.invalidatedTokens.containsKey(this.getUsernameFromToken(token))) {
            this.invalidatedTokens.get(this.getUsernameFromToken(token)).add(token);
        } else {
            this.invalidatedTokens.putIfAbsent(this.getUsernameFromToken(token), List.of(token));
        }
    }
}

