package com.alraxas.taskmanager.managers;

import com.alraxas.taskmanager.models.Alarm;
import com.alraxas.taskmanager.utils.ConsoleUtils;
import com.alraxas.taskmanager.utils.TimeUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class AlarmManager {
    private List<Alarm> alarms;
    private AtomicLong idCounter;
    private Timer alarmTimer;

    public AlarmManager() {
        this.alarms = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
    }

    public Alarm setAlarm(Alarm alarm) {
        alarms.add(alarm);
        ConsoleUtils.printLine("Alarm is set: " + alarm.getFormattedAlarmTime());
        return alarm;
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
        for (Alarm alarm : alarms) {
            if (alarm.getId().equals(alarmId)) {
                return alarm;
            }
        }
        return null;
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
        List<Alarm> activeAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.isActive()) {
                activeAlarms.add(alarm);
            }
        }
        return activeAlarms;
    }

    public List<Alarm> getTodayAlarms() {
        List<Alarm> todayAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.isActive() && alarm.isToday()) {
                todayAlarms.add(alarm);
            }
        }
        return todayAlarms;
    }

    public List<Alarm> getUpcomingAlarms() {
        List<Alarm> activeAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.isActive()) {
                activeAlarms.add(alarm);
            }
        }
        activeAlarms.sort(Comparator.comparing(Alarm::getAlarmTime));
        return activeAlarms.subList(0, Math.min(5, activeAlarms.size()));
    }

    public List<Alarm> getRecurringAlarms() {
        List<Alarm> recurringAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.isRecurring()) {
                recurringAlarms.add(alarm);
            }
        }
        return recurringAlarms;
    }

    public List<Alarm> getExpiredAlarms() {
        List<Alarm> expiredAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.isExpired()) {
                expiredAlarms.add(alarm);
            }
        }
        return expiredAlarms;
    }

    public List<Alarm> searchAlarmsByMessage(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        List<Alarm> searchedAlarms = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (alarm.getMessage().toLowerCase().contains(lowerKeyword)) {
                searchedAlarms.add(alarm);
            }
        }
        return searchedAlarms;
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

    public void stopAlarmChecking() {
        if (alarmTimer != null) {
            alarmTimer.cancel();
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

    public void startAlarmChecker() {
        alarmTimer = new Timer("AlarmChecker", true);
        alarmTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAlarms();
            }
        }, 0, 1000);
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