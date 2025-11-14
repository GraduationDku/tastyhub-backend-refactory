package org.example.chattingservice.chatting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.timeStamp.TimeStamp;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "chats")
public class Chat extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasty_hub_sequence")
    @SequenceGenerator(name = "tasty_hub_sequence", sequenceName = "thesq", allocationSize = 10)
    @Column(name = "chat_id")
    Long id;

    private String senderUsername;
    private String senderNickname;

    private String content;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public static Chat createChat(ChatDto chatDto, ChatRoom chatRoom, String senderUsername, String senderNickname) {
        return Chat.builder()
                .chatRoom(chatRoom)
                .content(chatDto.getContent())
                .senderNickname(senderNickname)
                .senderUsername(senderUsername)
                .build();
    }
}
