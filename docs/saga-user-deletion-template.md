# User Deletion Saga Template (Kafka + Outbox/Inbox)

## 1) Goal and Scope
TODO: define what "user deletion" means in each service (soft delete vs hard delete).
TODO: define compensation behavior (what gets restored and how long).
TODO: define success criteria (all steps succeeded, final hard delete, etc).

## 2) Saga Style
TODO: choose one:
- Orchestrated (user-service drives steps)
- Choreography (participants react to events without central coordinator)

## 3) Topics
Existing topic names (common/src/main/java/org/example/events/Topics.java):
- user.deletion.requested
- user.deletion.step.result
- user.deletion.completed
- user.deletion.failed
TODO: add extra topics only if needed (e.g., user.deletion.compensate).

## 4) Event Schemas (common module)
### 4.1 UserDeletionRequestedEvent (already exists)
Fields:
- messageId (UUID)
- sagaId (UUID)
- occurredAt (Instant)
- username (String)

### 4.2 UserDeletionStepResultEvent (already exists)
Fields:
- messageId (UUID)
- sagaId (UUID)
- occurredAt (Instant)
- username (String)
- step (UserDeletionStep)
- stepStatus (UserDeletionStepStatus)
- reason (String)
TODO: add factory method or builder if needed.

### 4.3 UserDeletionCompletedEvent (TODO)
Fields (suggested):
- messageId, sagaId, occurredAt, username
TODO: decide if you need extra metadata (duration, completedSteps).

### 4.4 UserDeletionFailedEvent (TODO)
Fields (suggested):
- messageId, sagaId, occurredAt, username
- failedStep
- reason
- completedSteps (for compensation)

## 5) Outbox / Inbox (per service)
### 5.1 Outbox Entity (template)
Fields (suggested):
- id (UUID)
- aggregateType (String)
- aggregateId (String)
- eventType (String)
- payload (String, JSON)
- status (PENDING, SENT, FAILED)
- createdAt, lastTriedAt
TODO: define JSON serializer and status enum.

### 5.2 Inbox Entity (template)
Fields (suggested):
- messageId (UUID, unique)
- consumer (String)
- receivedAt
TODO: enforce unique constraint on messageId.

### 5.3 Dispatcher (template)
Behavior:
- Poll PENDING outbox rows in batches
- Publish to Kafka
- Mark as SENT (or FAILED with retry count)
TODO: choose scheduling strategy and retry policy.

## 6) User-Service (Saga Orchestrator)
### 6.1 Saga State Entity (template)
Fields (suggested):
- sagaId (UUID)
- username (String)
- status (PENDING/IN_PROGRESS/COMPLETED/FAILED)
- recipeStepStatus (PENDING/SUCCEEDED/FAILED)
- chatStepStatus (PENDING/SUCCEEDED/FAILED)
- createdAt, updatedAt

### 6.2 Start Saga (DELETE /user-service/user/delete)
Template flow:
1) Validate request + auth
2) Create saga row (status = IN_PROGRESS)
3) Create outbox event: UserDeletionRequestedEvent
4) Return accepted response to client (202 or 200)

Pseudo-code:
```
UUID sagaId = UUID.randomUUID();
saveSaga(sagaId, username);
outbox.save(UserDeletionRequestedEvent.create(...));
return ACCEPTED;
```

### 6.3 Handle Step Results (consumer of user.deletion.step.result)
Template flow:
1) Deduplicate via inbox
2) Update saga step status
3) If all steps succeeded -> publish completed event
4) If any step failed -> publish failed event (for compensation)

## 7) Recipe-Service (Participant)
### 7.1 Handle Deletion Requested
Template flow:
1) Deduplicate via inbox
2) Soft-delete recipes/likes for username (or mark owner as deleted)
3) Write UserDeletionStepResultEvent to outbox (SUCCEEDED/FAILED)

### 7.2 Handle Compensation (consumer of user.deletion.failed)
Template flow:
1) Deduplicate via inbox
2) If this service previously succeeded -> restore soft-deleted data

## 8) Chatting-Service (Participant)
### 8.1 Handle Deletion Requested
Template flow:
1) Deduplicate via inbox
2) Remove user from chat rooms, mark messages if needed
3) Write UserDeletionStepResultEvent to outbox (SUCCEEDED/FAILED)

### 8.2 Handle Compensation (consumer of user.deletion.failed)
Template flow:
1) Deduplicate via inbox
2) Restore any changes done in 8.1

## 9) Kafka Configuration (per service)
Template properties:
```
spring.kafka.bootstrap-servers=TODO
spring.kafka.consumer.group-id=TODO
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.producer.acks=all
```
TODO: decide bootstrap server for local vs docker (e.g., localhost:29092 vs kafka:9092).

## 10) Testing Checklist
TODO: verify these manually or with tests:
- Outbox inserts and publishes correctly
- Inbox prevents duplicate processing
- Happy-path: requested -> step results -> completed
- Failure-path: one step fails -> failed -> compensation in other services
