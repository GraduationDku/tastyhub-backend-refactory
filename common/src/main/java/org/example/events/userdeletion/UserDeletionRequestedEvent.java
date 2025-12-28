package org.example.events.userdeletion;

import java.time.Instant;
import java.util.UUID;

public record UserDeletionRequestedEvent(
        UUID messageId,
        UUID sagaId,
        Instant occurredAt,
        String username
) {
    public static UserDeletionRequestedEvent create(UUID messageId, UUID sagaId, Instant occurredAt, String username) {
        return new  UserDeletionRequestedEvent(messageId, sagaId, occurredAt, username);
    }
}
