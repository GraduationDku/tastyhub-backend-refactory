package com.example.userservice.saga.userDeletion;

import com.example.userservice.messaging.outbox.entity.OutboxEventFactory;
import com.example.userservice.messaging.outbox.repository.OutboxRepository;
import com.example.userservice.saga.StepDecision;
import com.example.userservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.example.events.Topics;
import org.example.events.userdeletion.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDeletionSagaService {
    private final UserDeletionSagaRepository sagaRepository;
    private final OutboxRepository outboxRepository;
    private final OutboxEventFactory outboxEventFactory;
    private final UserDeletionLocalStepService localStepService;


    @Transactional
    public UUID start(String username) {
        UUID sagaId = UUID.randomUUID();
        UserDeletionSaga saga = UserDeletionSaga.start(sagaId, username);

        try {
            localStepService.softDelete(username);
            saga.applyStepResult(UserDeletionStepResultEvent.create(
                    UUID.randomUUID(), sagaId, Instant.now(), username,
                    UserDeletionStep.USER, UserDeletionStepStatus.SUCCEEDED, null));
        } catch (Exception e) {
            saga.fail(UserDeletionStep.USER.name(), e.getMessage());
            sagaRepository.save(saga);
            UserDeletionFailedEvent failed = UserDeletionFailedEvent.create(
                    UUID.randomUUID(), sagaId, Instant.now(), username,
                    UserDeletionStep.USER, e.getMessage());
            outboxRepository.save(outboxEventFactory.from(
                    Topics.USER_DELETION_FAILED, sagaId.toString(), failed, "UserDeletionFailedEvent"));
            return sagaId;
        }

        sagaRepository.save(saga);
        UserDeletionRequestedEvent event = UserDeletionRequestedEvent.create(
                UUID.randomUUID(), sagaId, Instant.now(), username);
        outboxRepository.save(outboxEventFactory.from(
                Topics.USER_DELETION_REQUESTED, sagaId.toString(), event, "UserDeletionRequestedEvent"));
        return sagaId;
    }


    public StepDecision applyStepResult(UserDeletionStepResultEvent event) {


        UserDeletionSaga saga = sagaRepository.findById(event.sagaId())
                .orElseThrow(() -> new IllegalArgumentException("Saga not found: " + event.sagaId()));
        if (!saga.canAcceptStepResult() || saga.isStepFinal(event.step())) {
            return StepDecision.none();
        }
        saga.applyStepResult(event);
        if (event.stepStatus() == UserDeletionStepStatus.FAILED) {
            saga.fail(event.step().name(), event.reason());
            return StepDecision.emit(event.step(), event.reason());
        }
        if (saga.allStepSucceeded()) {
            saga.complete();
            return StepDecision.completed();
        }
        return StepDecision.none();
    }

    @Transactional
    public void applyCompResult(UserDeletionCompensationResultEvent event) {
        UserDeletionSaga saga = sagaRepository.findById(event.sagaId())
                .orElseThrow(() -> new IllegalArgumentException("Saga not found: " + event.sagaId()));
        if (!saga.canAcceptCompResult() || saga.isCompFinal(event.step())) {
            return;
        }
        saga.applyCompResult(event);
        if (saga.anyCompFailed()) {
            saga.compensationFailed();
        } else if (saga.allCompSucceeded()) {
            saga.compensated();
        }
    }

    @Scheduled(fixedDelayString = "${saga.timeout.delay:60000}")
    @Transactional
    public void sweepStuckSagas() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
        List<UserDeletionSaga> stuck = sagaRepository.findByStatusAndUpdatedAtBefore(
                UserDeletionSagaStatus.IN_PROGRESS, cutoff);
        for (UserDeletionSaga saga : stuck) {
            saga.fail("TIMEOUT", "step timeout");
            UserDeletionFailedEvent failed = UserDeletionFailedEvent.create(
                    UUID.randomUUID(), saga.getSagaId(), Instant.now(), saga.getUsername(),
                    null, "timeout");
            outboxRepository.save(outboxEventFactory.from(
                    Topics.USER_DELETION_FAILED, saga.getSagaId().toString(),
                    failed, "UserDeletionFailedEvent"));
        }
    }



}

