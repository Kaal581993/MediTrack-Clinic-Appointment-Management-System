package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.entity.billing.BillSummary;
import com.airtribe.meditrack.entity.factory.BillFactory;
import com.airtribe.meditrack.entity.factory.BillFactoryImpl;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.DataStore;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PatientService patientService = new PatientService();
    private static final DataStore<Doctor> doctorStore = new DataStore<>();
    private static final DataStore<Appointment> appointmentStore = new DataStore<>();
    private static final BillFactory billFactory = new BillFactoryImpl();

    public static void main(String[] args) {
        seedInitialDoctors();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handlePatientMenu();
                case "2" -> handleDoctorMenu();
                case "3" -> handleAppointmentMenu();
                case "4" -> handleBillingMenu();
                case "5" -> testDeepCopyClone();
                case "0" -> {
                    System.out.println("Exiting MediTrack system. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid selection. Please enter a valid menu option.");
            }
        }
    }

    private static void seedInitialDoctors() {
        Doctor doc1 = (Doctor) Doctor.builder()
                .f_name("Arjun")
                .l_name("Mehta")
                .age(45)
                .gender(Gender.MALE)
                //.specialization(Specialization.ORTHOPEDIC)
               // .fees(750.0)
                .build();
        doctorStore.put(doc1.getP_id(), doc1);

        Doctor doc2 = (Doctor) Doctor.builder()
                .f_name("Sneha")
                .l_name("Roy")
                .age(38)
                .gender(Gender.FEMALE)
               // .specialization(Specialization.PEDIATRIC)
               // .fees(600.0)
                .build();
        doctorStore.put(doc2.getP_id(), doc2);
    }

    private static void printMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("     MEDITRACK APPOINTMENT & CLINIC UI    ");
        System.out.println("==========================================");
        System.out.println("1. Patient Operations (CRUD & Search)");
        System.out.println("2. View Doctors");
        System.out.println("3. Schedule & View Appointments");
        System.out.println("4. Billing & Payment Simulation");
        System.out.println("5. Run Deep-Copy Clone Test");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }

    private static void handlePatientMenu() {
        System.out.println("\n--- Patient Operations ---");
        System.out.println("1. Register New Patient");
        System.out.println("2. View All Patients");
        System.out.println("3. Search Patient by ID");
        System.out.println("4. Search Patient by Name");
        System.out.println("5. Search Patient by Age");
        System.out.println("6. Update Medical History");
        System.out.println("7. Delete Patient");
        System.out.print("Choice: ");
        String opt = scanner.nextLine().trim();

        try {
            switch (opt) {
                case "1" -> registerPatientInteractive();
                case "2" -> {
                    List<Patient> list = patientService.getAllPatients();
                    if (list.isEmpty()) {
                        System.out.println("No patients currently registered.");
                    } else {
                        list.forEach(System.out::println);
                    }
                }
                case "3" -> {
                    System.out.print("Enter Patient ID: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    patientService.searchPatient(id)
                            .ifPresentOrElse(System.out::println, () -> System.out.println("Patient not found."));
                }
                case "4" -> {
                    System.out.print("Enter Name fragment: ");
                    String name = scanner.nextLine().trim();
                    List<Patient> matches = patientService.searchPatient(name);
                    if (matches.isEmpty()) System.out.println("No matches found.");
                    else matches.forEach(System.out::println);
                }
                case "5" -> {
                    System.out.print("Enter Age: ");
                    int age = Integer.parseInt(scanner.nextLine().trim());
                    List<Patient> matches = patientService.searchPatientByAge(age);
                    if (matches.isEmpty()) System.out.println("No matches found.");
                    else matches.forEach(System.out::println);
                }
                case "6" -> {
                    System.out.print("Enter Patient ID: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter new medical history: ");
                    String history = scanner.nextLine().trim();
                    Patient updated = patientService.updateMedicalHistory(id, history);
                    System.out.println("Updated successfully: " + updated);
                }
                case "7" -> {
                    System.out.print("Enter Patient ID: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    boolean removed = patientService.deletePatient(id);
                    System.out.println(removed ? "Patient deleted." : "Patient ID not found.");
                }
                default -> System.out.println("Invalid option.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid numeric input.");
        } catch (InvalidDataException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    private static void registerPatientInteractive() throws InvalidDataException {
        System.out.print("First Name: ");
        String fName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lName = scanner.nextLine().trim();

        System.out.print("Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Gender Options: 1. MALE, 2. FEMALE, 3. OTHER");
        System.out.print("Select Gender: ");
        int gChoice = Integer.parseInt(scanner.nextLine().trim());
        Gender gender = switch (gChoice) {
            case 1 -> Gender.MALE;
            case 2 -> Gender.FEMALE;
            default -> Gender.OTHER;
        };

        System.out.print("Initial Medical History (or 'None'): ");
       // String history = scanner.nextLine().trim();

        Patient patient = (Patient) Patient.builder()
                .f_name(fName)
                .l_name(lName)
                .age(age)
                .gender(gender)
                //.medical_history(history)
                .build();

        Patient saved = patientService.registerPatient(patient);
        System.out.println("Patient registered successfully: " + saved);
    }

    private static void handleDoctorMenu() {
        System.out.println("\n--- Registered Doctors ---");
        List<Doctor> doctors = doctorStore.getAll();
        if (doctors.isEmpty()) {
            System.out.println("No doctors available.");
            return;
        }
        for (Doctor doc : doctors) {
            System.out.printf("Doctor ID: %d | Name: Dr. %s %s | Age: %d | Fees: ₹%.2f\n",
                    doc.getP_id(), doc.getF_name(), doc.getL_name(), doc.getAge(), doc.getFees());
        }
    }

    private static void handleAppointmentMenu() {
        System.out.println("\n--- Appointment Operations ---");
        System.out.println("1. Book Appointment");
        System.out.println("2. View All Appointments");
        System.out.print("Choice: ");
        String opt = scanner.nextLine().trim();

        if ("1".equals(opt)) {
            try {
                System.out.print("Enter Patient ID: ");
                int patId = Integer.parseInt(scanner.nextLine().trim());
                Optional<Patient> patientOpt = patientService.searchPatient(patId);
                if (patientOpt.isEmpty()) {
                    System.out.println("Patient not found. Register the patient first.");
                    return;
                }

                System.out.print("Enter Doctor ID: ");
                int docId = Integer.parseInt(scanner.nextLine().trim());
                Optional<Doctor> docOpt = doctorStore.get(docId);
                if (docOpt.isEmpty()) {
                    System.out.println("Doctor not found.");
                    return;
                }

                Appointment appointment = new Appointment(
                        new Date(),
                        AppointmentStatus.CONFIRMED,
                        AppointmentType.INITIAL,
                        250.0,
                        docOpt.get(),
                        patientOpt.get()
                );

                appointmentStore.put(appointment.getAppointment_id(), appointment);
                System.out.printf("Appointment successfully booked! Appointment ID: %d\n", appointment.getAppointment_id());

            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input.");
            }
        } else if ("2".equals(opt)) {
            List<Appointment> list = appointmentStore.getAll();
            if (list.isEmpty()) {
                System.out.println("No appointments scheduled.");
            } else {
                for (Appointment a : list) {
                    System.out.printf("Appt ID: %d | Patient: %s %s | Doctor: Dr. %s %s | Status: %s | Fees: ₹%.2f\n",
                            a.getAppointment_id(),
                            a.getPatient().getF_name(), a.getPatient().getL_name(),
                            a.getDoctor().getF_name(), a.getDoctor().getL_name(),
                            a.getStatus(),
                            a.getAppointmentFees());
                }
            }
        }
    }

    private static void handleBillingMenu() {
        System.out.println("\n--- Billing Operations ---");
        System.out.print("Enter Appointment ID to generate bill: ");
        try {
            int apptId = Integer.parseInt(scanner.nextLine().trim());
            Optional<Appointment> apptOpt = appointmentStore.get(apptId);

            if (apptOpt.isEmpty()) {
                System.out.println("Appointment ID not found.");
                return;
            }

            System.out.println("Choose Billing Strategy:");
            System.out.println("1. Standard Billing");
            System.out.println("2. Emergency Billing");
            System.out.print("Choice: ");
            String strat = scanner.nextLine().trim();

            Appointment appt = apptOpt.get();
            Bill bill = "2".equals(strat)
                    ? billFactory.createEmergencyBill(appt)
                    : billFactory.createStandardBill(appt);

            BillSummary summary = BillSummary.builder()
                    .appointment_date(bill.getAppointment_date())
                    .status(bill.getStatus())
                    .type(bill.getType())
                    .appointmentFees(bill.getAppointmentFees())
                    .doctor(bill.getDoctor())
                    .patient(bill.getPatient())
                    .doctor_fees(bill.getDoctor_fees())
                    .totalAmount(bill.calculateTotalAmount())
                    .billingStrategey(bill.getBillingStrategey())
                    .build();

            summary.processBillPayment();

        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric ID format.");
        }
    }

    private static void testDeepCopyClone() {
        System.out.println("\n--- Testing Deep-Copy Semantics for Patient ---");
        try {
            Patient original = (Patient) Patient.builder()
                    .f_name("Original")
                    .l_name("User")
                    .age(30)
                    .gender(Gender.MALE)
                    //.medical_history("Asthma")
                    .build();

            Patient copy = original.clone();

            System.out.println("Original before modification: " + original);
            System.out.println("Cloned Copy before modification: " + copy);

            copy.setMedical_history("Diabetes");
            copy.setF_name("ModifiedName");

            System.out.println("\nAfter modifying cloned object:");
            System.out.println("Original state: " + original);
            System.out.println("Cloned state:   " + copy);

            if (!original.getMedical_history().equals(copy.getMedical_history())) {
                System.out.println(" Deep Copy Verified: Modifying the clone did not alter the original object.");
            } else {
                System.out.println(" Shallow Copy Detected: Original object state was altered.");
            }
        } catch (Exception e) {
            System.out.println("Clone test failed: " + e.getMessage());
        }
    }
}