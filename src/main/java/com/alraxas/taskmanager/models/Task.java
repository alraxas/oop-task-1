package com.alraxas.taskmanager.models;

import com.alraxas.taskmanager.enums.TaskPriority;
import com.alraxas.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskPriority taskPriority;
    private TaskStatus taskStatus;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public Task(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskPriority = TaskPriority.MEDIUM;
        this.taskStatus = TaskStatus.PENDING;
        this.dueDate = null;
        this.completedAt = null;
        this.createdAt = LocalDateTime.now();
    }

    public Task(Long id, String title, String description, TaskPriority taskPriority) {
        this(id, title, description);
        this.taskPriority = taskPriority;
    }

    public Task(Long id, String title, String description, TaskPriority taskPriority, LocalDateTime dueDate) {
        this(id, title, description, taskPriority);
        this.dueDate = dueDate;
    }

    public Task(Long id, String title, String description, TaskPriority taskPriority,
                LocalDateTime dueDate, TaskStatus taskStatus) {
        this(id, title, description, taskPriority, dueDate);
        this.taskStatus = taskStatus;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title can not be empty");
        }
        this.title = title.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        if (taskPriority == null) {
            throw new IllegalArgumentException("Priority can not be null");
        }
        this.taskPriority = taskPriority;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return taskStatus == TaskStatus.COMPLETED;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && !isCompleted();
    }

    public void markInProgress() {
        if (taskStatus != TaskStatus.COMPLETED && taskStatus != TaskStatus.CANCELLED) {
            this.taskStatus = TaskStatus.IN_PROGRESS;
        }
    }

    public void markCompleted() {
        if (taskStatus != TaskStatus.CANCELLED) {
            this.taskStatus = TaskStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
        }
    }

    public void markCancelled() {
        if (taskStatus != TaskStatus.COMPLETED) {
            this.taskStatus = TaskStatus.CANCELLED;
        }
    }

    public void resetStatus() {
        if (taskStatus != TaskStatus.COMPLETED) {
            this.taskStatus = TaskStatus.PENDING;
        }
    }

    public String getFormattedCreatedAt() {
        return formatDateTime(createdAt);
    }

    public String getFormattedDueDate() {
        return dueDate != null ? formatDateTime(dueDate) : "Not set";
    }

    public String getFormattedCompletedAt() {
        return completedAt != null ? formatDateTime(createdAt) : "Not finished";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- TASK #").append(id).append(" ---\n");
        sb.append("| title: ").append(title).append("\n");
        sb.append("| description: ").append(description.isEmpty() ? "none" : description).append("\n");
        sb.append("| status: ").append(taskStatus.toString()).append("\n");
        sb.append("| priority: ").append(taskPriority.toString()).append("\n");
        sb.append("| created at: ").append(getFormattedCreatedAt()).append("\n");
        sb.append(" due to: ").append(getFormattedDueDate());

        if (isOverdue()) {
            sb.append(" Overdue!");
        }

        if (isCompleted()) {
            sb.append("\n Completed: ").append(getFormattedCompletedAt());
        }

        sb.append("\n---------------");

        return sb.toString();
    }

    public String toShortString() {
        return String.format("#%d: %s [%s]", id, title, taskStatus.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
