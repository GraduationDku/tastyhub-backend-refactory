package com.example.userservice.saga;

import jakarta.persistence.*;
import org.example.events.userdeletion.UserDeletionCompensationStatus;
import org.example.events.userdeletion.UserDeletionStepStatus;

import java.util.UUID;

@Entity
@Table(name = "user_deletion_saga")
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

    private String failedStep;
    private String failReason;

    @Version
    private long version;

}
