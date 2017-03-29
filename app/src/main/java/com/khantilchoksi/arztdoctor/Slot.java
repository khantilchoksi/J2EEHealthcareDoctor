package com.khantilchoksi.arztdoctor;

/**
 * Created by khantilchoksi on 28/03/17.
 */

public class Slot {
    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Slot() {

    }

    private String slotId;
    private String day = null;
    private int dayIndex = 0;
    private String startTime = null;
    private String endTime = null;
    private int slotFees;

    public String getSlotId() {
        return slotId;
    }

    public int getSlotFees() {
        return slotFees;
    }

    public Slot(String slotId, String day, String startTime, String endTime, int slotFees) {

        this.slotId = slotId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotFees = slotFees;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }
}
