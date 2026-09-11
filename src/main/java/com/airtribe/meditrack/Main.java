package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;

import com.airtribe.meditrack.ui.AppointmentMenu;
import com.airtribe.meditrack.ui.BillingMenu;
import com.airtribe.meditrack.ui.DoctorMenu;
import com.airtribe.meditrack.ui.PatientMenu;
import com.airtribe.meditrack.util.DataStore;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DataStore<Doctor> doctorStore = new DataStore<>();

     static void main() {
        seedInitialDoctors();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    PatientMenu patientMenu = new PatientMenu();
                    patientMenu.run();
                }
                case "2" -> {
                    DoctorMenu doctorMenu = new DoctorMenu();
                    doctorMenu.run();
                }
                case "3" -> {
                    AppointmentMenu appointmentMenu = new AppointmentMenu();
                    appointmentMenu.run();
                }
                case "4" -> {
                    BillingMenu billingMenu = new BillingMenu();
                    billingMenu.run();
                }
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
                .build();
        doctorStore.add(String.valueOf(doc1.getDoc_id()), doc1);

        Doctor doc2 = (Doctor) Doctor.builder()
                .f_name("Sneha")
                .l_name("Roy")
                .age(38)
                .gender(Gender.FEMALE)
                .build();
        doctorStore.add(String.valueOf(doc2.getDoc_id()), doc2);
    }

    private static void printMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("        MEDITRACK CLINIC SYSTEM          ");
        System.out.println("==========================================");
        System.out.println("1. Patient Operations");
        System.out.println("2. Doctor Operations");
        System.out.println("3. Appointment Operations");
        System.out.println("4. Billing & Payment");
        System.out.println("0. Exit");
        System.out.print("Select an option: ");
    }
}