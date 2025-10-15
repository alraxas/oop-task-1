package com.alraxas.taskmanager.managers;

import com.alraxas.taskmanager.enums.TaskPriority;
import com.alraxas.taskmanager.enums.TaskStatus;
import com.alraxas.taskmanager.models.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    @Test
    public void shouldCreateTaskWithValidParameters() {
        Task task = new Task(1L, "test", "desc");

        assertEquals("test", task.getTitle());
        assertEquals("desc", task.getDescription());
        assertEquals(TaskPriority.MEDIUM, task.getTaskPriority());
        assertEquals(TaskStatus.PENDING, task.getTaskStatus());
        assertNotNull(task.getId());
        assertNotNull(task.getCreatedAt());
    }

    private TaskManager taskManager = new TaskManager();


    @Test
    public void testCreateAndRetrieveTask() {
        Task task = taskManager.addTask("test", "desc");
        assertNotNull(task);
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    public void testCompleteTask() {
        Task task = taskManager.addTask("test", "desc");
        assertEquals(TaskStatus.PENDING, task.getTaskStatus());

        taskManager.completeTask(task.getId());
        assertEquals(TaskStatus.COMPLETED, task.getTaskStatus());
    }
}