package com.example.userservice.utils.headers;

import com.example.userservice.utils.headers.enums.ResponseMessages;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatusResponse {
    private int statusCode;
    private String message;

    public static StatusResponse valueOf(ResponseMessages responseMessages) {
        return new StatusResponse(responseMessages.getStatusCode(), responseMessages.getMessage());
    }
}
