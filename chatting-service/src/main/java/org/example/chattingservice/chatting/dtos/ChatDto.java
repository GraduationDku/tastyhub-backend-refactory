package org.example.chattingservice.chatting.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.chattingservice.chatting.entity.Chat;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {

    Long id;

    private String senderNickname;

    private String content;

    public ChatDto(Chat chat) {
        this.id = chat.getId();
        this.senderNickname = chat.getSenderNickname();
        this.content = chat.getContent();
    }
}
