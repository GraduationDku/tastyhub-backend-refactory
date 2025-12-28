package com.example.userservice.saga;

import lombok.Getter;
import org.example.events.userdeletion.UserDeletionStep;
import org.example.events.userdeletion.UserDeletionStepResultEvent;
import org.example.events.userdeletion.UserDeletionStepStatus;

public record StepDecision(
        boolean emitCompleted,
        boolean emitFailed,
        UserDeletionStep failedStep,
        String reason
) {
    public static StepDecision none () {
        return new StepDecision(false, false, null, null);
    }
    public static StepDecision completed () {
        return new StepDecision(true, false, null, null);
    }
    public static StepDecision emitFailed (UserDeletionStep step, String reason){
        return new StepDecision(false, true, step, reason);
    }

    public static StepDecision emit(UserDeletionStep step, String reason) {
        return emitFailed(step, reason);
    }
}
