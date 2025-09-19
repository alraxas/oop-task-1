package org.example.models;

import org.example.enums.TaskPriority;
import org.example.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskPriority taskPriority;
    private TaskStatus taskStatus;
    private LocalDateTime dueDate;
    private LocalDateTime completedDate;
    private LocalDateTime createdDate;

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskPriority = TaskPriority.MEDIUM;
        this.taskStatus = TaskStatus.PENDING;
        this.dueDate = null;
        this.completedDate = null;
        this.createdDate = LocalDateTime.now();
    }

    public Task(int id, String title, String description, TaskPriority taskPriority) {
        this(id, title, description);
        this.taskPriority = taskPriority;
    }

    public Task(int id, String title, String description, TaskPriority taskPriority, LocalDateTime dueDate) {
        this(id, title, description, taskPriority);
        this.dueDate = dueDate;
    }

    public Task(int id, String title, String description, TaskPriority taskPriority,
                LocalDateTime dueDate, TaskStatus taskStatus) {
        this(id, title, description, taskPriority, dueDate);
        this.taskStatus = taskStatus;
    }

    public int getId() {
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

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public void setDescription(String description) {
        this.description = description.trim();
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void markInProgress() {
        if (taskStatus != TaskStatus.COMPLETED && taskStatus != TaskStatus.CANCELLED) {
            this.taskStatus = TaskStatus.IN_PROGRESS;
        }
    }

    public void markCompleted() {
        if (taskStatus != TaskStatus.CANCELLED) {
            this.taskStatus = TaskStatus.COMPLETED;
            this.completedDate = LocalDateTime.now();
        }
    }

    public void markCancelled() {
        if (taskStatus != TaskStatus.COMPLETED) {
            this.taskStatus = TaskStatus.CANCELLED;
        }
    }

}
