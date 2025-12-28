package com.example.recipeservice.messaging.inbox.repository;


import com.example.recipeservice.messaging.inbox.entity.InboxEvent;
import com.example.recipeservice.messaging.inbox.entity.InboxId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxRepository extends JpaRepository<InboxEvent, InboxId> {
}
