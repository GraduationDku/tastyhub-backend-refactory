
package com.example.userservice.user.service;

import com.example.userservice.user.entity.User;
import com.example.userservice.user.repository.UserRepository;
import com.example.userservice.utils.auth.apple.AppleAuthService;
import com.example.userservice.utils.auth.apple.AppleTokenResponse;
import com.example.userservice.utils.auth.apple.AppleUserInfo;
import com.example.userservice.utils.nickName.NicknameGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.example.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NicknameGenerator nicknameGenerator;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AppleAuthService appleAuthService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .appleId("testAppleId")
                .userType(User.UserType.COMMON)
                .build();
    }

    @Test
    @DisplayName("닉네임 중복 확인 테스트")
    void checkDuplicatedNickname() {
        given(userRepository.existsByNickname("newuser")).willReturn(false);
        assertFalse(userService.checkDuplicatedNickname("newuser"));

        given(userRepository.existsByNickname("testuser")).willReturn(true);
        assertTrue(userService.checkDuplicatedNickname("testuser"));
    }

    @Test
    @DisplayName("애플 로그인 - 기존 사용자")
    void appleLogin_existingUser() {
        AppleTokenResponse tokenResponse = new AppleTokenResponse("access_token", 3600L, "id_token", "refresh_token", "Bearer");
        AppleUserInfo userInfo = new AppleUserInfo("testAppleId", "test@example.com", "true");

        given(appleAuthService.getAppleToken(anyString())).willReturn(tokenResponse);
        given(appleAuthService.getAppleUserInfo(anyString())).willReturn(userInfo);
        given(userRepository.findByAppleId("testAppleId")).willReturn(Optional.of(testUser));

        HttpServletResponse response = mock(HttpServletResponse.class);
        userService.appleLogin("test_code", response);

        verify(jwtUtils).createAccessToken(testUser.getNickname(), testUser.getUserType().toString());
        verify(jwtUtils).createRefreshToken(testUser.getNickname(), String.valueOf(testUser.getUserType().toString()));
    }

    @Test
    @DisplayName("애플 로그인 - 신규 사용자")
    void appleLogin_newUser() {
        AppleTokenResponse tokenResponse = new AppleTokenResponse("access_token", 3600L, "id_token", "refresh_token", "Bearer");
        AppleUserInfo userInfo = new AppleUserInfo("newAppleId", "new@example.com", "true");

        given(appleAuthService.getAppleToken(anyString())).willReturn(tokenResponse);
        given(appleAuthService.getAppleUserInfo(anyString())).willReturn(userInfo);
        given(userRepository.findByAppleId("newAppleId")).willReturn(Optional.empty());
        given(nicknameGenerator.generate(anyString())).willReturn("newuser");
        given(userRepository.existsByNickname("newuser")).willReturn(false);

        HttpServletResponse response = mock(HttpServletResponse.class);
        userService.appleLogin("test_code", response);

        verify(userRepository).save(any(User.class));
        verify(jwtUtils).createAccessToken(anyString(), any());
        verify(jwtUtils).createRefreshToken(anyString(), any());
    }

    @Test
    @DisplayName("액세스 토큰 재발급 - 성공")
    void refreshAccessToken_success() {
        given(userRepository.findByNickname("testuser")).willReturn(Optional.of(testUser));
        given(jwtUtils.getRefreshToken(testUser.getUserName())).willReturn("valid_refresh_token");
        given(jwtUtils.isTokenValid("valid_refresh_token")).willReturn(true);

        given(jwtUtils.createAccessToken(testUser.getNickname(), testUser.getUserType().toString())).willReturn("new_access_token");

        HttpServletResponse response = mock(HttpServletResponse.class);
        userService.refreshAccessToken("testuser", response);

        verify(response).setHeader("Authorization", "new_access_token");
    }

    @Test
    @DisplayName("액세스 토큰 재발급 - 실패 (유효하지 않은 리프레시 토큰)")
    void refreshAccessToken_invalidToken() {
        given(userRepository.findByNickname("testuser")).willReturn(Optional.of(testUser));
        given(jwtUtils.getRefreshToken(testUser.getUserName())).willReturn("invalid_refresh_token");
        given(jwtUtils.isTokenValid("invalid_refresh_token")).willReturn(false);

        HttpServletResponse response = mock(HttpServletResponse.class);
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.refreshAccessToken("testuser", response);
        });
    }

    @Test
    @DisplayName("회원 탈퇴 - 성공")
    void deleteUser_success() {
        assertTrue(userService.delete(testUser));
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    void logout_success() {
        given(jwtUtils.deleteRefreshToken(testUser.getUserName())).willReturn(true);
        assertTrue(userService.logout(testUser));
    }
}
