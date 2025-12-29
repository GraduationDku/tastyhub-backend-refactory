package com.example.userservice.user.controller;

import com.example.userservice.saga.UserDeletionSagaStatusResponse;
import com.example.userservice.saga.userDeletion.UserDeletionSaga;
import com.example.userservice.saga.userDeletion.UserDeletionSagaRepository;
import com.example.userservice.saga.userDeletion.UserDeletionSagaService;
import com.example.userservice.config.SecurityConfig;
import com.example.userservice.user.dtos.UserDto;
import com.example.userservice.user.entity.User;
import com.example.userservice.user.service.UserService;
import com.example.userservice.utils.auth.userDetails.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dtos.UserDtoForNickname;
import org.example.jwt.JwtAuthFilter;
import org.example.jwt.JwtAuthenticationEntryPoint;
import org.example.jwt.JwtUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class UserControllerRestDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDeletionSagaService userDeletionSagaService;

    @MockBean
    private UserDeletionSagaRepository sagaRepository;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean(name = "jpaMappingContext")
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .userName("tester")
                .nickname("tester")
                .userType(User.UserType.COMMON)
                .build();
        userDetails = new UserDetailsImpl(user, Collections.emptyMap());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void appleLoginDocs() throws Exception {
        Map<String, String> requestBody = Map.of("code", "test_code");
        doNothing().when(userService).appleLogin(anyString(), any());

        mockMvc.perform(post("/users/login/oauth2/code/apple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestBody)))
                .andExpect(status().isOk())
                .andDo(document("users-login-apple",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("code").type(JsonFieldType.STRING)
                                        .description("Apple authorization code")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("Result message")
                        )
                ));
    }

    @Test
    void refreshAccessTokenDocs() throws Exception {
        doNothing().when(userService).refreshAccessToken(anyString(), any());

        mockMvc.perform(post("/users/refresh")
                        .header("Refresh", "refresh-token"))
                .andExpect(status().isOk())
                .andDo(document("users-refresh-token",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Refresh").description("Refresh token")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("Result message")
                        )
                ));
    }

    @Test
    void getUserListDocs() throws Exception {
        Page<UserDto> page = new PageImpl<>(
                List.of(new UserDto("tester", "https://cdn.example/user.png")),
                PageRequest.of(0, 10),
                1
        );
        given(userService.getUserList(eq("tester"), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/users/search/list")
                        .param("nickname", "tester")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("users-search",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("nickname").description("Nickname keyword"),
                                parameterWithName("page").description("Page number").optional(),
                                parameterWithName("size").description("Page size").optional(),
                                parameterWithName("sort").description("Sort order").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[].nickName").type(JsonFieldType.STRING)
                                        .description("User nickname"),
                                fieldWithPath("content[].userImg").type(JsonFieldType.STRING)
                                        .description("User image URL"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER)
                                        .description("Total elements"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER)
                                        .description("Total pages"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("Page size"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("Page number")
                        )
                ));
    }

    @Test
    void deleteUserDocs() throws Exception {
        given(userDeletionSagaService.start(anyString()))
                .willReturn(UUID.fromString("11111111-1111-1111-1111-111111111111"));

        mockMvc.perform(delete("/users/delete")
                        .with(authenticatedUser())
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isAccepted())
                .andDo(document("users-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
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
    void deleteStatusDocs() throws Exception {
        UUID sagaId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UserDeletionSaga saga = UserDeletionSaga.start(sagaId, "tester");
        given(sagaRepository.findById(sagaId)).willReturn(Optional.of(saga));

        mockMvc.perform(get("/users/delete/status/{sagaId}", sagaId))
                .andExpect(status().isOk())
                .andDo(document("users-delete-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("sagaId").description("Saga ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("sagaId").type(JsonFieldType.STRING).description("Saga ID"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("Username"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("Saga status")
                        )
                ));
    }

    @Test
    void logoutDocs() throws Exception {
        given(userService.logout(any(User.class))).willReturn(true);

        mockMvc.perform(post("/users/user/logout")
                        .with(authenticatedUser())
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(document("users-logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
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
    void helloDocs() throws Exception {
        mockMvc.perform(get("/users/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello"))
                .andDo(document("users-hello",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void updateUserInfoDocs() throws Exception {
        doNothing().when(userService).updateUserInfoByUserUpdateRequest(anyString(), any(), any(User.class));

        MockMultipartFile img = new MockMultipartFile(
                "img", "profile.png", "image/png", "fake".getBytes()
        );
        MockMultipartFile data = new MockMultipartFile(
                "data", "", "text/plain", "new-nickname".getBytes()
        );

        mockMvc.perform(multipart("/users/modify/information")
                        .file(img)
                        .file(data)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .with(authenticatedUser())
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andDo(document("users-update-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer access token")
                        ),
                        requestParts(
                                partWithName("img").description("Profile image"),
                                partWithName("data").description("New nickname")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("Status code"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("Result message")
                        )
                ));
    }

    @Test
    void getUserNicknameDocs() throws Exception {
        given(userService.getUserNickname("tester"))
                .willReturn(UserDtoForNickname.builder().nickname("tester").build());

        mockMvc.perform(get("/users/get-user-nickname")
                        .param("username", "tester"))
                .andExpect(status().isOk())
                .andDo(document("users-nickname",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("username").description("Username")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("Nickname")
                        )
                ));
    }

    private RequestPostProcessor authenticatedUser() {
        return request -> {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, userDetails.getPassword(), userDetails.getAuthorities()
            );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            return request;
        };
    }

}
