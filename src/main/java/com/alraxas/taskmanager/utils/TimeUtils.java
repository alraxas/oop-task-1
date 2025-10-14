package com.alraxas.taskmanager.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Wrong date and time format. Please use: dd.MM.yyyy HH:mm");
        }
    }

    public static LocalDateTime parseTimeToday(String timeString) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime time = LocalDateTime.parse(
                    now.toLocalDate() + "T" + timeString + ":00"
            );
            if (time.isBefore(now)) { // если время уже прошло сегодня, устанавливаем на завтра
                time = time.plusDays(1);
            }
            return time;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Wrong time format. Please use: HH:mm");
        }
    }

    public static boolean isTimeInFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }

    public static String getTimeUntilAlarm(LocalDateTime alarmTime) {
        LocalDateTime now = LocalDateTime.now();
        if (alarmTime.isBefore(now)) {
            return "PASSED";
        }

        long seconds = java.time.Duration.between(now, alarmTime).getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return String.format("in %d:%d", hours, minutes);
        } else {
            return String.format("in 00:%d", minutes);
        }
    }

    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    public static boolean isTomorrow(LocalDateTime dateTime) {
        return dateTime.toLocalDate().equals(LocalDateTime.now().plusDays(1).toLocalDate());
    }

    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(23, 59, 59);
    }

    public static java.util.List<LocalDateTime> getNextWeekDays() {
        java.util.List<LocalDateTime> days = new java.util.ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        for (int i = 0; i < 7; i++) {
            days.add(today.plusDays(i));
        }

        return days;
    }
}