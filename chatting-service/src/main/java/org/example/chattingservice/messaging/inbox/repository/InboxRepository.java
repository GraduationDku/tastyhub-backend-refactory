package org.example.chattingservice.messaging.inbox.repository;

import org.example.chattingservice.messaging.inbox.entity.InboxEvent;
import org.example.chattingservice.messaging.inbox.entity.InboxId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<InboxEvent, InboxId> {
}
