package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;

import java.util.Date;

public class Validator {

    // ---------------------------------------------------------------------
    // Appointment module additions (Rohan). Appended only — nothing above
    // this line was modified, so other modules can add their own validation
    // helpers below without conflicting.
    // ---------------------------------------------------------------------

    /**
     * Centralised validation for the inputs of a new appointment.
     * <p>
     * Throws rather than returning a boolean so the caller cannot ignore a failure,
     * and so the message explains exactly which field was wrong.
     *
     * @param date    requested slot; must be non-null and in the future
     * @param type    appointment type; must be non-null
     * @param fees    consultation fee; must not be negative
     * @param doctor  the doctor; must be non-null
     * @param patient the patient; must be non-null
     * @throws IllegalArgumentException when any field is missing or out of range
     */
    public static void validateAppointment(Date date,
                                           AppointmentType type,
                                           double fees,
                                           Doctor doctor,
                                           Patient patient) {
        if (doctor == null) {
            throw new IllegalArgumentException("Appointment must have a doctor.");
        }
        if (patient == null) {
            throw new IllegalArgumentException("Appointment must have a patient.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Appointment must have a type (INITIAL or FOLLOWUP).");
        }
        if (date == null) {
            throw new IllegalArgumentException("Appointment must have a date.");
        }
        if (!DateUtil.isFuture(date)) {
            throw new IllegalArgumentException(
                    "Appointment date must be in the future, but was: " + DateUtil.format(date));
        }
        if (fees < 0) {
            throw new IllegalArgumentException("Appointment fees must not be negative, but was: " + fees);
        }
    }

    /**
     * @param name the name to check
     * @return {@code true} when the name is non-null and not blank
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * @param id the id to check
     * @return {@code true} when the id is positive (generated ids start at 1)
     */
    public static boolean isValidId(int id) {
        return id > 0;
    }
}
