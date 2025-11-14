package org.example.chattingservice.chatting.repository.chat;

import org.example.chattingservice.chatting.entity.Chat;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
}
