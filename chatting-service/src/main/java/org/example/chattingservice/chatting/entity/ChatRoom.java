package org.example.chattingservice.chatting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomName;

    private String chatRoomDescription;

    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatRoomMember> members = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private final List<Chat> chats = new ArrayList<>();

    public boolean hasMember(String username){
        return members.stream().anyMatch(m -> m.getUsername().equals(username));
    }

    public ChatRoomMember addUser(String username,String nicknameSnapshot) {
        if(hasMember(username)){
            return null;
        }
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(this)
                .username(username)
                .nicknameSnapshot(nicknameSnapshot)
                .build();
        members.add(member);
        return member;
    }

    public void deleteUser(String username) {
        members.removeIf(m-> m.getUsername().equals(username));
    }
}
