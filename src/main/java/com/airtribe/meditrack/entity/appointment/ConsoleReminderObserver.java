package com.airtribe.meditrack.entity.appointment;

import com.airtribe.meditrack.util.DateUtil;

/**
 * {@link AppointmentObserver} that prints reminders to the console.
 * <p>
 * The simplest useful observer: it lets the service announce lifecycle changes
 * without knowing anything about how they are delivered. Swapping in an email or SMS
 * observer later requires no change to the service.
 */
public class ConsoleReminderObserver implements AppointmentObserver {

    private final String label;

    /** Creates an observer labelled "REMINDER". */
    public ConsoleReminderObserver() {
        this("REMINDER");
    }

    /**
     * @param label prefix printed before each notification, e.g. {@code "SMS"}
     */
    public ConsoleReminderObserver(String label) {
        this.label = label;
    }

    @Override
    public void onBooked(Appointment appointment) {
        print("Appointment #" + appointment.getAppointment_id() + " booked for "
                + patientOf(appointment) + " with Dr. " + doctorOf(appointment)
                + " on " + DateUtil.format(appointment.getAppointment_date()) + ".");
    }

    @Override
    public void onCancelled(Appointment appointment) {
        print("Appointment #" + appointment.getAppointment_id() + " for " + patientOf(appointment)
                + " has been CANCELLED.");
    }

    @Override
    public void onRescheduled(Appointment appointment) {
        print("Appointment #" + appointment.getAppointment_id() + " for " + patientOf(appointment)
                + " moved to " + DateUtil.format(appointment.getAppointment_date()) + ".");
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
