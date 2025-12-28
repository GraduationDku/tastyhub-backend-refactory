package org.example.chattingservice.saga;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.example.chattingservice.chatting.entity.ChatRoomMember;
import org.example.chattingservice.chatting.repository.chatRoom.ChatRoomRepository;
import org.example.chattingservice.chatting.repository.chatRoomMember.ChatRoomMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeletionStepService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    // TODO: 또는 QueryDSL 기반 bulk update/delete 추가

    @Transactional
    public void softDelete(String username) {
        // TODO: recipes/likes를 soft delete (deleted flag or archived)
        List<ChatRoomMember> allByUsername = chatRoomMemberRepository.findAllByUsername(username);
        for (ChatRoomMember chatRoomMember : allByUsername) {
            chatRoomMember.softDeleted();
        }

    }

    @Transactional
    public void compensate(String username) {
        // TODO: soft delete 복구
        List<ChatRoomMember> allByUsername = chatRoomMemberRepository.findAllByUsername(username);
        for (ChatRoomMember chatRoomMember : allByUsername) {
            chatRoomMember.sofDeletedRollBack();

        }
    }

    @Transactional
    public void hardDelete(String username) {
        // TODO: 완전 삭제 (completed 이후)
        List<ChatRoomMember> allByUsername = chatRoomMemberRepository.findAllByUsername(username);
        for (ChatRoomMember chatRoomMember : allByUsername) {
            if (chatRoomMember.isDeleted()) {
                chatRoomMemberRepository.delete(chatRoomMember);
            }
        }
    }

}
