package org.example.jwt;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtils.resolveAccessToken(request);
        if (!Objects.equals(accessToken,"") && jwtUtils.isTokenValid(accessToken)) {
            Claims userInfoFromToken = jwtUtils.getUserInfoFromToken(accessToken);
            String username = userInfoFromToken.getSubject();
            String role = String.valueOf(userInfoFromToken.get("auth"));
            setAuthentication(username,role);
        }
        try {
            filterChain.doFilter(request, response);}
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void setAuthentication(String username,String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createSafeAuthentication(username,role);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createSafeAuthentication(String username,String role) {
        try{
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new  UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }catch (Exception e){
            String authority = mapRoleToAuthority(role);
            return new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(authority)));
        }
    }

    private  String mapRoleToAuthority(String role) {
        if (role == null) {
            return "COMMON";
        }
        return switch (role.toUpperCase()) {
            case "ADMIN" -> "ADMIN";
            default -> "COMMON";

        };
    }
}