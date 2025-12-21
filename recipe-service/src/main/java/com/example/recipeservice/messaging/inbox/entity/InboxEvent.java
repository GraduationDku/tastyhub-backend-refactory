package com.example.recipeservice.messaging.inbox.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inbox_events")

public class InboxEvent {
    @EmbeddedId
    private InboxId id;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @PrePersist
    void onCreate() {
        if (receivedAt == null) this.receivedAt = Instant.now();
    }
}
