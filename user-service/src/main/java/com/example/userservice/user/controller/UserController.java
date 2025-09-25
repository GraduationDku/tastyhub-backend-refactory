package com.example.userservice.user.controller;

import com.example.userservice.user.dtos.UserDto;
import com.example.userservice.user.service.UserService;
import com.example.userservice.utils.auth.userDetails.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.example.headers.StatusResponse;
import static org.example.headers.HttpResponseEntity.RESPONSE_OK;
import static org.example.headers.HttpResponseEntity.INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    // 프론트엔드에서 authorization_code를 받아오는 API
    @PostMapping("/login/oauth2/code/apple")
    @ResponseBody
    public StatusResponse appleLogin(@RequestBody Map<String, String> requestBody, HttpServletResponse response) {
        String code = requestBody.get("code");
        userService.appleLogin(code, response);
        return new StatusResponse(200, "요청 성공");
    }

    @PostMapping("/refresh")
    @ResponseBody
    public StatusResponse refreshAccessToken(
            @RequestParam String nickName, HttpServletResponse response) {
        userService.refreshAccessToken(nickName, response);
        return new StatusResponse(200, "요청 성공");
    }


    /**
     * 사용자 검색하기 - skyriv213
     */

    @GetMapping("/search/list")
    public ResponseEntity<Page<UserDto>> getUserList(
            @RequestParam String nickname,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserDto> userDtoList = userService.getUserList(nickname, pageable);
        return ResponseEntity.ok().body(userDtoList);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<StatusResponse> delete(@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        try {
            boolean result = userService.delete(userDetails.getUser());

            // 3. 결과에 따른 응답 생성
            if (result) {
                return ResponseEntity.ok()
                        .body((StatusResponse) Map.of(
                                "status", "success",
                                "message", "회원탈퇴가 완료되었습니다"
                        ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body((StatusResponse) Map.of(
                                "status", "error",
                                "message", "회원탈퇴 처리 중 오류가 발생했습니다"
                        ));
            }
        } catch (Exception e) {
            // 기타 서버 오류
            log.error("회원탈퇴 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body((StatusResponse) Map.of(
                            "status", "error",
                            "message", "서버 오류가 발생했습니다"
                    ));
        }

    }

    @PostMapping("/user/logout")
    public ResponseEntity<StatusResponse> logout(@AuthenticationPrincipal UserDetailsImpl user) {
        try {
            // 사용자 객체가 null인지 확인
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((StatusResponse) Map.of(
                        "status", "error",
                        "message", "인증되지 않은 사용자입니다."
                ));
            }

            boolean result = userService.logout(user.getUser());

            // 성공/실패에 따른 응답 구분
            if (result) {
                return ResponseEntity.ok().body((StatusResponse) Map.of(
                        "status", "success",
                        "message", "로그아웃 처리되었습니다."
                ));
            } else {
                return ResponseEntity.ok().body((StatusResponse) Map.of(
                        "status", "warning",
                        "message", "이미 로그아웃 되었거나 세션이 만료되었습니다."
                ));
            }
        } catch (Exception e) {
            // 예외 발생 시 로그 기록 및 오류 응답
            log.error("로그아웃 처리 중 예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body((StatusResponse) Map.of(
                    "status", "error",
                    "message", "서버 오류로 로그아웃 처리에 실패했습니다."
            ));
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @PatchMapping(value = "/modify/information")
    public ResponseEntity<StatusResponse> updateUserInfo(@RequestPart("img") MultipartFile img,
                                                         @RequestPart("data") String newNickname,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        userService.updateUserInfoByUserUpdateRequest(newNickname, img, userDetails.getUser());
        return RESPONSE_OK;
    }


}
