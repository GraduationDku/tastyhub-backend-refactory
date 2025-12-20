package org.example.events.userdeletion;

import java.time.Instant;
import java.util.UUID;

public record UserDeletionFailedEvent(
        UUID messageId,
        UUID sagaId,
        Instant occurredAt,
        String username,
        UserDeletionStep failedStep,
        String reason
) {
    public static UserDeletionFailedEvent create(
            UUID messageId,
            UUID sagaId,
            Instant occurredAt,
            String username,
            UserDeletionStep failedStep,
            String reason
    ) {
        return new UserDeletionFailedEvent(messageId, sagaId, occurredAt, username, failedStep, reason);
    }
}
