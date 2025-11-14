package org.example.chattingservice.chatting.repository.chatRoom;

import org.example.chattingservice.chatting.dtos.ChatRoomDto;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChatRoomRepositoryQuery {
//    List<ChatRoomDto> findByUsername(String username);

    Page<ChatRoomDto> findAllByMemberUsername(String username, Pageable pageable);

//    Optional<ChatRoom> lockById(Long id);

}
