package org.example.chattingservice.chatting.service.chat;

import lombok.RequiredArgsConstructor;
import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.chattingservice.chatting.entity.Chat;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.example.chattingservice.chatting.repository.chat.ChatRepository;
import org.example.chattingservice.chatting.repository.chatRoom.ChatRoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatDto sendMessage(Long roomId, ChatDto chatDto, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방은 존재하지않습니다"));
        Chat chat = Chat.createChat(chatDto, chatRoom, username);
        chatRepository.save(chat);
        return chatDto;
    }
}
