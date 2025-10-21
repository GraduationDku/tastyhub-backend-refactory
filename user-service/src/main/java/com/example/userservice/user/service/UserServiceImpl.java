package com.example.userservice.user.service;

import com.example.userservice.user.dtos.UserDto;
import com.example.userservice.user.entity.User;
import com.example.userservice.user.repository.UserRepository;
import com.example.userservice.utils.auth.apple.AppleAuthService;
import com.example.userservice.utils.auth.apple.AppleTokenResponse;
import com.example.userservice.utils.auth.apple.AppleUserInfo;
import com.example.userservice.utils.nickName.NicknameGenerator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwt.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;
    private final JwtUtils jwtUtils;
    // s3용 이미지 저장 추가
    private final AppleAuthService appleAuthService;


    @Override
    public boolean checkDuplicatedNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    @Transactional
    public void appleLogin(String code, HttpServletResponse response) {
        // 1. 코드를 사용해 Apple 로부터 토큰 받기
        AppleTokenResponse tokenResponse = appleAuthService.getAppleToken(code);

        // 2. id_token에서 사용자 정보 추출
        AppleUserInfo userInfo = appleAuthService.getAppleUserInfo(tokenResponse.idToken());

        // 3. 사용자 정보로 DB 조회
        Optional<User> existingUser = userRepository.findByAppleId(userInfo.sub());

        User user;
        if (existingUser.isPresent()) {
            // 4a. 기존 사용자가 있으면 로그인 처리
            user = existingUser.get();
        } else {
            // 4b. 신규 사용자면 회원가입
            String nickname = generateUniqueNickname(userInfo.email());
            user = User.createAppleUser(userInfo.email(), userInfo.sub(), nickname);
            userRepository.save(user);
        }

        // 5. 우리 서비스의 JWT 생성 및 응답 헤더에 추가
        String accessToken = jwtUtils.createAccessToken(user.getNickname(), user.getUserType().toString());
        jwtUtils.createRefreshToken(user.getNickname(), String.valueOf(user.getUserType()));
        response.addHeader("Authorization", accessToken);

    }

    private String generateUniqueNickname(String email) {
        String baseNickname = email.split("@")[0];
        int tryCheck = 0;
        int maxTry = 10;

        while (tryCheck < maxTry) {
            String nickname = nicknameGenerator.generate(baseNickname);
            if (!userRepository.existsByNickname(nickname)) {
                return nickname;
            }
            tryCheck++;
        }
        return baseNickname + String.valueOf(tryCheck);
    }

    @Override
    @Transactional
    public void updateUserInfoByUserUpdateRequest(String newNickname, MultipartFile img, User user) {
        if (!userRepository.existsByNickname(newNickname)) {
            throw new UsernameNotFoundException("Duplicated Nickname");
        }
        ;
        String imgUrl = ""; // 로컬 이미지 처리기 추가
        user.updateUserInfo(newNickname, imgUrl);
    }

    @Override
    public void refreshAccessToken(String nickName, HttpServletResponse response) {
        User user = userRepository.findByNickname(nickName).orElseThrow(() -> new UsernameNotFoundException("Nickname not found"));
        String refreshToken = jwtUtils.getRefreshToken(user.getUserName());
        if (jwtUtils.isTokenValid(refreshToken)) {
            response.setHeader("Authorization", jwtUtils.createAccessToken(user.getNickname(), user.getUserType().toString()));
        } else {
            throw new UsernameNotFoundException("Invalid refresh token");
        }
    }

    @Override
    public Page<UserDto> getUserList(String nickname, Pageable pageable) {
        return userRepository.findAllByNickname(nickname,pageable);
    }

    @Override
    public boolean delete(User user) {
        try {
            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            log.error("회원탈퇴 처리 중 예외 발생: {}", e.getMessage(), e);

            return false;
        }
    }

    @Override
    public boolean logout(User user) {
        return jwtUtils.deleteRefreshToken(user.getUserName());
    }

}

