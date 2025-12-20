package org.example.events.userdeletion;

import java.time.Instant;
import java.util.UUID;

public record UserDeletionCompensationResultEvent(
        UUID messageId,
        UUID sagaId,
        Instant occurredAt,
        String username,
        UserDeletionStep step,
        UserDeletionCompensationStatus compStatus,
        String reason
) {
    public static UserDeletionCompensationResultEvent create(
            UUID messageId,
            UUID sagaId,
            Instant occurredAt,
            String username,
            UserDeletionStep step,
            UserDeletionCompensationStatus compStatus,
            String reason
            ) {
        return new UserDeletionCompensationResultEvent(messageId, sagaId, occurredAt, username, step, compStatus, reason);
    }
}
