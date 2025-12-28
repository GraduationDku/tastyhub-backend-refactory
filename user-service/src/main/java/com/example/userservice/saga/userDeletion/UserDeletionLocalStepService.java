package com.example.userservice.saga.userDeletion;

import com.example.userservice.user.entity.User;
import com.example.userservice.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeletionLocalStepService {

    private final UserRepository userRepository;

    @Transactional
    public void softDelete(String username) {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new IllegalArgumentException("Username not found: " + username));
        user.softDelete();
    }

    @Transactional
    public void compensation(String username) {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new IllegalArgumentException("Username not found: " + username));
        user.restoreDeleted();
    }

    @Transactional
    public void hardDelete(String username) {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new IllegalArgumentException("Username not found: " + username));
        if (user.isDeleted()) {
            userRepository.delete(user);
        }
    }
}
