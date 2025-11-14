package org.example.chattingservice.chatting.service.chatRoom;

import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.chattingservice.chatting.dtos.ChatRoomCreateDto;
import org.example.chattingservice.chatting.dtos.ChatRoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRoomService {
    void createChatRoom(String username, ChatRoomCreateDto chatRoomDto);

    Page<ChatRoomDto> getChatRoomList(String username, Pageable pageable);

    List<ChatDto> getChatRoom(Long roomId, String username);

    void enterNewChatRoom(Long roomId, String username);

    void outChatRoom(Long roomId, String username);

    void deleteChatRoom(Long roomId, String username);
}
