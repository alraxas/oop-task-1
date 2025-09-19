package org.example.enums;

public enum TaskStatus {
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    IN_PROGRESS("In progress"),
    PENDING("Pending");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
