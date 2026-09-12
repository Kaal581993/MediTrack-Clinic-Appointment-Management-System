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

public class AppointmentCSVUtil {

    private AppointmentCSVUtil() {
    }

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

    public static void save(String path, List<Appointment> appointments) {
        List<String> lines = new ArrayList<>();
        lines.add(Constants.APPOINTMENT_CSV_HEADER);
        for (Appointment appointment : appointments) {
            lines.add(toRow(appointment));
        }
        CSVUtil.writeLines(path, lines);
    }

    public static List<Appointment> load(String path) {
        return load(path, null, null);
    }

    public static List<Appointment> load(String path,
                                         IntFunction<Doctor> doctorLookup,
                                         IntFunction<Patient> patientLookup) {
        List<Appointment> appointments = new ArrayList<>();
        List<String> lines = CSVUtil.readLines(path);

        for (String line : lines) {
            if (line.startsWith("appointment_id")) {
                continue;
            }
            String[] fields = CSVUtil.splitRow(line);
            if (fields.length < 9) {
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

        appointment.setAppointment_id(Integer.parseInt(fields[0]));
        return appointment;
    }


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
