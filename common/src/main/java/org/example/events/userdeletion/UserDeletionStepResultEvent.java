package org.example.events.userdeletion;

import java.time.Instant;
import java.util.UUID;

public record UserDeletionStepResultEvent(
        UUID messageId,
        UUID sagaId,
        Instant occurredAt,
        String username,
        UserDeletionStep step,
        UserDeletionStepStatus stepStatus,
        String reason
) {

    public static UserDeletionStepResultEvent create(
            UUID messageId,
            UUID sagaId,
            Instant occurredAt,
            String username,
            UserDeletionStep step,
            UserDeletionStepStatus stepStatus,
            String reason) {
        return new UserDeletionStepResultEvent(
                messageId, sagaId, occurredAt, username, step, stepStatus, reason);

    }
}
