package com.example.userservice.saga.userDeletion;

import com.example.userservice.messaging.inbox.entity.InboxEvent;
import com.example.userservice.messaging.inbox.entity.InboxId;
import com.example.userservice.messaging.inbox.repository.InboxRepository;
import com.example.userservice.messaging.outbox.entity.OutboxEventFactory;
import com.example.userservice.messaging.outbox.repository.OutboxRepository;
import com.example.userservice.saga.StepDecision;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.events.Topics;
import org.example.events.userdeletion.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDeletionEventListener {

    private static final String CONSUMER = "user-service";
    private final InboxRepository inboxRepository;
    private final UserDeletionSagaService sagaService;
    private final OutboxRepository outboxRepository;
    private final OutboxEventFactory outboxEventFactory;
    private final UserDeletionLocalStepService localStepService;

    private final ObjectMapper objectMapper;

    private <T> T readEvent(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid payload for " + type.getSimpleName(), e);
        }
    }


    @KafkaListener(topics = Topics.USER_DELETION_STEP_RESULT,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onStepResult(String payload, Acknowledgment ack) {
        UserDeletionStepResultEvent resultEvent = readEvent(payload, UserDeletionStepResultEvent.class);
        InboxId inboxId = new InboxId(resultEvent.messageId(), CONSUMER);
        if (inboxRepository.existsById(inboxId)) {
            ack.acknowledge();
            return;
        }
        inboxRepository.save(new InboxEvent(inboxId));
        StepDecision decision = sagaService.applyStepResult(resultEvent);
        if (decision.emitCompleted()) {
            UserDeletionCompletedEvent completed = UserDeletionCompletedEvent.create(UUID.randomUUID(), resultEvent.sagaId(), Instant.now(), resultEvent.username());
            outboxRepository.save(outboxEventFactory.from(
                    Topics.USER_DELETION_COMPLETED,
                    resultEvent.sagaId().toString(),
                    completed,
                    "UserDeletionCompletedEvent"
            ));
        }
        if (decision.emitFailed()) {
            UserDeletionFailedEvent failed = UserDeletionFailedEvent.create(UUID.randomUUID(), resultEvent.sagaId(), Instant.now(), resultEvent.username(),
                    decision.failedStep(), decision.reason());
            outboxRepository.save(outboxEventFactory.from(
                    Topics.USER_DELETION_FAILED,
                    resultEvent.sagaId().toString(),
                    failed,
                    "UserDeletionFailedEvent"
            ));
        }
        ack.acknowledge();
    }


    @KafkaListener(topics = Topics.USER_DELETION_COMPLETED, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onCompleted(String payload, Acknowledgment ack) {
        UserDeletionCompletedEvent event = readEvent(payload, UserDeletionCompletedEvent.class);
        InboxId inboxId = new InboxId(event.messageId(), CONSUMER);
        if (inboxRepository.existsById(inboxId)) {
            ack.acknowledge();
            return;
        }
        inboxRepository.save(new InboxEvent(inboxId));
        localStepService.hardDelete(event.username());
        ack.acknowledge();
    }

    @KafkaListener(topics = Topics.USER_DELETION_FAILED, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onFailed(String payload, Acknowledgment ack) {
        UserDeletionFailedEvent event = readEvent(payload, UserDeletionFailedEvent.class);
        InboxId inboxId = new InboxId(event.messageId(), CONSUMER);
        if (inboxRepository.existsById(inboxId)) {
            ack.acknowledge();
            return;
        }
        inboxRepository.save(new InboxEvent(inboxId));
        UserDeletionCompensationStatus comp = UserDeletionCompensationStatus.SUCCEEDED;
        String reason = null;
        try{
            localStepService.compensation(event.username());
        }catch (Exception e){
            comp = UserDeletionCompensationStatus.FAILED;
            reason = e.getMessage();
        }
        sagaService.applyCompResult(UserDeletionCompensationResultEvent.create(
                UUID.randomUUID(), event.sagaId(), Instant.now(), event.username(),
                UserDeletionStep.USER, comp, reason));
        ack.acknowledge();
    }



    @KafkaListener(topics = Topics.USER_DELETION_COMPENSATION_RESULT,
            groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onCompResult(String payload, Acknowledgment ack) {

        UserDeletionCompensationResultEvent event =
                readEvent(payload, UserDeletionCompensationResultEvent.class);

        InboxId inboxId = new InboxId(event.messageId(), CONSUMER);
        if (inboxRepository.existsById(inboxId)) {
            ack.acknowledge();
            return;
        }
        inboxRepository.save(new InboxEvent(inboxId));
        sagaService.applyCompResult(event);
        ack.acknowledge();
    }
}
