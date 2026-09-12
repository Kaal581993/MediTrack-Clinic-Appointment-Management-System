package com.airtribe.meditrack.constants;

public class Constants {

    // ---------------------------------------------------------------------
    // Appointment module additions (Rohan). Appended only — nothing above
    // this line was modified, so other modules can add their own constants
    // below without conflicting.
    //
    // These are public static final (compile-time constants) rather than
    // instance fields with getters, so callers can use them without holding a
    // Constants instance. Note entity/Constants.java is a separate, older class
    // used by the billing module; this is the constants package the project
    // structure actually calls for.
    // ---------------------------------------------------------------------

    /** Application-wide date/time pattern used for parsing and display. */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    /** Relative path of the appointment CSV persistence file. */
    public static final String APPOINTMENT_FILE_PATH = "data/appointments.csv";

    /** Header row written to (and skipped when reading) the appointment CSV. */
    public static final String APPOINTMENT_CSV_HEADER =
            "appointment_id,appointment_date,status,type,fees,doctor_id,doctor_name,patient_id,patient_name";

    /** Field separator used by the CSV utilities. */
    public static final String CSV_SEPARATOR = ",";

    /** Consultation fee applied to a first (INITIAL) appointment. */
    public static final double INITIAL_APPOINTMENT_FEE = 500.0;

    /** Consultation fee applied to a FOLLOWUP appointment. */
    public static final double FOLLOWUP_APPOINTMENT_FEE = 100.0;

    /** Length of a single consultation slot, in minutes. */
    public static final int SLOT_DURATION_MINUTES = 30;

    /** First hour of the clinic day, 24-hour clock. */
    public static final int CLINIC_OPENING_HOUR = 9;

    /** Hour the clinic stops taking appointments, 24-hour clock. */
    public static final int CLINIC_CLOSING_HOUR = 17;

    /** Utility holder — not meant to be instantiated. */
    private Constants() {
    }
}
