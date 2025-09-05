package com.example.userservice.utils.auth.apple;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppleUserInfo(
        @JsonProperty("sub") String sub,
        @JsonProperty("email") String email,
        @JsonProperty("email_verified") String emailVerified
) {
}