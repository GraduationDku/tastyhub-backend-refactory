package org.example.chattingservice.chatting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.chattingservice.chatting.dtos.ChatDto;
import org.example.chattingservice.chatting.dtos.ChatRoomCreateDto;
import org.example.chattingservice.chatting.dtos.ChatRoomDto;
import org.example.chattingservice.chatting.service.chatRoom.ChatRoomService;
import org.example.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChattingRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class ChattingRoomControllerRestDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private ChatRoomDto chatRoomDto;
    private ChatDto chatDto;

    @BeforeEach
    void setUp() {
        given(jwtUtils.extractUsername(anyString())).willReturn("tester");

        chatRoomDto = ChatRoomDto.builder()
                .roomId(1L)
                .roomName("Room A")
                .chatRoomDescription("Sample room")
                .build();

        chatDto = ChatDto.builder()
                .id(1L)
                .senderNickname("tester")
                .content("Hello")
                .build();
    }

    @Test
    void createChatRoomDocs() throws Exception {
        ChatRoomCreateDto request = ChatRoomCreateDto.builder()
                .roomName("Room A")
                .chatRoomDescription("Sample room")
                .build();
        doNothing().when(chatRoomService).createChatRoom(anyString(), any());

        mockMvc.perform(post("/chatting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer access-token")
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isCreated())
                .andDo(document("chatting-create-room",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("roomName").type(JsonFieldType.STRING).description("Room name"),
                                fieldWithPath("chatRoomDescription").type(JsonFieldType.STRING)
                                        .description("Room description")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("Result message")
                        )
                ));
    }

    @Test
    void getChatRoomListDocs() throws Exception {
        Page<ChatRoomDto> page = new PageImpl<>(
                List.of(chatRoomDto), PageRequest.of(0, 10), 1
        );
        given(chatRoomService.getChatRoomList(eq("tester"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/chatting")
                        .header("Authorization", "Bearer access-token")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("chatting-room-list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        queryParameters(
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page size").optional(),
                                parameterWithName("sort").description("Sort order").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[].roomId").type(JsonFieldType.NUMBER).description("Room ID"),
                                fieldWithPath("content[].roomName").type(JsonFieldType.STRING).description("Room name"),
                                fieldWithPath("content[].chatRoomDescription").type(JsonFieldType.STRING)
                                        .description("Room description"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("Total elements"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("Total pages"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("Page size"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("Page number")
                        )
                ));
    }

    @Test
    void getChatRoomDocs() throws Exception {
        given(chatRoomService.getChatContent(eq(1L), eq("tester"))).willReturn(List.of(chatDto));

        mockMvc.perform(get("/chatting/{roomId}", 1L)
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(document("chatting-room-detail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("roomId").description("Room ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("Chat ID"),
                                fieldWithPath("[].senderNickname").type(JsonFieldType.STRING)
                                        .description("Sender nickname"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("Message content")
                        )
                ));
    }

    @Test
    void enterNewChatRoomDocs() throws Exception {
        doNothing().when(chatRoomService).enterNewChatRoom(eq(1L), anyString());

        mockMvc.perform(patch("/chatting/{roomId}", 1L)
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(document("chatting-room-enter",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("roomId").description("Room ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("Result message")
                        )
                ));
    }

    @Test
    void outChatRoomDocs() throws Exception {
        doNothing().when(chatRoomService).outChatRoom(eq(1L), anyString());

        mockMvc.perform(delete("/chatting/{roomId}", 1L)
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isNoContent())
                .andDo(document("chatting-room-leave",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("roomId").description("Room ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        )
                ));
    }

    @Test
    void deleteChatRoomDocs() throws Exception {
        doNothing().when(chatRoomService).deleteChatRoom(eq(1L), anyString());

        mockMvc.perform(delete("/chatting/{roomId}/{postId}", 1L, 10L)
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isNoContent())
                .andDo(document("chatting-room-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("roomId").description("Room ID"),
                                parameterWithName("postId").description("Post ID (unused)")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        )
                ));
    }

}
