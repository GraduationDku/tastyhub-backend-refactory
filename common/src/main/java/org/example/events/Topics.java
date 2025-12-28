package org.example.events;

public final class Topics {
    public static final String USER_DELETION_REQUESTED =
            "user.deletion.requested";
    public static final String USER_DELETION_STEP_RESULT=
            "user.deletion.step.result";
    public static final String USER_DELETION_COMPLETED =
            "user.deletion.completed";
    public static final String USER_DELETION_FAILED =
            "user.deletion.failed";
    public static final String USER_DELETION_COMPENSATION_RESULT =
            "user.deletion.compensation.result";
    private Topics() {}
}
