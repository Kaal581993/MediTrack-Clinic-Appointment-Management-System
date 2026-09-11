package com.airtribe.meditrack.ui;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.appointment.ConsoleReminderObserver;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.entity.persons.Specialization;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AppointmentMenu {

    private final AppointmentService service;
    private final Scanner scanner;

    private final List<Doctor> doctors = new ArrayList<>();
    private final List<Patient> patients = new ArrayList<>();

    public AppointmentMenu() {
        this(new AppointmentService(), new Scanner(System.in));
    }

    public AppointmentMenu(AppointmentService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
        this.service.register(new ConsoleReminderObserver());
    }

    public static void start(String[] args) {
        AppointmentMenu menu = new AppointmentMenu();
        menu.seedDemoPeople();
        if (hasFlag(args, "--loadData")) {
            menu.loadPersistedData();
        }
        menu.run();
    }

    public static void main(String[] args) {
        start(args);
    }

    private static boolean hasFlag(String[] args, String flag) {
        if (args == null) {
            return false;
        }
        for (String arg : args) {
            if (flag.equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }

    private void loadPersistedData() {
        try {
            int loaded = service.loadFromFile();
            System.out.println("Loaded " + loaded + " appointment(s) from " + service.getStoragePath());
        } catch (RuntimeException e) {
            System.out.println("Could not load persisted data: " + e.getMessage());
        }
    }

    public void seedDemoPeople() {
        Doctor.DoctorBuilder cardio = Doctor.builder();
        cardio.f_name("Asha").l_name("Rao").age(44).gender(Gender.FEMALE);
        doctors.add(cardio.specialization(Specialization.MBBS).fees(500.0).build());

        Doctor.DoctorBuilder ortho = Doctor.builder();
        ortho.f_name("Vikram").l_name("Singh").age(51).gender(Gender.MALE);
        doctors.add(ortho.specialization(Specialization.ORTHOPEDIC).fees(700.0).build());

        Patient.PatientBuilder first = Patient.builder();
        first.f_name("Neha").l_name("Sharma").age(29).gender(Gender.FEMALE);
        patients.add(first.medical_history("No known allergies").build());

        Patient.PatientBuilder second = Patient.builder();
        second.f_name("Imran").l_name("Khan").age(36).gender(Gender.MALE);
        patients.add(second.medical_history("Asthma").build());
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> bookAppointment();
                    case "2" -> viewAll();
                    case "3" -> viewById();
                    case "4" -> searchMenu();
                    case "5" -> cancelAppointment();
                    case "6" -> rescheduleAppointment();
                    case "7" -> completeAppointment();
                    case "8" -> showAnalytics();
                    case "9" -> saveData();
                    case "10" -> loadPersistedData();
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option: " + choice);
                }
            } catch (AppointmentNotFoundException e) {
                System.out.println("Not found: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Cannot do that: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Leaving the appointment module.");
    }

    private void printMenu() {
        System.out.println("===== MediTrack : Appointments =====");
        System.out.println(" 1. Book appointment");
        System.out.println(" 2. View all appointments");
        System.out.println(" 3. View appointment by id");
        System.out.println(" 4. Search appointments");
        System.out.println(" 5. Cancel appointment");
        System.out.println(" 6. Reschedule appointment");
        System.out.println(" 7. Mark appointment completed");
        System.out.println(" 8. Analytics");
        System.out.println(" 9. Save to CSV");
        System.out.println("10. Load from CSV");
        System.out.println(" 0. Back");
        System.out.print("Choice: ");
    }

    private void bookAppointment() {
        if (doctors.isEmpty() || patients.isEmpty()) {
            System.out.println("No doctors or patients available.");
            return;
        }

        System.out.println("-- Doctors --");
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.println("  " + i + ". Dr. " + doctor.getF_name() + " " + doctor.getL_name());
        }
        Doctor doctor = doctors.get(readIndex("Select doctor: ", doctors.size()));

        System.out.println("-- Patients --");
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            System.out.println("  " + i + ". " + patient.getF_name() + " " + patient.getL_name());
        }
        Patient patient = patients.get(readIndex("Select patient: ", patients.size()));

        System.out.print("Appointment type (1 = INITIAL, 2 = FOLLOWUP): ");
        AppointmentType type = "2".equals(scanner.nextLine().trim())
                ? AppointmentType.FOLLOWUP
                : AppointmentType.INITIAL;

        System.out.print("Date and time (" + Constants.DATE_FORMAT + "): ");
        Date date = DateUtil.parseDateInput(scanner.nextLine());

        Appointment appointment = service.bookAppointment(date, type, doctor, patient);
        System.out.println("Booked: " + appointment);
    }

    private void viewAll() {
        List<Appointment> all = service.sortedByDate();
        if (all.isEmpty()) {
            System.out.println("No appointments yet.");
            return;
        }
        all.forEach(System.out::println);
    }

    private void viewById() {
        System.out.print("Appointment id: ");
        System.out.println(service.viewAppointment(readInt()));
    }

    private void searchMenu() {
        System.out.println(" a. By patient name");
        System.out.println(" b. By status");
        System.out.println(" c. By date range");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim().toLowerCase();

        List<Appointment> results;
        switch (choice) {
            case "a" -> {
                System.out.print("Patient name: ");
                results = service.searchAppointment(scanner.nextLine());
            }
            case "b" -> {
                System.out.print("Status (PENDING/CONFIRMED/CANCELLED/RESCHEDULED/COMPLETED): ");
                results = service.searchAppointment(
                        AppointmentStatus.valueOf(scanner.nextLine().trim().toUpperCase()));
            }
            case "c" -> {
                System.out.print("From (" + Constants.DATE_FORMAT + "): ");
                Date from = DateUtil.parseDateInput(scanner.nextLine());
                System.out.print("To (" + Constants.DATE_FORMAT + "): ");
                results = service.searchAppointment(from, DateUtil.parseDateInput(scanner.nextLine()));
            }
            default -> {
                System.out.println("Unknown search option.");
                return;
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching appointments.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void cancelAppointment() {
        System.out.print("Appointment id to cancel: ");
        service.cancelAppointment(readInt());
    }

    private void rescheduleAppointment() {
        System.out.print("Appointment id to reschedule: ");
        int id = readInt();
        System.out.print("New date and time (" + Constants.DATE_FORMAT + "): ");
        service.rescheduleAppointment(id, DateUtil.parseDateInput(scanner.nextLine()));
    }

    private void completeAppointment() {
        System.out.print("Appointment id to complete: ");
        service.completeAppointment(readInt());
    }

    private void showAnalytics() {
        System.out.println("Total appointments : " + service.listAll().size());
        System.out.printf("Average fee        : %.2f%n", service.averageFee());
        System.out.printf("Total revenue      : %.2f%n", service.totalRevenue());

        System.out.println("Count by status    :");
        for (Map.Entry<AppointmentStatus, Long> entry : service.countByStatus().entrySet()) {
            System.out.println("   " + entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("Appointments per doctor id:");
        for (Map.Entry<Integer, Long> entry : service.appointmentsPerDoctor().entrySet()) {
            System.out.println("   doctor #" + entry.getKey() + " -> " + entry.getValue());
        }

        List<Appointment> upcoming = service.upcomingAppointments();
        System.out.println("Upcoming           : " + upcoming.size());
        upcoming.forEach(a -> System.out.println("   " + a));
    }

    private void saveData() {
        int saved = service.saveToFile();
        System.out.println("Saved " + saved + " appointment(s) to " + service.getStoragePath());
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a whole number.", e);
        }
    }

    private int readIndex(String prompt, int size) {
        System.out.print(prompt);
        int index = readInt();
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Choose a number between 0 and " + (size - 1) + ".");
        }
        return index;
    }
}
