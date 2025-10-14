package com.alraxas.taskmanager.models;

import com.alraxas.taskmanager.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public class Alarm {
    private Long id;
    private String message;
    private LocalDateTime alarmTime;
    private boolean isActive;
    private boolean isRecurring;
    private LocalDateTime createdAt;
    private LocalDateTime lastTriggered;

    public Alarm(Long id, String message, LocalDateTime alarmTime, boolean isActive, boolean isRecurring) {
        validateInput(id, message, alarmTime);

        this.id = id;
        this.message = message;
        this.alarmTime = alarmTime;
        this.isActive = isActive;
        this.isRecurring = isRecurring;
        this.createdAt = LocalDateTime.now();
        this.lastTriggered = null;
    }

    public Alarm(Long id, String message, LocalDateTime alarmTime) {
        this(id, message, alarmTime, true, false);
    }

    public Alarm(Long id, String message, LocalDateTime alarmTime, boolean isRecurring) {
        this(id, message, alarmTime, true, false);
    }

    public Alarm(Long id, String message, String timeString) {
        this(id, message, TimeUtils.parseTimeToday(timeString));
    }

    private void validateInput(Long id, String message, LocalDateTime alarmTime) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Alarm's ID has to be a positive number");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message can not be empty");
        }
        if (alarmTime == null) {
            throw new IllegalArgumentException("Alarm time can not be null");
        }
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getAlarmTime() {
        return alarmTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isDue() {
        return isActive && LocalDateTime.now().isAfter(alarmTime);
    }

    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message can not be empty");
        }
        this.message = message;
    }

    public void setAlarmTime(LocalDateTime alarmTime) {
        if (alarmTime == null) {
            throw new IllegalArgumentException("Alarm time can not be null");
        }
        this.alarmTime = alarmTime;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void toggle() {
        this.isActive = !this.isActive;
    }

    public boolean shouldTrigger() {
        return isActive &&
                LocalDateTime.now().isAfter(alarmTime) &&
                (lastTriggered == null || !lastTriggered.toLocalDate().equals(LocalDateTime.now().toLocalDate()));
    }

    public void trigger() {
        if (shouldTrigger()) {
            this.lastTriggered = LocalDateTime.now();

            if (isRecurring) {
                // для повторяющихся будильников - устанавливаем на завтра
                this.alarmTime = this.alarmTime.plusDays(1);
            } else {
                // для одноразовых
                this.isActive = false;
            }
        }
    }

    public void snooze(int minutes) {
        if (isActive) {
            this.alarmTime = LocalDateTime.now().plusMinutes(minutes);
            this.lastTriggered = null; // Сбрасываем триггер для нового времени
        }
    }

    public boolean isExpired() {
        return !isActive && !isRecurring && alarmTime.isBefore(LocalDateTime.now());
    }

    public boolean isToday() {
        return alarmTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    public boolean isTomorrow() {
        return alarmTime.toLocalDate().equals(LocalDateTime.now().plusDays(1).toLocalDate());
    }

    public String getFormattedAlarmTime() {
        return TimeUtils.formatDateTime(alarmTime);
    }

    public String getFormattedCreatedAt() {
        return TimeUtils.formatDateTime(createdAt);
    }

    public String getFormattedLastTriggered() {
        return lastTriggered != null ? TimeUtils.formatDateTime(lastTriggered) : "Never";
    }

    public String getTimeUntilAlarm() {
        return TimeUtils.getTimeUntilAlarm(alarmTime);
    }

    public String getStatusWithIcon() {
        if (!isActive) return "Turned off";
        if (shouldTrigger()) return "Passed";
        return "Turned on";
    }

    public String getRecurringWithIcon() {
        return isRecurring ? "Repeated" : "Not repeated";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equals(id, alarm.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() { // для вывода
        StringBuilder sb = new StringBuilder();
        sb.append("--- ALARM #").append(id).append(" ---\n");
        sb.append("| message: ").append(message).append("\n");
        sb.append("| time: ").append(getFormattedAlarmTime()).append("\n");
        sb.append("| status: ").append(getStatusWithIcon()).append("\n");
        sb.append("| type: ").append(getRecurringWithIcon()).append("\n");
        sb.append("| created at: ").append(getFormattedCreatedAt()).append("\n");

        if (isActive && !shouldTrigger()) {
            sb.append(" due to: ").append(getTimeUntilAlarm()).append("\n");
        }

        if (lastTriggered != null) {
            sb.append("| last signal: ").append(getFormattedLastTriggered()).append("\n");
        }

        sb.append("---------------");
        return sb.toString();
    }

    public String toShortString() { // короткий вывод
        String timeInfo = isToday() ? "Today " : isTomorrow() ? "Tomorrow " : "";
        return String.format("#%d: %s %s(%s) [%s]",
                id,
                message,
                timeInfo,
                TimeUtils.formatTime(alarmTime),
                getStatusWithIcon()
        );
    }
}
