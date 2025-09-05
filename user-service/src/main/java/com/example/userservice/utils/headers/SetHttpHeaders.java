package com.example.userservice.utils.headers;

import jakarta.mail.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class SetHttpHeaders {

    public HttpHeaders setHeadersTypeJson() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        httpHeaders.setDate(new Date().getTime());
        return httpHeaders;
    }
}
