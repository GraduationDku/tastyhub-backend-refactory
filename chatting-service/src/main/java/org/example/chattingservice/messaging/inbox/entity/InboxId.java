package org.example.chattingservice.messaging.inbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class InboxId {
    @Column(name = "messageId", nullable = false)
    private UUID messageId;

    @Column(name = "consumer", nullable = false, length = 200)
    private String consumer;
}
