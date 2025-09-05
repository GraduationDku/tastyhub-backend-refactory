package com.example.userservice.utils.auth.apple;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppleProperties {
    @Value("${apple.team-id}")
    private String teamId;
    @Value("${apple.client-id}")
    private String clientId;
    @Value("${apple.key-id}")
    private String keyId;
    @Value("${apple.key-path}")
    private String keyPath;
    @Value("${apple.auth-url}")
    private String authUrl;
}
