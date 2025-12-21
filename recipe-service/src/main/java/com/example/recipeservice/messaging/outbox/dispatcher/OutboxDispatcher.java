package com.example.recipeservice.messaging.outbox.dispatcher;


import com.example.recipeservice.messaging.outbox.entity.OutboxEvent;
import com.example.recipeservice.messaging.outbox.entity.OutboxStatus;
import com.example.recipeservice.messaging.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@ComponentScan
@RequiredArgsConstructor
public class OutboxDispatcher {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.dispatcher.delay:2000}")
    public void dispatch() {
        List<OutboxEvent> eventList =
                outboxRepository.findTop100ByStatusOrderByCreatedAt(OutboxStatus.PENDING);
        for (OutboxEvent event : eventList) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload()).get();
                event.markSent();

            } catch (Exception e) {
                event.markFailed();
            }
        }
    }
}
