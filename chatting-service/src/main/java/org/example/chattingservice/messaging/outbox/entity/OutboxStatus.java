package org.example.chattingservice.messaging.outbox.entity;

public enum OutboxStatus {
    PENDING,
    SENT,
    FAILED,
    DEAD

}
