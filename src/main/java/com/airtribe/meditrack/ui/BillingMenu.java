package com.airtribe.meditrack.ui;

import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.entity.billing.BillSummary;
import com.airtribe.meditrack.entity.billing.PaymentMethods;
import com.airtribe.meditrack.entity.factory.BillFactory;
import com.airtribe.meditrack.entity.factory.BillFactoryImpl;
import com.airtribe.meditrack.exception.BillNotFoundException;
import com.airtribe.meditrack.service.BillingService;
import com.airtribe.meditrack.util.DataStore;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BillingMenu {

    private final BillingService billingService;
    private final DataStore<Appointment> appointmentStore;
    private final BillFactory billFactory;
    private final Scanner scanner;

    public BillingMenu() {
        this(new BillingService(), new DataStore<>(), new BillFactoryImpl(), new Scanner(System.in));
    }

    public BillingMenu(BillingService billingService, DataStore<Appointment> appointmentStore, 
                       BillFactory billFactory, Scanner scanner) {
        this.billingService = billingService;
        this.appointmentStore = appointmentStore;
        this.billFactory = billFactory;
        this.scanner = scanner;
    }

    public void setAppointmentStore(DataStore<Appointment> appointmentStore) {
        this.appointmentStore.clear();
        appointmentStore.getAll().forEach(a -> this.appointmentStore.put(a.getAppointment_id(), a));
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> generateBill();
                    case "2" -> processPayment();
                    case "3" -> viewBillById();
                    case "4" -> viewAllBills();
                    case "5" -> viewBillsByPatient();
                    case "6" -> viewBillsByDoctor();
                    case "7" -> cancelBill();
                    case "8" -> generateReport();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid numeric input.");
            } catch (BillNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Leaving the billing module.");
    }

    private void printMenu() {
        System.out.println("\n===== MediTrack : Billing =====");
        System.out.println(" 1. Generate Bill");
        System.out.println(" 2. Process Payment");
        System.out.println(" 3. View Bill by ID");
        System.out.println(" 4. View All Bills");
        System.out.println(" 5. View Bills by Patient");
        System.out.println(" 6. View Bills by Doctor");
        System.out.println(" 7. Cancel Bill");
        System.out.println(" 8. Generate Billing Report");
        System.out.println(" 0. Back");
        System.out.print("Choice: ");
    }

    private void generateBill() {
        System.out.print("Enter Appointment ID to generate bill: ");
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
                ? billingService.createEmergencyBill(appt)
                : billingService.createStandardBill(appt);

        System.out.println("Bill generated successfully!");
        bill.generateBill();
    }

    private void processPayment() {
        System.out.print("Enter Bill ID: ");
        int billId = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Payment Methods:");
        for (int i = 0; i < PaymentMethods.values().length; i++) {
            System.out.println("  " + (i + 1) + ". " + PaymentMethods.values()[i]);
        }
        System.out.print("Select Payment Method: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());

        if (choice < 1 || choice > PaymentMethods.values().length) {
            System.out.println("Invalid payment method.");
            return;
        }

        PaymentMethods method = PaymentMethods.values()[choice - 1];
        var payment = billingService.processPayment(billId, method);
        System.out.println("Payment processed: " + payment);
    }

    private void viewBillById() {
        System.out.print("Enter Bill ID: ");
        int billId = Integer.parseInt(scanner.nextLine().trim());
        
        Optional<Bill> bill = billingService.getBillById(billId);
        if (bill.isPresent()) {
            bill.get().generateBill();
        } else {
            System.out.println("Bill not found.");
        }
    }

    private void viewAllBills() {
        List<Bill> bills = billingService.getAllBills();
        if (bills.isEmpty()) {
            System.out.println("No bills available.");
        } else {
            bills.forEach(System.out::println);
        }
    }

    private void viewBillsByPatient() {
        System.out.print("Enter Patient ID: ");
        int patientId = Integer.parseInt(scanner.nextLine().trim());
        
        List<Bill> bills = billingService.getBillsByPatient(patientId);
        if (bills.isEmpty()) {
            System.out.println("No bills found for this patient.");
        } else {
            bills.forEach(System.out::println);
        }
    }

    private void viewBillsByDoctor() {
        System.out.print("Enter Doctor ID: ");
        int doctorId = Integer.parseInt(scanner.nextLine().trim());
        
        List<Bill> bills = billingService.getBillsByDoctor(doctorId);
        if (bills.isEmpty()) {
            System.out.println("No bills found for this doctor.");
        } else {
            bills.forEach(System.out::println);
        }
    }

    private void cancelBill() {
        System.out.print("Enter Bill ID to cancel: ");
        int billId = Integer.parseInt(scanner.nextLine().trim());
        
        boolean cancelled = billingService.cancelBill(billId);
        if (cancelled) {
            System.out.println("Bill cancelled successfully.");
        } else {
            System.out.println("Bill not found or cannot be cancelled.");
        }
    }

    private void generateReport() {
        billingService.generateBillReport();
    }
}
