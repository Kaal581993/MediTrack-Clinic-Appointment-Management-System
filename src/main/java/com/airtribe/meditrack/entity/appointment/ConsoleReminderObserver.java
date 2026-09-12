package com.airtribe.meditrack.entity.appointment;

import com.airtribe.meditrack.util.DateUtil;

public class ConsoleReminderObserver implements AppointmentObserver {

    private final String label;

    public ConsoleReminderObserver() {
        this("REMINDER");
    }

    public ConsoleReminderObserver(String label) {
        this.label = label;
    }

    @Override
    public void onBooked(Appointment appointment) {
        print("Appointment #" + appointment.getAppointment_id() + " booked for "
                + patientOf(appointment) + " with Dr. " + doctorOf(appointment)
                + " on " + DateUtil.formatDate(appointment.getAppointment_date()) + ".");
    }

    @Override
    public void onCancelled(Appointment appointment) {
        print("Appointment #" + appointment.getAppointment_id() + " for " + patientOf(appointment)
                + " has been CANCELLED.");
    }

    @Override
    public void onRescheduled(Appointment appointment) {
        print("Appointment #" + appointment.getAppointment_id() + " for " + patientOf(appointment)
                + " moved to " + DateUtil.formatDate(appointment.getAppointment_date()) + ".");
    }

    @Override
    public void onCompleted(Appointment appointment) {
        print("Appointment #" + appointment.getAppointment_id() + " for " + patientOf(appointment)
                + " is COMPLETED. Bill can now be generated.");
    }

    private void print(String message) {
        System.out.println("[" + label + "] " + message);
    }

    private String patientOf(Appointment appointment) {
        return (appointment.getPatient() == null)
                ? "unknown patient"
                : appointment.getPatient().getF_name() + " " + appointment.getPatient().getL_name();
    }

    private String doctorOf(Appointment appointment) {
        return (appointment.getDoctor() == null)
                ? "unknown doctor"
                : appointment.getDoctor().getF_name() + " " + appointment.getDoctor().getL_name();
    }
}
