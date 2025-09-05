package com.example.userservice.utils.auth;

public interface OAuthUserInfo {
    String getProvider();
    String getProviderId();

    String getName();

    String getEmail();


}
