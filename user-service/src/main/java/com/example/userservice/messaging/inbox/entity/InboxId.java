package com.example.userservice.messaging.inbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InboxId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "messageId", nullable = false)
    private UUID messageId;

    @Column(name = "consumer", nullable = false, length = 200)
    private String consumer;

}
