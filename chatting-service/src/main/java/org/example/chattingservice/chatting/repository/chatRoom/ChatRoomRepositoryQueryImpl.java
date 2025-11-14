package org.example.chattingservice.chatting.repository.chatRoom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.example.chattingservice.chatting.dtos.ChatRoomDto;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.example.repository.support.QuerydslRepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.function.Function;

import static org.example.chattingservice.chatting.entity.QChatRoom.chatRoom;
import static org.example.chattingservice.chatting.entity.QChatRoomMember.chatRoomMember;

@Slf4j
public class ChatRoomRepositoryQueryImpl  extends QuerydslRepositorySupport implements ChatRoomRepositoryQuery {
    private JPAQueryFactory jpaQueryFactory;

    public ChatRoomRepositoryQueryImpl(JPAQueryFactory jpaQueryFactory) {
        super(ChatRoom.class, jpaQueryFactory);
    }


//    @Override
//    public List<ChatRoomDto> findByUsername(String username) {
/// /        return jpaQueryFactory.select(Projections.constructor())
/// /                .from(chatRoom);
/// /
//        return null;
//    }

    @Override
    public Page<ChatRoomDto> findAllByMemberUsername(String username, Pageable pageable) {
        Function <JPAQueryFactory, JPAQuery<ChatRoomDto>> contentQuery = factory -> factory.select(Projections.constructor(ChatRoomDto.class,
                        chatRoom.id,
                        chatRoom.roomName,
                        chatRoom.chatRoomDescription
                )).from(chatRoom)
                .join(chatRoom.members, chatRoomMember)
                .where(chatRoomMember.username.eq(username))
                .fetchJoin();
        Function<JPAQueryFactory, JPAQuery<Long>> countQuery = facctory -> createCountQuery(chatRoom);
        return applyPagination(pageable, contentQuery, countQuery);
    }

//    @Override
//    public Optional<ChatRoom> lockById(Long id) {
//        return Optional.empty();
//    }
}
