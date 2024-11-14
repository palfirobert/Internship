package com.example.java.configurations;

import com.example.java.filters.JwtSecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    /**
     * The filter used for security.
     */
    private final JwtSecurityFilter securityFilter;


    /**
     * @param securityFilterInstance - Filter that will be used for the security.
     */
    @Autowired
    public SecurityConfiguration(final JwtSecurityFilter securityFilterInstance) {
        this.securityFilter = securityFilterInstance;
    }

    /**
     * Function that creates the filter chain.
     *
     * @param http - HttpSecurity.
     * @return Returns the filter chain used for security.
     * @throws Exception - In some cases.
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()//the most important line from all this code
                .antMatchers("/admin/**")
                .hasRole("ADMIN")
                .antMatchers("/anonymous*")
                .anonymous()
                .antMatchers("/api/v1/authentication/login*")
                .permitAll()
                .antMatchers("/api/v1/authentication/register*")
                .permitAll()
                .antMatchers("/api/v1/authentication/verify*")
                .permitAll()
                .antMatchers("/api/v1/authentication/request-reset-password*")
                .permitAll()
                .antMatchers("/api/v1/authentication/reset-password*")
                .permitAll()
                .antMatchers("/api/v1/authentication/logout*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Function that returns the password encoder used to encode well passwords.
     *
     * @return - BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
