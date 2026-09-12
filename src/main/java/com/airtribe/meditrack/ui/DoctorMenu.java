package com.airtribe.meditrack.ui;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Specialization;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.service.DoctorService;

import java.util.List;
import java.util.Scanner;

public class DoctorMenu {

    private final DoctorService doctorService;
    private final Scanner scanner;

    public DoctorMenu() {
        this(new DoctorService(), new Scanner(System.in));
    }

    public DoctorMenu(DoctorService doctorService, Scanner scanner) {
        this.doctorService = doctorService;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> addDoctor();
                    case "2" -> viewAllDoctors();
                    case "3" -> searchDoctorById();
                    case "4" -> searchDoctorByName();
                    case "5" -> searchDoctorBySpecialization();
                    case "6" -> updateDoctorFees();
                    case "7" -> deleteDoctor();
                    case "8" -> viewDoctorsByFee();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid numeric input.");
            } catch (DoctorNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Leaving the doctor module.");
    }

    private void printMenu() {
        System.out.println("\n===== MediTrack : Doctors =====");
        System.out.println(" 1. Add New Doctor");
        System.out.println(" 2. View All Doctors");
        System.out.println(" 3. Search Doctor by ID");
        System.out.println(" 4. Search Doctor by Name");
        System.out.println(" 5. Search Doctor by Specialization");
        System.out.println(" 6. Update Doctor Fees");
        System.out.println(" 7. Delete Doctor");
        System.out.println(" 8. View Doctors by Fee Range");
        System.out.println(" 0. Back");
        System.out.print("Choice: ");
    }

    private void addDoctor() {
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

        System.out.println("Specialization Options:");
        for (int i = 0; i < Specialization.values().length; i++) {
            System.out.println("  " + (i + 1) + ". " + Specialization.values()[i]);
        }
        System.out.print("Select Specialization: ");
        int sChoice = Integer.parseInt(scanner.nextLine().trim());
        Specialization specialization = Specialization.values()[sChoice - 1];

        System.out.print("Consultation Fees: ");
        double fees = Double.parseDouble(scanner.nextLine().trim());

        Doctor doctor = Doctor.builder()
                .f_name(fName)
                .l_name(lName)
                .age(age)
                .gender(gender)
                .specialization(specialization)
                .fees(fees)
                .build();

        doctorService.addDoctor(doctor);
        System.out.println("Doctor added successfully: " + doctor);
    }

    private void viewAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors available.");
        } else {
            for (Doctor doc : doctors) {
                System.out.printf("ID: %d | Dr. %s %s | %s | Age: %d | Fees: ₹%.2f\n",
                        doc.getDoc_id(), doc.getF_name(), doc.getL_name(),
                        doc.getSpecialization(), doc.getAge(), doc.getFees());
            }
        }
    }

    private void searchDoctorById() {
        System.out.print("Enter Doctor ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Doctor doctor = doctorService.searchDoctor(id);
        if (doctor != null) {
            System.out.println(doctor);
        } else {
            System.out.println("Doctor not found.");
        }
    }

    private void searchDoctorByName() {
        System.out.print("Enter Name fragment: ");
        String name = scanner.nextLine().trim();
        Doctor doctor = doctorService.searchDoctor(name);
        if (doctor != null) {
            System.out.println(doctor);
        } else {
            System.out.println("Doctor not found.");
        }
    }

    private void searchDoctorBySpecialization() {
        System.out.println("Specialization Options:");
        for (int i = 0; i < Specialization.values().length; i++) {
            System.out.println("  " + (i + 1) + ". " + Specialization.values()[i]);
        }
        System.out.print("Select Specialization: ");
        int sChoice = Integer.parseInt(scanner.nextLine().trim());
        Specialization specialization = Specialization.values()[sChoice - 1];

        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        if (doctors.isEmpty()) {
            System.out.println("No doctors found with this specialization.");
        } else {
            doctors.forEach(System.out::println);
        }
    }

    private void updateDoctorFees() {
        System.out.print("Enter Doctor ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter new fees: ");
        double newFees = Double.parseDouble(scanner.nextLine().trim());
        
        boolean updated = doctorService.updateDoctorFees(id, newFees);
        if (updated) {
            System.out.println("Fees updated successfully.");
        } else {
            System.out.println("Doctor not found.");
        }
    }

    private void deleteDoctor() {
        System.out.print("Enter Doctor ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        
        boolean deleted = doctorService.deleteDoctor(id);
        if (deleted) {
            System.out.println("Doctor deleted successfully.");
        } else {
            System.out.println("Doctor not found.");
        }
    }

    private void viewDoctorsByFee() {
        System.out.print("Maximum Fee: ");
        double maxFee = Double.parseDouble(scanner.nextLine().trim());
        
        List<Doctor> doctors = doctorService.getDoctorsByMaxFee(maxFee);
        if (doctors.isEmpty()) {
            System.out.println("No doctors found within this fee range.");
        } else {
            doctors.forEach(System.out::println);
        }
    }
}
