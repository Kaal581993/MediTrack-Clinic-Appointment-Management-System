package com.airtribe.meditrack.entity.appointment;

/**
 * Observer contract for appointment lifecycle events.
 * <p>
 * Implementations register with the appointment service and are notified whenever an
 * appointment is booked, cancelled, rescheduled or completed. This keeps notification
 * concerns (console reminders, and later email/SMS) out of the service itself.
 * <p>
 * Every method is a default no-op so an observer only overrides the events it cares
 * about.
 */
public interface AppointmentObserver {

    /**
     * @param appointment the appointment that was just booked
     */
    default void onBooked(Appointment appointment) {
    }

    /**
     * @param appointment the appointment that was just cancelled
     */
    default void onCancelled(Appointment appointment) {
    }

    /**
     * @param appointment the appointment that was just moved to a new slot
     */
    default void onRescheduled(Appointment appointment) {
    }

    /**
     * @param appointment the appointment whose consultation has taken place
     */
    default void onCompleted(Appointment appointment) {
    }
}
