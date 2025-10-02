package org.example;


import org.example.models.Alarm;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        demoAlarm();
    }

    public static void demoAlarm() {
        Alarm alarm1 = new Alarm(1L, "встать", LocalDateTime.now().plusMinutes(2));
        System.out.println(alarm1);

        alarm1.deactivate();
        System.out.println(alarm1.toString());
    }
}