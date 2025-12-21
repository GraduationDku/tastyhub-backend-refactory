package com.example.recipeservice.messaging.outbox.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(name = "idx_outbox_status", columnList = "status"),
                @Index(name = "idx_outbox_created_at", columnList = "created_at"),
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "topic", nullable = false, length = 200)
    private String topic;

    @Column(name = "message_key", length = 200)
    private String messageKey;

    @Column(nullable = false, length = 200)
    private String eventType;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_tried_at", nullable = false)
    private Instant lastTriedAt;
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (status == null) {
            status = OutboxStatus.PENDING;
        }
        if(createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.lastTriedAt = Instant.now();
    }

    public void markFailed() {
        this.retryCount += 1;
        this.lastTriedAt = Instant.now();
        this.status = OutboxStatus.FAILED;
    }

}
