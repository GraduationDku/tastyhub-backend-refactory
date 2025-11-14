package org.example.chattingservice.chatting.service.chat;

import org.example.chattingservice.chatting.dtos.ChatDto;

public interface ChatService {
    ChatDto sendMessage(Long roomId, ChatDto chatDto, String username);
}
