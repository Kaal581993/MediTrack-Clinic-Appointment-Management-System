package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Maps {@link Appointment} objects to and from CSV rows.
 * <p>
 * Row format is defined by {@link Constants#APPOINTMENT_CSV_HEADER}. Reading and
 * writing of the raw file is delegated to {@link CSVUtil}, so this class only owns
 * appointment-specific mapping.
 * <p>
 * Doctor and patient records are resolved through caller-supplied lookup functions.
 * That keeps this utility independent of the doctor and patient service modules: until
 * those expose a registry, {@link #load(String)} reconstructs lightweight stand-ins
 * from the names stored in the row.
 */
public class AppointmentCSVUtil {

    /** Utility holder — not meant to be instantiated. */
    private AppointmentCSVUtil() {
    }

    /**
     * Converts one appointment into a CSV row.
     *
     * @param appointment the appointment to serialise
     * @return the CSV row, without a trailing newline
     */
    public static String toRow(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();
        return CSVUtil.joinRow(
                appointment.getAppointment_id(),
                DateUtil.format(appointment.getAppointment_date()),
                appointment.getStatus(),
                appointment.getType(),
                appointment.getAppointmentFees(),
                (doctor == null) ? 0 : doctor.getP_id(),
                (doctor == null) ? "" : doctor.getF_name() + " " + doctor.getL_name(),
                (patient == null) ? 0 : patient.getP_id(),
                (patient == null) ? "" : patient.getF_name() + " " + patient.getL_name());
    }

    /**
     * Writes appointments to a CSV file, header included.
     *
     * @param path         destination file
     * @param appointments the appointments to write
     */
    public static void save(String path, List<Appointment> appointments) {
        List<String> lines = new ArrayList<>();
        lines.add(Constants.APPOINTMENT_CSV_HEADER);
        for (Appointment appointment : appointments) {
            lines.add(toRow(appointment));
        }
        CSVUtil.writeLines(path, lines);
    }

    /**
     * Reads appointments from a CSV file, rebuilding doctor and patient stand-ins from
     * the names stored in each row.
     *
     * @param path source file
     * @return the loaded appointments, or an empty list when the file does not exist
     */
    public static List<Appointment> load(String path) {
        return load(path, null, null);
    }

    /**
     * Reads appointments from a CSV file, resolving people through the supplied lookups.
     * <p>
     * Pass the doctor and patient services' own lookups here once they exist, so loaded
     * appointments point at the real shared records. A lookup may be {@code null}, or may
     * return {@code null} for an unknown id, in which case a stand-in is rebuilt from the
     * name in the row.
     *
     * @param path          source file
     * @param doctorLookup  resolves a doctor by person id; may be {@code null}
     * @param patientLookup resolves a patient by person id; may be {@code null}
     * @return the loaded appointments, or an empty list when the file does not exist
     */
    public static List<Appointment> load(String path,
                                         IntFunction<Doctor> doctorLookup,
                                         IntFunction<Patient> patientLookup) {
        List<Appointment> appointments = new ArrayList<>();
        List<String> lines = CSVUtil.readLines(path);

        for (String line : lines) {
            // Skip the header row.
            if (line.startsWith("appointment_id")) {
                continue;
            }
            String[] fields = CSVUtil.splitRow(line);
            if (fields.length < 9) {
                // Malformed row — skip rather than abort the whole load.
                System.out.println("[CSV] Skipping malformed appointment row: " + line);
                continue;
            }
            try {
                appointments.add(rowToAppointment(fields, doctorLookup, patientLookup));
            } catch (RuntimeException e) {
                System.out.println("[CSV] Skipping unreadable appointment row: " + line
                        + " (" + e.getMessage() + ")");
            }
        }
        return appointments;
    }

    private static Appointment rowToAppointment(String[] fields,
                                                IntFunction<Doctor> doctorLookup,
                                                IntFunction<Patient> patientLookup) {
        AppointmentStatus status = AppointmentStatus.valueOf(fields[2]);
        AppointmentType type = AppointmentType.valueOf(fields[3]);
        double fees = Double.parseDouble(fields[4]);
        int doctorId = Integer.parseInt(fields[5]);
        int patientId = Integer.parseInt(fields[7]);

        Doctor doctor = (doctorLookup == null) ? null : doctorLookup.apply(doctorId);
        if (doctor == null) {
            doctor = rebuildDoctor(fields[6]);
        }
        Patient patient = (patientLookup == null) ? null : patientLookup.apply(patientId);
        if (patient == null) {
            patient = rebuildPatient(fields[8]);
        }

        Appointment appointment = new Appointment.AppointmentBuilder()
                .appointment_date(DateUtil.parse(fields[1]))
                .status(status)
                .type(type)
                .appointmentFees(fees)
                .doctor(doctor)
                .patient(patient)
                .build();

        // The id is regenerated by the constructor, so restore the persisted one to keep
        // ids stable across a save/load round trip.
        appointment.setAppointment_id(Integer.parseInt(fields[0]));
        return appointment;
    }

    // The shared PersonBuilder fluent setters return PersonBuilder rather than the
    // concrete subtype, so the chain is built first and build() called separately; the
    // DoctorBuilder/PatientBuilder override of build() still returns the concrete type.
    // Those builders are owned by the doctor/patient modules, so they are used as-is.

    private static Doctor rebuildDoctor(String fullName) {
        String[] parts = splitName(fullName);
        Doctor.DoctorBuilder builder = Doctor.builder();
        builder.f_name(parts[0]).l_name(parts[1]).gender(Gender.OTHER);
        return builder.build();
    }

    private static Patient rebuildPatient(String fullName) {
        String[] parts = splitName(fullName);
        Patient.PatientBuilder builder = Patient.builder();
        builder.f_name(parts[0]).l_name(parts[1]).gender(Gender.OTHER);
        return builder.build();
    }

    private static String[] splitName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[]{"Unknown", ""};
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }
}
