package com.gm.hrms.config;

import com.gm.hrms.entity.UserAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserAuth userAuth;

    public Long getUserId() {
        return userAuth.getPersonalInformation().getId();
    }

    public String getRole() {
        return userAuth.getRole().name();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + userAuth.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return userAuth.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return userAuth.getUsername();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !userAuth.getAccountLocked();
    }

    @Override
    public boolean isEnabled() {
        return userAuth.getActive();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}