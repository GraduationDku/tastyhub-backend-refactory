package org.example.chattingservice.chatting.repository.chat;

import org.example.chattingservice.chatting.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
