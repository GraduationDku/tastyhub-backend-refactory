package com.example.userservice.saga;

public enum UserDeletionSagaStatus {

    IN_PROGRESS,
    FAILED,
    COMPENSATING,
    COMPENSATED,
    COMPLETED,
    COMPENSATION_FAILED
}
