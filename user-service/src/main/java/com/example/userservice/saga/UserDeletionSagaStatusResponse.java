package com.example.userservice.saga;

import com.example.userservice.saga.userDeletion.UserDeletionSagaStatus;

import java.util.UUID;

public record UserDeletionSagaStatusResponse(UUID sagaId, String username, UserDeletionSagaStatus status) {}
