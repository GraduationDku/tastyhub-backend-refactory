package com.example.recipeservice.messaging.outbox.dispatcher;


import com.example.recipeservice.messaging.outbox.entity.OutboxEvent;
import com.example.recipeservice.messaging.outbox.entity.OutboxStatus;
import com.example.recipeservice.messaging.outbox.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxDispatcher {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelayString = "${outbox.dispatcher.delay:2000}")
    public void dispatch() {
        List<OutboxEvent> events = outboxRepository.findTop100ByStatusInAndNextAttemptAtBefore(
                List.of(OutboxStatus.PENDING, OutboxStatus.FAILED), Instant.now());

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload()).get();
                event.markSent();

            } catch (Exception e) {
                if (event.canRetry()) {
                    event.markRetry(Duration.ofSeconds(5));
                } else {
                    event.markDead();

                }
            }
        }
    }
}
