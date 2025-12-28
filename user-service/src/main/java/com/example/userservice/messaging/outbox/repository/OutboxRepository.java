package com.example.userservice.messaging.outbox.repository;

import com.example.userservice.messaging.outbox.entity.OutboxEvent;
import com.example.userservice.messaging.outbox.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAt(OutboxStatus outboxStatus);
    List<OutboxEvent> findTop100ByStatusInAndNextAttemptAtBefore(List<OutboxStatus> statuses, Instant now);


}
