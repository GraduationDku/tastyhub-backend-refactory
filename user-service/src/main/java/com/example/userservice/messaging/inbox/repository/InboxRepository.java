package com.example.userservice.messaging.inbox.repository;

import com.example.userservice.messaging.inbox.entity.InboxEvent;
import com.example.userservice.messaging.inbox.entity.InboxId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<InboxEvent, InboxId> {
}
