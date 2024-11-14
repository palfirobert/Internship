package com.example.java.filters;

import com.example.java.service.UserDetailServiceDB;
import com.example.java.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public final class JwtSecurityFilter extends OncePerRequestFilter {

    /**
     * The point where the token starts in the authorization header.
     */
    private static final int CUTTINGPOINT = 7;

    /**
     * Object that helps with the interaction with the jwt token.
     */
    private final JwtTokenUtils tokenUtils;

    /**
     * Object thaT MANIPULATES THE USERS OF THE APP.
     */
    private final UserDetailServiceDB userDetailServiceDB;

    /**
     * @param tokenUtilsInstance          - JwtTokenUtils used to work with the token.
     * @param userDetailServiceDBInstance - UserDetailServiceDb - used to interact with the UserDetailService.
     */
    @Autowired
    public JwtSecurityFilter(final JwtTokenUtils tokenUtilsInstance,
                             final UserDetailServiceDB userDetailServiceDBInstance) {
        this.tokenUtils = tokenUtilsInstance;
        this.userDetailServiceDB = userDetailServiceDBInstance;
    }

    /**
     * Function that dose the filtering.
     *
     * @param request  - the request that will be filtered.
     * @param response - the response that will be given.
     * @param chain    - the filter chain that will be used.
     * @throws ServletException -
     * @throws IOException      -
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    @NonNull final HttpServletResponse response,
                                    @NonNull final FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(CUTTINGPOINT);
            try {
                username = tokenUtils.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.warn("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {

                logger.warn("JWT Token has expired");
            }
        } else {
            logger.warn("JWT Token with Bearer String");
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailServiceDB.loadUserByUsername(username);

            // if token is valid configure Spring Security to manually set
            // authentication
            if (Boolean.TRUE.equals(tokenUtils.validateToken(jwtToken, userDetails))) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }

}
