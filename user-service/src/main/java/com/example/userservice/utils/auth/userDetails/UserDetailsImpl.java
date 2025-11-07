package com.example.userservice.utils.auth.userDetails;

import com.example.userservice.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Map<String, Collection<? extends GrantedAuthority>> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getUserType() == User.UserType.ADMIN ? "ADMIN" : "COMMON";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword() == null ? "" : user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    public User getUser() {
        return user;
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
