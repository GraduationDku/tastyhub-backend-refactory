package org.example.chattingservice.chatting.service.chat;

import lombok.RequiredArgsConstructor;
import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.chattingservice.chatting.entity.Chat;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.example.chattingservice.chatting.entity.ChatRoomMember;
import org.example.chattingservice.chatting.repository.chat.ChatRepository;
import org.example.chattingservice.chatting.repository.chatRoom.ChatRoomRepository;
import org.example.chattingservice.chatting.repository.chatRoomMember.ChatRoomMemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    @Override
    public ChatDto sendMessage(Long roomId, ChatDto chatDto, String username) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방은 존재하지않습니다"));
        if(chatRoomMemberRepository.existsByChatRoomAndUsername(chatRoom, username)){
            throw new IllegalArgumentException("채팅방에 참여 중인 사용자만 전송 가능합니다.");
        }
        String nickname = ""; // 후에 동기 방식으로 닉네임을 요청, 캐시하여 처리
        Chat chat = Chat.createChat(chatDto, chatRoom, username,nickname);
        chatRepository.save(chat);
        return chatDto;
    }
}
