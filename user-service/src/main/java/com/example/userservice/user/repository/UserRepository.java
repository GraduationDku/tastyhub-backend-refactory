package com.example.userservice.user.repository;

import com.example.userservice.user.dtos.UserDto;
import com.example.userservice.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);

    Optional<User> findByAppleId(String appleId);
    Optional<User> findByUserName(String userName);

}
