package com.example.recipeservice.saga;

import com.example.recipeservice.messaging.inbox.entity.InboxEvent;
import com.example.recipeservice.messaging.inbox.entity.InboxId;
import com.example.recipeservice.messaging.inbox.repository.InboxRepository;
import com.example.recipeservice.messaging.outbox.entity.OutboxEventFactory;
import com.example.recipeservice.messaging.outbox.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.events.Topics;
import org.example.events.userdeletion.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDeletionEventListener {
    private static final String CONSUMER = "recipe-service";
    private final InboxRepository inboxRepository;
    private final OutboxRepository outboxRepository;
    private final OutboxEventFactory outboxEventFactory;
    private final UserDeletionStepService stepService;
    private final ObjectMapper objectMapper;
    private <T> T readEvent(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid payload for " + type.getSimpleName(), e);
        }
    }


    @KafkaListener(topics = Topics.USER_DELETION_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onRequested(String payload, Acknowledgment ack) {
        UserDeletionRequestedEvent event =
                readEvent(payload, UserDeletionRequestedEvent.class);
        InboxId inboxId = new InboxId(event.messageId(), CONSUMER);
        if(inboxRepository.existsById(inboxId)){
            ack.acknowledge();
            return;
        }
        inboxRepository.save(new InboxEvent(inboxId));
        UserDeletionStepStatus status = UserDeletionStepStatus.SUCCEEDED;
        String reason = null;
        try {
            stepService.softDelete(event.username());
        } catch (Exception e) {
            status = UserDeletionStepStatus.FAILED;
            reason = e.getMessage();
        }
        UserDeletionStepResultEvent resultEvent = UserDeletionStepResultEvent.create(
                UUID.randomUUID(), event.sagaId(), Instant.now(), event.username(),
                UserDeletionStep.RECIPE, status, reason
        );
        outboxRepository.save(outboxEventFactory.from(
                Topics.USER_DELETION_STEP_RESULT,
                event.sagaId().toString(),
                resultEvent,
                "UserDeletionStepResultEvent"
        ));
        ack.acknowledge();
    }

    @KafkaListener(topics = Topics.USER_DELETION_FAILED,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onFailed(String payload, Acknowledgment ack) {
        UserDeletionFailedEvent event =
                readEvent(payload, UserDeletionFailedEvent.class);
        InboxId inboxId = new InboxId(event.messageId(), CONSUMER);
        if(inboxRepository.existsById(inboxId)){
            ack.acknowledge();
            return;
        }
        inboxRepository.save(new InboxEvent(inboxId));
        UserDeletionCompensationStatus status = UserDeletionCompensationStatus.SUCCEEDED;
        String reason = null;
        try {
            stepService.compensate(event.username());
        }catch (Exception e){
            status = UserDeletionCompensationStatus.FAILED;
            reason = e.getMessage();
        }
        UserDeletionCompensationResultEvent resultEvent = UserDeletionCompensationResultEvent.create(
                UUID.randomUUID(), event.sagaId(), Instant.now(), event.username(),
                UserDeletionStep.RECIPE, status, reason
        );
        outboxRepository.save(outboxEventFactory.from(
                Topics.USER_DELETION_COMPENSATION_RESULT,
                event.sagaId().toString(),
                resultEvent,
                "UserDeletionCompensationResultEvent"
        ));
        ack.acknowledge();
    }


    @KafkaListener(topics = Topics.USER_DELETION_COMPLETED,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onCompleted(String payload, Acknowledgment ack) {
        UserDeletionCompletedEvent event =
                readEvent(payload, UserDeletionCompletedEvent.class);
        InboxId inboxId = new InboxId(event.messageId(), CONSUMER);
        if(!inboxRepository.existsById(inboxId)){
            inboxRepository.save(new InboxEvent(inboxId));
            stepService.hardDelete(event.username());
        }
        ack.acknowledge();
    }
}
