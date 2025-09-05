package com.example.userservice.user.service;

import com.example.userservice.user.dtos.UserDto;
import com.example.userservice.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    boolean checkDuplicatedNickname(String nickname);

//    void delete(UserAuthRequest deleteRequest, User user);

    void appleLogin(String code, HttpServletResponse response);

    void updateUserInfoByUserUpdateRequest(String newNickname, MultipartFile img, User user);

    void refreshAccessToken(String nickName, HttpServletResponse response);

    Page<UserDto> getUserList(String nickname, Pageable pageable);

    boolean delete(User user);

    boolean logout(User user);
}
