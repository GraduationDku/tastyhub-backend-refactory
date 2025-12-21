package org.example.chattingservice.messaging.outbox.repository;


import org.example.chattingservice.messaging.outbox.entity.OutboxEvent;
import org.example.chattingservice.messaging.outbox.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAt(OutboxStatus outboxStatus);
}
