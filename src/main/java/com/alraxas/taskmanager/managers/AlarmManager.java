package com.alraxas.taskmanager.managers;

import com.alraxas.taskmanager.models.Alarm;
import com.alraxas.taskmanager.utils.ConsoleUtils;
import com.alraxas.taskmanager.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AlarmManager {
    private List<Alarm> alarms;
    private AtomicLong idCounter;
    private Timer alarmTimer;

    public AlarmManager() {
        this.alarms = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
    }

    public Alarm setAlarm(String message, LocalDateTime alarmTime) {
        Alarm alarm = new Alarm(idCounter.getAndIncrement(), message, alarmTime);
        alarms.add(alarm);
        ConsoleUtils.printLine("Alarm is set: " + alarm.getFormattedAlarmTime());
        return alarm;
    }

    public Alarm setAlarm(String message, LocalDateTime alarmTime, boolean isRecurring) {
        Alarm alarm = new Alarm(idCounter.getAndIncrement(), message, alarmTime, isRecurring);
        alarms.add(alarm);
        ConsoleUtils.printLine("Alarm is set: " + alarm.getFormattedAlarmTime() +
                (isRecurring ? " (repeated)" : ""));
        return alarm;
    }

    public Alarm setQuickAlarm(String message, String timeString) {
        Alarm alarm = new Alarm(idCounter.getAndIncrement(), message, timeString);
        alarms.add(alarm);
        ConsoleUtils.printLine("Alarm is set on: " + timeString);
        return alarm;
    }

    public boolean removeAlarm(Long alarmId) {
        boolean removed = alarms.removeIf(alarm -> alarm.getId().equals(alarmId));
        if (removed) {
            ConsoleUtils.printLine("Alarm #" + alarmId + " is deleted");
        } else {
            ConsoleUtils.printError("Alarm #" + alarmId + " is not found");
        }
        return removed;
    }

    public Alarm getAlarmById(Long alarmId) {
        return alarms.stream()
                .filter(alarm -> alarm.getId().equals(alarmId))
                .findFirst()
                .orElse(null);
    }

    public boolean activateAlarm(Long alarmId) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.activate();
            ConsoleUtils.printLine("Alarm #" + alarmId + " is activated");
            return true;
        }
        return false;
    }

    public boolean deactivateAlarm(Long alarmId) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.deactivate();
            ConsoleUtils.printLine("Alarm #" + alarmId + " is deactivated");
            return true;
        }
        return false;
    }

    public boolean toggleAlarm(Long alarmId) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.toggle();
            String status = alarm.isActive() ? "activated" : "deactivated";
            ConsoleUtils.printLine("Alarm #" + alarmId + " " + status);
            return true;
        }
        return false;
    }

    public boolean snoozeAlarm(Long alarmId, int minutes) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null && alarm.isActive()) {
            alarm.snooze(minutes);
            ConsoleUtils.printLine("Alarm #" + alarmId + " is postponed for " + minutes + " minutes");
            return true;
        }
        return false;
    }

    public List<Alarm> getAllAlarms() {
        return new ArrayList<>(alarms);
    }

    public List<Alarm> getActiveAlarms() {
        return alarms.stream()
                .filter(Alarm::isActive)
                .collect(Collectors.toList());
    }

    public List<Alarm> getTodayAlarms() {
        return alarms.stream()
                .filter(alarm -> alarm.isActive() && alarm.isToday())
                .collect(Collectors.toList());
    }

    public List<Alarm> getUpcomingAlarms() {
        return alarms.stream()
                .filter(Alarm::isActive)
                .sorted(Comparator.comparing(Alarm::getAlarmTime))
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Alarm> getRecurringAlarms() {
        return alarms.stream()
                .filter(Alarm::isRecurring)
                .collect(Collectors.toList());
    }

    public List<Alarm> getExpiredAlarms() {
        return alarms.stream()
                .filter(Alarm::isExpired)
                .collect(Collectors.toList());
    }

    public List<Alarm> searchAlarmsByMessage(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return alarms.stream()
                .filter(alarm -> alarm.getMessage().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    private void startAlarmChecker() {
        alarmTimer = new Timer("AlarmChecker", true);
        alarmTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAlarms();
            }
        }, 0, 30000); // Проверка каждые 30 секунд
    }

    private void triggerAlarm(Alarm alarm) {
        ConsoleUtils.printLine("\nALARM GOES OFF!");
        ConsoleUtils.printLine(alarm.getMessage());
        ConsoleUtils.printLine("Time: " + alarm.getFormattedAlarmTime());

        alarm.trigger();

        // Предложить отложить
        if (alarm.isActive() && !alarm.isRecurring()) {
            ConsoleUtils.printLine("Press Enter to postpone for 5 minutes...");
            new Scanner(System.in).nextLine();
            snoozeAlarm(alarm.getId(), 5);
        }
    }

    private void checkAlarms() {
        for (Alarm alarm : getActiveAlarms()) {
            if (alarm.shouldTrigger()) {
                triggerAlarm(alarm);
            }
        }
    }

    public void clearAllAlarms() {
        if (ConsoleUtils.confirmAction("Are you sure you want to delete all the alarms?")) {
            alarms.clear();
            idCounter.set(1);
            ConsoleUtils.printLine("All alarms were deleted");
        }
    }

    public void shutdown() {
        if (this.alarmTimer != null) {
            alarmTimer.cancel();
        }
        ConsoleUtils.printInfo("Alarm manager is stopped");
    }

    public int getAlarmCount() {
        return alarms.size();
    }

    // === СТАТИСТИКА ===
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("All alarms", alarms.size());
        stats.put("Active", getActiveAlarms().size());
        stats.put("Today", getTodayAlarms().size());
        stats.put("Repeated", getRecurringAlarms().size());
        stats.put("Expired", getExpiredAlarms().size());

        return stats;
    }

    public void showSummary() {
        Map<String, Integer> stats = getStatistics();

        ConsoleUtils.printTitle("ALARMS SUMMARY");
        stats.forEach((key, value) ->
                ConsoleUtils.printLine(String.format("| %-20s: %d", key, value))
        );

        List<Alarm> upcoming = getUpcomingAlarms();
        if (!upcoming.isEmpty()) {
            ConsoleUtils.printLine("|");
            ConsoleUtils.printLine("| Closest alarms:");
            upcoming.forEach(alarm ->
                    ConsoleUtils.printLine(String.format("|   %s - %s",
                            TimeUtils.formatTime(alarm.getAlarmTime()), alarm.getMessage()))
            );
        }
        ConsoleUtils.printSeparator();
    }
}