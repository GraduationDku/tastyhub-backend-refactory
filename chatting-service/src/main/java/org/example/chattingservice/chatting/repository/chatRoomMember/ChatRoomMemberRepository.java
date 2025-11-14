package org.example.chattingservice.chatting.repository.chatRoomMember;

import org.example.chattingservice.chatting.entity.ChatRoom;
import org.example.chattingservice.chatting.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    boolean existsByChatRoomAndUsername(ChatRoom chatRoom, String username);
    Optional<ChatRoomMember> findByChatRoomAndUsername(ChatRoom chatRoom, String username);
}
