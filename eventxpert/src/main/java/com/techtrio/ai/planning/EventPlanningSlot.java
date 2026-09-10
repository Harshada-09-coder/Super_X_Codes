package com.techtrio.ai.planning;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EventPlanningSlot {

    private String slotId;
    private String slotName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int startMinuteOfDay;
    private int durationMinutes;
    private int dayIndex;

    public EventPlanningSlot() {
    }

    public EventPlanningSlot(String slotId, String slotName, LocalDate date, LocalTime startTime, LocalTime endTime, int dayIndex) {
        this.slotId = slotId;
        this.slotName = slotName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startMinuteOfDay = startTime.getHour() * 60 + startTime.getMinute();
        this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        this.dayIndex = dayIndex;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getStartMinuteOfDay() {
        return startMinuteOfDay;
    }

    public void setStartMinuteOfDay(int startMinuteOfDay) {
        this.startMinuteOfDay = startMinuteOfDay;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public String getFormattedTimeRange() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a");
        return startTime.format(fmt) + " - " + endTime.format(fmt);
    }

    @Override
    public String toString() {
        return slotName != null ? slotName : slotId;
    }
}