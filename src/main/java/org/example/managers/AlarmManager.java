package org.example.managers;

import org.example.models.Alarm;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class AlarmManager {
    private List<Alarm> alarms;
    private AtomicLong idCounter;
    private Timer alarmTimer;
    private boolean autoSave;

    public AlarmManager() {
        this.alarms = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
        this.autoSave = true;
    }

    public Alarm setAlarm(String message, LocalDateTime alarmTime) {
        Alarm alarm = new Alarm(idCounter.getAndIncrement(), message, alarmTime);
        alarms.add(alarm);
//        if (autoSave) autoSave();
//        ConsoleHelper.printSuccess("Будильник установлен: " + alarm.getFormattedAlarmTime());
        return alarm;
    }

    public Alarm setAlarm(String message, LocalDateTime alarmTime, boolean isRecurring) {
        Alarm alarm = new Alarm(idCounter.getAndIncrement(), message, alarmTime, isRecurring);
        alarms.add(alarm);
//        if (autoSave) autoSave();
//        ConsoleHelper.printSuccess("Будильник установлен: " + alarm.getFormattedAlarmTime() +
//                (isRecurring ? " (повторяющийся)" : ""));
        return alarm;
    }

    public Alarm setQuickAlarm(String message, String timeString) {
        Alarm alarm = new Alarm(idCounter.getAndIncrement(), message, timeString);
        alarms.add(alarm);
//        if (autoSave) autoSave();
//        ConsoleHelper.printSuccess("Будильник установлен на: " + timeString);
        return alarm;
    }

    /**
     * Удаление будильника по ID
     */
    public boolean removeAlarm(Long alarmId) {
        boolean removed = alarms.removeIf(alarm -> alarm.getId().equals(alarmId));
        if (removed) {
//            if (autoSave) autoSave();
//            ConsoleHelper.printSuccess("Будильник #" + alarmId + " удален");
        } else {
//            ConsoleHelper.printError("Будильник #" + alarmId + " не найден");
        }
        return removed;
    }

    /**
     * Получение будильника по ID
     */
    public Alarm getAlarmById(Long alarmId) {
        return alarms.stream()
                .filter(alarm -> alarm.getId().equals(alarmId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Активировать будильник
     */
    public boolean activateAlarm(Long alarmId) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.activate();
//            if (autoSave) autoSave();
//            ConsoleHelper.printSuccess("Будильник #" + alarmId + " активирован");
            return true;
        }
        return false;
    }

    /**
     * Деактивировать будильник
     */
    public boolean deactivateAlarm(Long alarmId) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.deactivate();
//            if (autoSave) autoSave();
//            ConsoleHelper.printSuccess("Будильник #" + alarmId + " деактивирован");
            return true;
        }
        return false;
    }

    /**
     * Переключить статус будильника
     */
    public boolean toggleAlarm(Long alarmId) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            alarm.toggle();
//            if (autoSave) autoSave();
            String status = alarm.isActive() ? "активирован" : "деактивирован";
//            ConsoleHelper.printSuccess("Будильник #" + alarmId + " " + status);
            return true;
        }
        return false;
    }

    /**
     * Отложить будильник
     */
    public boolean snoozeAlarm(Long alarmId, int minutes) {
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null && alarm.isActive()) {
            alarm.snooze(minutes);
//            if (autoSave) autoSave();
//            ConsoleHelper.printSuccess("Будильник #" + alarmId + " отложен на " + minutes + " минут");
            return true;
        }
        return false;
    }

    /**
     * Получить все будильники
     */
    public List<Alarm> getAllAlarms() {
        return new ArrayList<>(alarms);
    }

    /**
     * Получить активные будильники
     */
    public List<Alarm> getActiveAlarms() {
        return alarms.stream()
                .filter(Alarm::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Получить будильники на сегодня
     */
    public List<Alarm> getTodayAlarms() {
        return alarms.stream()
                .filter(alarm -> alarm.isActive() && alarm.isToday())
                .collect(Collectors.toList());
    }

    /**
     * Получить ближайшие будильники
     */
    public List<Alarm> getUpcomingAlarms() {
        return alarms.stream()
                .filter(Alarm::isActive)
                .sorted(Comparator.comparing(Alarm::getAlarmTime))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Получить повторяющиеся будильники
     */
    public List<Alarm> getRecurringAlarms() {
        return alarms.stream()
                .filter(Alarm::isRecurring)
                .collect(Collectors.toList());
    }

    /**
     * Получить просроченные будильники
     */
    public List<Alarm> getExpiredAlarms() {
        return alarms.stream()
                .filter(Alarm::isExpired)
                .collect(Collectors.toList());
    }

    /**
     * Поиск будильников по сообщению
     */
    public List<Alarm> searchAlarmsByMessage(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return alarms.stream()
                .filter(alarm -> alarm.getMessage().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Запуск проверки будильников
     */
    private void startAlarmChecker() {
        alarmTimer = new Timer("AlarmChecker", true);
        alarmTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAlarms();
            }
        }, 0, 30000); // Проверка каждые 30 секунд
    }

    /**
     * Обработка сработавшего будильника
     */
    private void triggerAlarm(Alarm alarm) {
        // В реальном приложении здесь был бы звуковой сигнал
//        ConsoleHelper.printMessage("\n🚨🚨🚨 БУДИЛЬНИК СРАБОТАЛ! 🚨🚨🚨");
//        ConsoleHelper.printMessage("📢 " + alarm.getMessage());
//        ConsoleHelper.printMessage("⏰ Время: " + alarm.getFormattedAlarmTime());

        alarm.trigger();
//        if (autoSave) autoSave();

        // Предложить отложить
        if (alarm.isActive() && !alarm.isRecurring()) {
//            ConsoleHelper.printMessage("Нажмите Enter для откладывания на 5 минут...");
            new Scanner(System.in).nextLine();
            snoozeAlarm(alarm.getId(), 5);
        }
    }

    /**
     * Проверка сработавших будильников
     */
    private void checkAlarms() {
        for (Alarm alarm : getActiveAlarms()) {
            if (alarm.shouldTrigger()) {
                triggerAlarm(alarm);
            }
        }
    }

    /**
     * Очистка всех будильников
     */
    public void clearAllAlarms() {
//        if (ConsoleHelper.confirmAction("Вы уверены, что хотите удалить все будильники?")) {
            alarms.clear();
            idCounter.set(1);
//            if (autoSave) autoSave();
//            ConsoleHelper.printSuccess("Все будильники удалены");
        }

    /**
     * Остановка менеджера будильников
     */
    public void shutdown() {
        if (this.alarmTimer != null) {
            alarmTimer.cancel();
        }
//        ConsoleHelper.printInfo("Менеджер будильников остановлен");
    }

    /**
     * Установка автосохранения
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
//        ConsoleHelper.printInfo("Автосохранение: " + (autoSave ? "включено" : "выключено"));
    }

    /**
     * Получить количество будильников
     */
    public int getAlarmCount() {
        return alarms.size();
    }
}