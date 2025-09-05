package com.example.userservice.user.repository;

import com.example.userservice.user.dtos.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryQuery {
    Page<UserDto> findAllByNickname(String nickname, Pageable pageable);

}
