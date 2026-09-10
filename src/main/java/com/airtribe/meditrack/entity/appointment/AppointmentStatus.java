package com.airtribe.meditrack.entity.appointment;

/**
 * Lifecycle states an {@link Appointment} can be in.
 * <p>
 * A newly booked appointment starts as {@link #PENDING}. It may then be
 * {@link #CONFIRMED}, {@link #RESCHEDULED}, {@link #CANCELLED} or, once the
 * consultation has taken place, {@link #COMPLETED}.
 */
public enum AppointmentStatus {
    /** Booked but not yet confirmed by the clinic. */
    PENDING,
    /** Confirmed and scheduled to go ahead. */
    CONFIRMED,
    /** Called off by the patient or the clinic. */
    CANCELLED,
    /** Moved to a different date/time. */
    RESCHEDULED,
    /** Consultation has taken place. */
    COMPLETED
}
