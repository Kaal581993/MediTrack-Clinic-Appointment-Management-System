package com.airtribe.meditrack.entity.billing;

import com.airtribe.meditrack.entity.Constants;
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

    // Builder pattern (does not alter existing constructors/logic)
    public static BillSummaryBuilder builder() {
        return new BillSummaryBuilder();
    }

    public static class BillSummaryBuilder {
        private Date appointment_date;
        private AppointmentStatus status;
        private AppointmentType type;
        private double appointmentFees;
        private Doctor doctor;
        private Patient patient;
        private Constants constants;
        private double doctor_fees;
        private double totalAmount;

        public BillSummaryBuilder appointment_date(Date appointment_date) {
            this.appointment_date = appointment_date;
            return this;
        }

        public BillSummaryBuilder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public BillSummaryBuilder type(AppointmentType type) {
            this.type = type;
            return this;
        }

        public BillSummaryBuilder appointmentFees(double appointmentFees) {
            this.appointmentFees = appointmentFees;
            return this;
        }

        public BillSummaryBuilder doctor(Doctor doctor) {
            this.doctor = doctor;
            return this;
        }

        public BillSummaryBuilder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public BillSummaryBuilder constants(Constants constants) {
            this.constants = constants;
            return this;
        }

        public BillSummaryBuilder doctor_fees(double doctor_fees) {
            this.doctor_fees = doctor_fees;
            return this;
        }

        public BillSummaryBuilder totalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Bill_Summary build() {
            return new Bill_Summary(appointment_date, status, type, appointmentFees, doctor, patient, constants, doctor_fees, totalAmount);
        }
    }
}
