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

    private String day = null;
    private int dayIndex = 0;
    private String startTime = null;
    private String endTime = null;


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
