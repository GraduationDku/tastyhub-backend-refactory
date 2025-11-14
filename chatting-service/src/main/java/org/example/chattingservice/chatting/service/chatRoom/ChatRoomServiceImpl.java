package org.example.chattingservice.chatting.service.chatRoom;

import lombok.RequiredArgsConstructor;
import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.chattingservice.chatting.dtos.ChatRoomCreateDto;
import org.example.chattingservice.chatting.dtos.ChatRoomDto;
import org.example.chattingservice.chatting.entity.Chat;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.example.chattingservice.chatting.entity.ChatRoomMember;
import org.example.chattingservice.chatting.repository.chatRoom.ChatRoomRepository;
import org.example.chattingservice.chatting.repository.chat.ChatRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.example.chattingservice.chatting.repository.chatRoomMember.ChatRoomMemberRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    @Transactional
    public void createChatRoom(String username, ChatRoomCreateDto chatRoomDto) {

        ArrayList<String> userNames = new ArrayList<>();
        // user service 호출 부분, username을 기반으로 nickname 호출
        String nickname="";

        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomDescription(chatRoomDto.getChatRoomDescription())
                .roomName(chatRoomDto.getRoomName())
                .build();


        chatRoom.addUser(username, nickname);

        chatRoomRepository.save(chatRoom);

    }

    @Override
    @Transactional
    public Page<ChatRoomDto> getChatRoomList(String username, Pageable pageable) {
        Page<ChatRoomDto> userChatRooms = chatRoomRepository.findAllByMemberUsername(username,pageable);


        return userChatRooms;
    }

    @Override
    @Transactional
    public List<ChatDto> getChatContent(Long roomId, String username) {
        ChatRoom chatRoom = findChatRoomById(roomId);
        if(!chatRoomMemberRepository.existsByChatRoomAndUsername(chatRoom, username)){
            try{
                String nickname = "";
                ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                        .chatRoom(chatRoom)
                        .username(username)
                        .nicknameSnapshot(nickname)
                        .build();
                chatRoomMemberRepository.save(chatRoomMember);
            }catch (DataIntegrityViolationException e){}
        }
        List<Chat> chats=
                chatRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
        return chats.stream().map(ChatDto::new).toList();
    }

    @Override
    @Transactional
    public void enterNewChatRoom(Long roomId, String username) {
        ChatRoom chatRoom = findChatRoomById(roomId);
        // user service 호출 부분, username을 기반으로 nickname 호출
        String nickname="";
        chatRoom.addUser(username, username);
    }

    @Override
    @Transactional
    public void outChatRoom(Long roomId, String username) {
        ChatRoom chatRoom = findChatRoomById(roomId);
        if(!chatRoom.hasMember(username)) {
            throw new IllegalArgumentException("해당 유저는 존재하지 않습니다");
        }
        chatRoom.deleteUser(username);
    }

    @Override
    @Transactional
    public void deleteChatRoom(Long roomId, String username) {

        ChatRoom chatRoom = findChatRoomById(roomId);
        if (chatRoom.getMembers().size() == 1 ) {
            chatRoomRepository.delete(chatRoom);
        }

    }

    private ChatRoom findChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("현재 채팅방은 존재하지 않습니다."));
    }
}
