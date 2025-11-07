package com.example.userservice.user.entity;

import com.example.userservice.village.entity.Village;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.timeStamp.TimeStamp;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Table(name = "users",indexes = {
        @Index(name = "idx_username", columnList = "user_name"),
        @Index(name = "idx_apple_id", columnList = "appleId")
})
@Entity

public class User  extends TimeStamp {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    private String password;

    private String userImg;

    private String nickname;

    private String email;

    private String appleId;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    public static enum UserType {
        ADMIN, COMMON
    }

    @Embedded
    private Village village;


    public void updatePassword(String password) {
        this.password = password;
    }

    public static User createUser(String userName, String encryptedPassword, String imgUrl,
                                  String nickname, String email, UserType userType, Village village) {
        return User.builder()
                .userName(userName)
                .password(encryptedPassword)
                .userImg(imgUrl)
                .email(email)
                .nickname(nickname)
                .village(village)
                .userType(userType)
                .build();
    }

    public static User createAppleUser(String email, String appleId, String nickname) {
        return User.builder()
                .email(email)
                .userName(email.split("@")[0])
                .appleId(appleId)
                .nickname(nickname)
                .userType(UserType.COMMON)
                .build();
    }

    public void updateUserInfo(String newNickName, String imgUrl) {
        this.nickname = newNickName;
        this.userImg = imgUrl;
    }

    public void updateVillage(Village village) {
        this.village = village;
    }

}