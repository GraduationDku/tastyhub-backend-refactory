package org.example.chattingservice.chatting.repository.chatRoom;

import jakarta.persistence.LockModeType;
import org.example.chattingservice.chatting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryQuery {

    @Query("select distinct c from ChatRoom c join c.members m where m.username = :username")
    List<ChatRoom> findAllByMemberUsername(@Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ChatRoom c where c.id = :id")
    Optional<ChatRoom> lockById(@Param("id") Long id);
}
