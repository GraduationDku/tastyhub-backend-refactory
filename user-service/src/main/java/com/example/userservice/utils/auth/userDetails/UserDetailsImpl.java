package com.example.userservice.utils.auth.userDetails;

import com.example.userservice.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
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
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    public User getUser() {
        return user;
    }
}
