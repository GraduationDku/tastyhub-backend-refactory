package org.example.chattingservice.chatting.controller;

import lombok.RequiredArgsConstructor;
import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.chattingservice.chatting.service.chat.ChatService;
import org.example.jwt.JwtUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChattingController {

    private final ChatService chatService;
    private final JwtUtils jwtUtils;

    @CrossOrigin
    @MessageMapping("/rooms/{roomId}")
    @SendTo("/topic/rooms/{roomId}")
    public ChatDto sendMessage(@DestinationVariable Long roomId, @Payload ChatDto chatDto, @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtils.extractUsername(authHeader);
        return chatService.sendMessage(roomId, chatDto, username);


    }
}
