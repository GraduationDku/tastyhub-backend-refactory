package com.example.recipeservice.messaging.outbox.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventFactory {
    private final ObjectMapper objectMapper;

    public OutboxEvent from(String topic, String messageKey, Object payload, String eventType) {
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Outbox payload serialize failed", e);
        }
        return new OutboxEvent().builder()
                .topic(topic)
                .messageKey(messageKey)
                .eventType(eventType)
                .payload(json)
                .status(OutboxStatus.PENDING)
                .build();
    }
}
