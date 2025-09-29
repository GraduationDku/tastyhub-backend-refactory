package org.example.jwt;

import com.example.userservice.utils.auth.userDetails.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtils.resolveAccessToken(request);
        if (!Objects.equals(accessToken,"") && jwtUtils.isTokenValid(accessToken)) {
            Claims userInfoFromToken = jwtUtils.getUserInfoFromToken(accessToken);
            String username = userInfoFromToken.getSubject();
            String role = userInfoFromToken.get("auth").toString();
            setAuthentication(username,role);
        }
        try {
            filterChain.doFilter(request, response);}
        catch (Exception e) {
            log.error(e.getMessage());
        }
//        } finally {
//            MDC.remove("userId");
//        }
    }

    public void setAuthentication(String username,String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication;
        if(role.equals("Admin")){
            authentication = this.createAdminAuthentication(username,role);
        }else{
            authentication = this.createAuthentication(username,role);
        }
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        String usernameForMDC = userDetails.getUsername();
//        MDC.put("userID", usernameForMDC);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public Authentication createAuthentication(String username, String role) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, role, userDetails.getAuthorities());
    }
    public Authentication createAdminAuthentication(String username, String role) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, role, userDetails.getAuthorities());
    }
}