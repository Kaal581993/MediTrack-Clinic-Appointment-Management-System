package com.airtribe.meditrack.ui;

import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.PatientService;

import java.util.List;
import java.util.Scanner;

public class PatientMenu {

    private final PatientService patientService;
    private final Scanner scanner;

    public PatientMenu() {
        this(new PatientService(), new Scanner(System.in));
    }

    public PatientMenu(PatientService patientService, Scanner scanner) {
        this.patientService = patientService;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> registerPatient();
                    case "2" -> viewAllPatients();
                    case "3" -> searchPatientById();
                    case "4" -> searchPatientByName();
                    case "5" -> searchPatientByAge();
                    case "6" -> updateMedicalHistory();
                    case "7" -> deletePatient();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid numeric input.");
            } catch (InvalidDataException e) {
                System.out.println("Validation Error: " + e.getMessage());
            }
        }
        System.out.println("Leaving the patient module.");
    }

    private void printMenu() {
        System.out.println("\n===== MediTrack : Patients =====");
        System.out.println(" 1. Register New Patient");
        System.out.println(" 2. View All Patients");
        System.out.println(" 3. Search Patient by ID");
        System.out.println(" 4. Search Patient by Name");
        System.out.println(" 5. Search Patient by Age");
        System.out.println(" 6. Update Medical History");
        System.out.println(" 7. Delete Patient");
        System.out.println(" 0. Back");
        System.out.print("Choice: ");
    }

    private void registerPatient() throws InvalidDataException {
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
        String history = scanner.nextLine().trim();
        if (history.isEmpty()) history = "None";

        Patient patient = Patient.builder()
                .f_name(fName)
                .l_name(lName)
                .age(age)
                .gender(gender)
                .medical_history(history)
                .build();

        Patient saved = patientService.registerPatient(patient);
        System.out.println("Patient registered successfully: " + saved);
    }

    private void viewAllPatients() {
        List<Patient> list = patientService.getAllPatients();
        if (list.isEmpty()) {
            System.out.println("No patients currently registered.");
        } else {
            list.forEach(System.out::println);
        }
    }

    private void searchPatientById() {
        System.out.print("Enter Patient ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        patientService.searchPatient(id)
                .ifPresentOrElse(System.out::println, () -> System.out.println("Patient not found."));
    }

    private void searchPatientByName() {
        System.out.print("Enter Name fragment: ");
        String name = scanner.nextLine().trim();
        List<Patient> matches = patientService.searchPatient(name);
        if (matches.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            matches.forEach(System.out::println);
        }
    }

    private void searchPatientByAge() {
        System.out.print("Enter Age: ");
        int age = Integer.parseInt(scanner.nextLine().trim());
        List<Patient> matches = patientService.searchPatientByAge(age);
        if (matches.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            matches.forEach(System.out::println);
        }
    }

    private void updateMedicalHistory() {
        System.out.print("Enter Patient ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter new medical history: ");
        String history = scanner.nextLine().trim();
        Patient updated = patientService.updateMedicalHistory(id, history);
        System.out.println("Updated successfully: " + updated);
    }

    private void deletePatient() {
        System.out.print("Enter Patient ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        boolean removed = patientService.deletePatient(id);
        System.out.println(removed ? "Patient deleted." : "Patient ID not found.");
    }
}
