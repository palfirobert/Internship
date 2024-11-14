package com.example.java.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public final class BatteryUserPrincipal implements UserDetails {
    /**
     * User that is wrapped around by the DTO.
     */
    private final BatteryUser user;

    /**
     * @param userInstance - User that will be wrapped around with this DTO.
     */
    public BatteryUserPrincipal(final BatteryUser userInstance) {
        this.user = userInstance;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Function that returns the if of the user.
     *
     * @return - Integer - user id.
     */
    public Integer getUserId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
