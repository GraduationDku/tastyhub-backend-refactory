package org.example.chattingservice.chatting.controller;


import lombok.RequiredArgsConstructor;
import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.chattingservice.chatting.dtos.ChatRoomCreateDto;
import org.example.chattingservice.chatting.service.chatRoom.ChatRoomService;
import org.example.headers.StatusResponse;
import org.example.jwt.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.headers.HttpResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChattingRoomController {
    private final ChatRoomService chatRoomService;
    private final JwtUtils jwtUtils;


    /***
     * 채팅방 생성하기
     * @return RESPONSE_OK
     */
    @PostMapping
    public ResponseEntity<StatusResponse> createChatRoom(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChatRoomCreateDto chatRoomDto
    ) {
        try {
            String username = jwtUtils.extractUsername(authHeader);
            chatRoomService.createChatRoom(username, chatRoomDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RESPONSE_CREATED;
    }

    /***
     * 참여하는 채팅방 리스트 조회하기
     * @return List<ChatRoomDto>
     */
    @GetMapping
    public ResponseEntity<Page<ChatRoomCreateDto>> getChatRoomList(
            @RequestHeader("Authorization") String authHeader,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ChatRoomCreateDto> chatRoomDtoList = null;
        try {
            String username = jwtUtils.extractUsername(authHeader);
            chatRoomDtoList = chatRoomService.getChatRoomList(username, pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(chatRoomDtoList);
    }

    /***
     * 채팅방 입장하기
     * @param
     * @return List<ChatDto> chatDtoList
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatDto>> getChatRoom(@PathVariable Long roomId,
                                                     @RequestHeader("Authorization") String authHeader
    ) {

        List<ChatDto> chatDtoList = null;
        try {
            String username = jwtUtils.extractUsername(authHeader);
            chatDtoList = chatRoomService.getChatRoom(roomId, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(chatDtoList);

    }

    /***
     * 새로운 채팅방 입장하기
     * @param roomId
     * @return RESPONSE_OK
     */
    @PatchMapping("/{roomId}")
    public ResponseEntity<StatusResponse> enterNewChatRoom(@PathVariable Long roomId,
                                                           @RequestHeader("Authorization") String authHeader
    ) {

        try {
            String username = jwtUtils.extractUsername(authHeader);
            chatRoomService.enterNewChatRoom(roomId, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RESPONSE_OK;
    }
//https://localhost/room/{roomId} ->Patch/

    /***
     * 채팅방 나가기
     * @param roomId
     * @return RESPONSE_OK
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<StatusResponse> outChatRoom(@PathVariable Long roomId,
                                                      @RequestHeader("Authorization") String authHeader
    ) {


        try {
            String username = jwtUtils.extractUsername(authHeader);
            chatRoomService.outChatRoom(roomId, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DELETE_SUCCESS;
    }

    /***
     * 채팅방 삭제하기
     * @param roomId
     * @return RESPONSE_OK
     */
    @DeleteMapping("/{roomId}/{postId}")
    public ResponseEntity<StatusResponse> deleteChatRoom(@PathVariable Long roomId,
                                                         @RequestHeader("Authorization") String authHeader
    ) {
        try {
            String username = jwtUtils.extractUsername(authHeader);
            chatRoomService.deleteChatRoom(roomId, username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DELETE_SUCCESS;
    }

//    @GetMapping("/check/{postId}")
//    public ResponseEntity<CheckDto> checkRoomCondition(@PathVariable Long postId) {
//        CheckDto checkDto = chatRoomService.checkRoomCondition(postId);
//        return ResponseEntity.ok().body(checkDto);
//    }
}