package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;

import java.util.Date;
import java.util.Scanner;

public final class Bill_Summary extends Bill {
    private Payment payment;

    public Bill_Summary(Date appointment_date, AppointmentStatus status, AppointmentType type, double appointmentFees, Doctor doctor, Patient patient, Constants constants, double doctor_fees, double totalAmount) {
        super(appointment_date, status, type, appointmentFees, doctor, patient, constants, doctor_fees, totalAmount);
    }

    /**
     * Initiates and processes the payment for this bill.
     * It allows the user to select a payment method and then creates and processes a Payment object.
     */
    public void processBillPayment() {
        // First, generate and display the bill to the user
        generateBill();

        // Then, proceed with payment processing
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Process Payment ---");
        System.out.println("Select a payment method:");
        for (int i = 0; i < PaymentMethods.values().length; i++) {
            System.out.println((i + 1) + ". " + PaymentMethods.values()[i]);
        }
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice > 0 && choice <= PaymentMethods.values().length) {
            PaymentMethods selectedMethod = PaymentMethods.values()[choice - 1];
            
            // Create a new payment object with the total amount from the bill
            this.payment = new Payment(this.getTotalAmount(), selectedMethod);
            
            // Process the payment
            this.payment.processPayment();
            
            if (this.payment.getStatus() == PaymentStatus.COMPLETED) {
                System.out.println("Bill successfully paid and closed.");
            } else {
                System.out.println("Further action required for this bill.");
            }
        } else {
            System.out.println("Invalid payment method selected. Please try again.");
        }
    }

    public Payment getPayment() {
        return payment;
    }
}
