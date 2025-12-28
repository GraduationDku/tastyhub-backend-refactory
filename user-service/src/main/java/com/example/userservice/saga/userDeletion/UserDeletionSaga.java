package com.example.userservice.saga.userDeletion;

import jakarta.persistence.*;
import lombok.Getter;
import org.example.events.userdeletion.UserDeletionCompensationResultEvent;
import org.example.events.userdeletion.UserDeletionCompensationStatus;
import org.example.events.userdeletion.UserDeletionStep;
import org.example.events.userdeletion.UserDeletionStepResultEvent;
import org.example.events.userdeletion.UserDeletionStepStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_deletion_saga")
@Getter
public class UserDeletionSaga {
    @Id
    private UUID sagaId;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserDeletionSagaStatus status;

    @Enumerated(EnumType.STRING)
    private UserDeletionStepStatus recipeStepStatus;

    @Enumerated(EnumType.STRING)
    private UserDeletionStepStatus chatStepStatus;

    @Enumerated(EnumType.STRING)
    private UserDeletionCompensationStatus recipeCompStatus;

    @Enumerated(EnumType.STRING)
    private UserDeletionCompensationStatus chatCompStatus;

    @Enumerated(EnumType.STRING)
    private UserDeletionStepStatus userStepStatus;

    @Enumerated(EnumType.STRING)
    private UserDeletionCompensationStatus userCompStatus;


    private String failedStep;
    private String failReason;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }


    public static UserDeletionSaga start(UUID sagaId, String username) {
        UserDeletionSaga saga = new UserDeletionSaga();
        saga.sagaId = sagaId;
        saga.username = username;
        saga.status = UserDeletionSagaStatus.IN_PROGRESS;
        saga.userStepStatus = UserDeletionStepStatus.PENDING;
        saga.recipeStepStatus = UserDeletionStepStatus.PENDING;
        saga.chatStepStatus = UserDeletionStepStatus.PENDING;
        saga.userCompStatus = UserDeletionCompensationStatus.PENDING;
        saga.recipeCompStatus = UserDeletionCompensationStatus.PENDING;
        saga.chatCompStatus = UserDeletionCompensationStatus.PENDING;
        return saga;
    }

    public void fail(String name, String reason) {
        this.failedStep = name;
        this.failReason = reason;
        this.status = UserDeletionSagaStatus.FAILED;
        if (userCompStatus == null) {
            userCompStatus = UserDeletionCompensationStatus.PENDING;
        }
        if (recipeCompStatus == null) {
            recipeCompStatus = UserDeletionCompensationStatus.PENDING;
        }
        if (chatCompStatus == null) {
            chatCompStatus = UserDeletionCompensationStatus.PENDING;
        }
    }

    public void applyStepResult(UserDeletionStepResultEvent event) {
        if (event.step() == UserDeletionStep.USER) {
            userStepStatus = event.stepStatus();
        }
        if (event.step() == UserDeletionStep.RECIPE) {
            recipeStepStatus = event.stepStatus();
        }
        if (event.step() == UserDeletionStep.CHAT) {
            chatStepStatus = event.stepStatus();
        }
    }

    public void applyCompResult(UserDeletionCompensationResultEvent event) {
        if (status == UserDeletionSagaStatus.FAILED) {
            status = UserDeletionSagaStatus.COMPENSATING;
        }
        if (event.step() == UserDeletionStep.USER) {
            userCompStatus = event.compStatus();
        }
        if (event.step() == UserDeletionStep.RECIPE) {
            recipeCompStatus = event.compStatus();
        }
        if (event.step() == UserDeletionStep.CHAT) {
            chatCompStatus = event.compStatus();
        }
    }

    public boolean anyCompFailed() {
        return UserDeletionCompensationStatus.FAILED.equals(userCompStatus)
                || UserDeletionCompensationStatus.FAILED.equals(recipeCompStatus)
                || UserDeletionCompensationStatus.FAILED.equals(chatCompStatus);

    }

    public void compensationFailed() {
        status = UserDeletionSagaStatus.COMPENSATION_FAILED;
    }

    public boolean allCompSucceeded() {
        return UserDeletionCompensationStatus.SUCCEEDED.equals(userCompStatus)
                && UserDeletionCompensationStatus.SUCCEEDED.equals(recipeCompStatus)
                && UserDeletionCompensationStatus.SUCCEEDED.equals(chatCompStatus);
    }

    public void compensated() {
        status = UserDeletionSagaStatus.COMPENSATED;
    }

    public void complete() {
        status = UserDeletionSagaStatus.COMPLETED;
    }

    public boolean allStepSucceeded() {
        return UserDeletionStepStatus.SUCCEEDED.equals(userStepStatus)
                && UserDeletionStepStatus.SUCCEEDED.equals(recipeStepStatus)
                && UserDeletionStepStatus.SUCCEEDED.equals(chatStepStatus);

    }

    public boolean isTerminal() {
        return status == UserDeletionSagaStatus.COMPLETED
                || status == UserDeletionSagaStatus.FAILED
                || status == UserDeletionSagaStatus.COMPENSATING
                || status == UserDeletionSagaStatus.COMPENSATED
                || status == UserDeletionSagaStatus.COMPENSATION_FAILED;
    }

    public boolean isStepFinal(UserDeletionStep step) {
        if (step == UserDeletionStep.USER) return userStepStatus != UserDeletionStepStatus.PENDING;
        if (step == UserDeletionStep.RECIPE) return recipeStepStatus != UserDeletionStepStatus.PENDING;
        if (step == UserDeletionStep.CHAT) return chatStepStatus != UserDeletionStepStatus.PENDING;
        return false;
    }

    public boolean isCompFinal(UserDeletionStep step) {

        if (step == UserDeletionStep.USER)
            return userCompStatus != null && userCompStatus != UserDeletionCompensationStatus.PENDING;
        if (step == UserDeletionStep.RECIPE)
            return recipeCompStatus != null && recipeCompStatus != UserDeletionCompensationStatus.PENDING;
        if (step == UserDeletionStep.CHAT)
            return chatCompStatus != null && chatCompStatus != UserDeletionCompensationStatus.PENDING;
        return false;
    }

    public boolean canAcceptStepResult() {
        return status == UserDeletionSagaStatus.IN_PROGRESS;
    }

    public boolean canAcceptCompResult() {
        return status == UserDeletionSagaStatus.FAILED || status == UserDeletionSagaStatus.COMPENSATING;
    }


}
