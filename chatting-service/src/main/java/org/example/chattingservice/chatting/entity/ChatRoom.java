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
    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private Set<ChatRoomMember> userNames = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private final List<Chat> chats = new ArrayList<>();

    public void updateEnterUser(ChatRoomMember chatRoomMember) {
        this.getUserNames().add(chatRoomMember);
    }

    public void updateDeleteUser(ChatRoomMember chatRoomMember) {
        this.getUserNames().remove(chatRoomMember);
    }
}
