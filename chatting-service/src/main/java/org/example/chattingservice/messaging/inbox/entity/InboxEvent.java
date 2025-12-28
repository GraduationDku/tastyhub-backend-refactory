package org.example.chattingservice.messaging.inbox.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "inbox_events")
@NoArgsConstructor
public class InboxEvent {
    @EmbeddedId
    private InboxId id;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    public InboxEvent(InboxId inboxId) {
        this.id = inboxId;
    }

    @PrePersist
    void onCreate() {
        if (receivedAt == null) this.receivedAt = Instant.now();
    }
}
