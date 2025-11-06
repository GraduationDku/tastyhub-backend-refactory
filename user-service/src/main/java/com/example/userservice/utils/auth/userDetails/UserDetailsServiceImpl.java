package com.example.userservice.utils.auth.userDetails;

import com.example.userservice.user.entity.User;
import com.example.userservice.user.repository.UserRepository;
import com.example.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        return new UserDetailsImpl(user, java.util.Collections.emptyMap());
    }

}
