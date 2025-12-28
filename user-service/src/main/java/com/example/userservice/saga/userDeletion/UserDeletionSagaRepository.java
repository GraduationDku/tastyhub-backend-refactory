package com.example.userservice.saga.userDeletion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserDeletionSagaRepository extends JpaRepository<UserDeletionSaga, UUID> {

    List<UserDeletionSaga> findByStatusAndUpdatedAtBefore(
            UserDeletionSagaStatus status, Instant cutoff);

}
