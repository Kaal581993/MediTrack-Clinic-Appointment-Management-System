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
        String[] row = new String[]{
                String.valueOf(appointment.getAppointment_id()),
                DateUtil.formatDateTime(appointment.getAppointment_date()),
                String.valueOf(appointment.getStatus()),
                String.valueOf(appointment.getType()),
                String.valueOf(appointment.getAppointmentFees()),
                String.valueOf((doctor == null) ? 0 : doctor.getP_id()),
                (doctor == null) ? "" : doctor.getF_name() + " " + doctor.getL_name(),
                String.valueOf((patient == null) ? 0 : patient.getP_id()),
                (patient == null) ? "" : patient.getF_name() + " " + patient.getL_name()
        };
        return CSVUtil.joinRow(row);
    }

    public static void save(String path, List<Appointment> appointments) {
        List<String[]> lines = new ArrayList<>();
        lines.add(Constants.APPOINTMENT_CSV_HEADER.split(","));
        for (Appointment appointment : appointments) {
            lines.add(CSVUtil.splitRow(toRow(appointment)));
        }
        try {
            CSVUtil.writeLines(path, lines);
        } catch (java.io.IOException e) {
            System.err.println("Error saving appointments to CSV: " + e.getMessage());
        }
    }

    public static List<Appointment> load(String path) {
        return load(path, null, null);
    }

    public static List<Appointment> load(String path,
                                         IntFunction<Doctor> doctorLookup,
                                         IntFunction<Patient> patientLookup) {
        List<Appointment> appointments = new ArrayList<>();
        try {
            List<String[]> lines = CSVUtil.readLines(path);

            for (String[] line : lines) {
                if (line.length > 0 && line[0].startsWith("appointment_id")) {
                    continue;
                }
                if (line.length < 9) {
                    System.out.println("[CSV] Skipping malformed appointment row: " + String.join(",", line));
                    continue;
                }
                try {
                    appointments.add(rowToAppointment(line, doctorLookup, patientLookup));
                } catch (RuntimeException e) {
                    System.out.println("[CSV] Skipping unreadable appointment row: " + String.join(",", line)
                            + " (" + e.getMessage() + ")");
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Error loading appointments from CSV: " + e.getMessage());
        }
        return appointments;
    }

    private static Appointment rowToAppointment(String[] fields,
                                                IntFunction<Doctor> doctorLookup,
                                                IntFunction<Patient> patientLookup) {
        try {
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
                    .appointment_date(DateUtil.parseDateTime(fields[1]))
                    .status(status)
                    .type(type)
                    .appointmentFees(fees)
                    .doctor(doctor)
                    .patient(patient)
                    .build();

            appointment.setAppointment_id(Integer.parseInt(fields[0]));
            return appointment;
        } catch (java.text.ParseException e) {
            throw new RuntimeException("Failed to parse date: " + e.getMessage(), e);
        }
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
