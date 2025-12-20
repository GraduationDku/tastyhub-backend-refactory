package org.example.events.userdeletion;

import java.time.Instant;
import java.util.UUID;

public record UserDeletionCompletedEvent(
        UUID messageId,
        UUID sagaId,
        Instant occurredAt,
        String username

) {
    public static UserDeletionCompletedEvent create(
            UUID messageId,
            UUID sagaId,
            Instant occurredAt,
            String username
    ) {
        return new UserDeletionCompletedEvent(messageId, sagaId, occurredAt, username);
    }
}
