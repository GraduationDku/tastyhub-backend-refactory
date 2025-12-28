package org.example.chattingservice.chatting.service.client;

import org.example.dtos.UserDtoForNickname;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", path = "/users")
public interface UserClient {

    @GetMapping("/get-user-nickname")
    UserDtoForNickname getUserNickName(@RequestParam("username") String username);


}
