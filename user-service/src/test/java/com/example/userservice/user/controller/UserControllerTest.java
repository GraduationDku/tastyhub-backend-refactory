package com.example.userservice.user.controller;

import com.example.userservice.config.SecurityConfig;
import com.example.userservice.user.dtos.UserDto;
import com.example.userservice.user.entity.User;
import com.example.userservice.user.service.UserService;
import com.example.userservice.utils.auth.userDetails.UserDetailsImpl;
import com.example.userservice.utils.auth.userDetails.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jwt.JwtAuthFilter;
import org.example.jwt.JwtAuthenticationEntryPoint;
import org.example.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User testUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .userType(User.UserType.COMMON)
                .build();
        userDetails = new UserDetailsImpl(testUser, java.util.Collections.emptyMap());
    }

    @Test
    @DisplayName("애플 로그인 테스트")
    void appleLogin() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("code", "test_code");

        doNothing().when(userService).appleLogin(anyString(), any());

        mockMvc.perform(post("/login/oauth2/code/apple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("요청 성공"));
    }

    @Test
    @DisplayName("액세스 토큰 재발급 테스트")
    void refreshAccessToken() throws Exception {
        doNothing().when(userService).refreshAccessToken(anyString(), any());

        mockMvc.perform(post("/refresh")
                        .param("nickName", "testuser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("요청 성공"));
    }

    @Test
    @DisplayName("사용자 목록 검색 테스트")
    @WithMockUser
    void getUserList() throws Exception {
        UserDto userDto = new UserDto(testUser.getNickname(), testUser.getUserImg());
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserDto> page = new PageImpl<>(Collections.singletonList(userDto), pageable, 1);

        given(userService.getUserList(eq("test"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/search/list")
                        .param("nickname", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].nickName").value("testuser"));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    @WithMockUser
    void deleteUser() throws Exception {
        given(userService.delete(any(User.class))).willReturn(true);

        mockMvc.perform(delete("/delete")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("회원탈퇴가 완료되었습니다"));
    }

    @Test
    @DisplayName("로그아웃 테스트")
    @WithMockUser
    void logout() throws Exception {
        given(userService.logout(any(User.class))).willReturn(true);

        mockMvc.perform(post("/user/logout")
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("로그아웃 처리되었습니다."));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
            return new MappingJackson2HttpMessageConverter();
        }
    }
}