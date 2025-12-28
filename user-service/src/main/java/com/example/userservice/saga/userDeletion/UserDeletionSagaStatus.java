package com.example.userservice.saga.userDeletion;

public enum UserDeletionSagaStatus {

    IN_PROGRESS,
    FAILED,
    COMPENSATING,
    COMPENSATED,
    COMPLETED,
    COMPENSATION_FAILED
}
