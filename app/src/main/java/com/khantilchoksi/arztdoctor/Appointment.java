package com.khantilchoksi.arztdoctor;

/**
 * Created by khantilchoksi on 29/03/17.
 */

public class Appointment {
    private String appointmentId;
    private String patientName;

    public String getClinicName() {
        return clinicName;
    }

    private String clinicName;

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public String getAppointmentStartTime() {
        return appointmentStartTime;
    }

    public String getAppointmentEndTime() {
        return appointmentEndTime;
    }

    public Appointment(String appointmentId, String patientName, String appointmentDate, String appointmentDay, String appointmentStartTime, String appointmentEndTime, String clinicName) {

        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.appointmentDate = appointmentDate;
        this.appointmentDay = appointmentDay;
        this.appointmentStartTime = appointmentStartTime;
        this.appointmentEndTime = appointmentEndTime;
        this.clinicName = clinicName;
    }

    private String appointmentDate;
    private String appointmentDay;
    private String appointmentStartTime;
    private String appointmentEndTime;

}
