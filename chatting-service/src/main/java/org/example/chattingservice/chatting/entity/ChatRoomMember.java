package org.example.chattingservice.chatting.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.timeStamp.TimeStamp;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "chat_room_members",
       uniqueConstraints = @UniqueConstraint(name = "uk_room_username", columnNames = {"chat_room_id", "username"}))
public class ChatRoomMember extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = true, length = 100)
    private String nicknameSnapshot;




}
