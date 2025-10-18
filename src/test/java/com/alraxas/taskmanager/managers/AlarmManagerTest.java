package com.alraxas.taskmanager.managers;


import com.alraxas.taskmanager.models.Alarm;
import com.alraxas.taskmanager.utils.TimeUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlarmManagerTest {

    @Test
    public void shouldCreateAlarmWithValidParameters() {
        Alarm alarm = new Alarm(1L, "test", TimeUtils.parseTimeToday("22:00"));

        assertEquals("test", alarm.getMessage());
        assertEquals(TimeUtils.parseTimeToday("22:00"), alarm.getAlarmTime());
    }

    private AlarmManager alarmManager = new AlarmManager();

    @Test
    public void testCreateAndRetrieveTask() {
        Alarm alarm =  new Alarm(1L, "test", TimeUtils.parseTimeToday("22:00"));
        assertNotNull(alarm);
        assertEquals(1, alarmManager.getAllAlarms().size());
    }

    @Test
    public void testDeactivateAlarm() {
        Alarm alarm =  new Alarm(1L, "test", TimeUtils.parseTimeToday("22:00"));
        assertTrue(alarm.isActive());

        alarmManager.deactivateAlarm(alarm.getId());
        assertFalse(alarm.isActive());
    }
}