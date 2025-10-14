package com.alraxas.taskmanager.managers;

import com.alraxas.taskmanager.enums.TaskPriority;
import com.alraxas.taskmanager.enums.TaskStatus;
import com.alraxas.taskmanager.models.Task;
import com.alraxas.taskmanager.utils.ConsoleUtils;
import com.alraxas.taskmanager.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TaskManager {
    private List<Task> tasks;
    private AtomicLong idCounter;

    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
    }

    public Task addTask(Task task) {
        tasks.add(task);
        ConsoleUtils.printLine("Task added: " + task.getTitle());
        return task;
    }

    public Task addTask(String title, String description) {
        Task task = new Task(idCounter.getAndIncrement(), title, description);
        tasks.add(task);
        ConsoleUtils.printLine("Task added: " + task.getTitle());
        return task;
    }

    public Task addTask(String title, String description, TaskPriority priority) {
        Task task = new Task(idCounter.getAndIncrement(), title, description, priority);
        tasks.add(task);
        ConsoleUtils.printLine("Task added: " + task.getTitle());
        return task;
    }

    public Task addTask(String title, String description, TaskPriority priority, LocalDateTime dueDate) {
        Task task = new Task(idCounter.getAndIncrement(), title, description, priority, dueDate);
        tasks.add(task);
        ConsoleUtils.printLine("Task added: " + task.getTitle());
        return task;
    }

    public boolean removeTask(Long taskId) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(taskId));
        if (removed) {
            ConsoleUtils.printLine("Task #" + taskId + " deleted");
        } else {
            ConsoleUtils.printLine("Task #" + taskId + " not found");
        }
        return removed;
    }

    public Task getTaskById(Long taskId) {
        return tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);
    }

    public boolean updateTask(Long taskId, String title, String description, TaskPriority priority) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setTitle(title);
            task.setDescription(description);
            task.setTaskPriority(priority);
            ConsoleUtils.printLine("Task #" + taskId + " updated");
            return true;
        }
        ConsoleUtils.printLine("Task #" + taskId + " not found");
        return false;
    }

    public boolean markTaskInProgress(Long taskId) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.markInProgress();
            ConsoleUtils.printLine("Task #" + taskId + " in progress");
            return true;
        }
        return false;
    }

    public boolean completeTask(Long taskId) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.markCompleted();
            ConsoleUtils.printLine("Task #" + taskId + " done");
            return true;
        }
        return false;
    }

    public boolean cancelTask(Long taskId) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.markCancelled();
            ConsoleUtils.printLine("Task #" + taskId + " cancelled");
            return true;
        }
        return false;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getActiveTasks() {
        return tasks.stream()
                .filter(task -> !task.isCompleted() && task.getTaskStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks() {
        return tasks.stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByPriority(TaskPriority priority) {
        return tasks.stream()
                .filter(task -> task.getTaskPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        return tasks.stream()
                .filter(Task::isOverdue)
                .collect(Collectors.toList());
    }

    public List<Task> getTodayTasks() {
        return tasks.stream()
                .filter(task -> task.getDueDate() != null && TimeUtils.isToday(task.getDueDate()))
                .collect(Collectors.toList());
    }

    public List<Task> getHighPriorityTasks() {
        return tasks.stream()
                .filter(task -> task.getTaskPriority() == TaskPriority.HIGH ||
                        task.getTaskPriority() == TaskPriority.URGENT)
                .collect(Collectors.toList());
    }

    public List<Task> searchTasksByTitle(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<Task> searchTasksByDescription(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public List<Task> filterTasksByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getTaskStatus() == status)
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Total tasks", tasks.size());
        stats.put("Active", getActiveTasks().size());
        stats.put("Completed", getCompletedTasks().size());
        stats.put("Overdue", getOverdueTasks().size());
        stats.put("Urgent", getHighPriorityTasks().size());

        // Статистика по приоритетам
        for (TaskPriority priority : TaskPriority.values()) {
            stats.put(priority.getDisplayName(), getTasksByPriority(priority).size());
        }

        return stats;
    }

    public void showSummary() {
        Map<String, Integer> stats = getStatistics();

        System.out.println("SUMMARY OF TASKS");
        stats.forEach((key, value) ->
                System.out.printf("│ %-20s: %d%n", key, value)
        );

    }

    public int getTaskCount() {
        return tasks.size();
    }

    public void clearAllTasks() {
        if (ConsoleUtils.confirmAction("Are you sure you want to delete all the issues?")) {
            tasks.clear();
            idCounter.set(1);
            ConsoleUtils.printLine("All tasks were deleted");
        }
    }
}