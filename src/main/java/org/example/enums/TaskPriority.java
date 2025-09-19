package org.example.enums;

public enum TaskPriority {
    LOW("Low priority"),
    MEDIUM("Medium priority"),
    HIGH("High priority");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
