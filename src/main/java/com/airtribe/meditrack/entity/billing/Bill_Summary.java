package com.airtribe.meditrack.entity.billing;

import com.airtribe.meditrack.entity.Constants;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;

import java.util.Date;
import java.util.Scanner;

/**
 * Immutable summary of a bill.
 *
 * <p>Immutability guarantees:
 * <ul>
 *   <li>The class is {@code final} so it cannot be subclassed.</li>
 *   <li>The {@code payment} field is {@code final} and is assigned exactly
 *   once, in the constructor.</li>
 *   <li>There are no setters and no method mutates this instance's state.</li>
 *   <li>Payment processing is performed via {@link #processBillPayment()}
 *   which returns a {@link Payment} without modifying this summary,
 *   keeping the instance thread-safe.</li>
 * </ul>
 *
 * <p><b>Note on inheritance:</b> {@code Bill_Summary} extends {@link Bill}, which itself is not
 * fully immutable (e.g. its {@code totalAmount} field may be recomputed by
 * {@link Bill#generateBill()}). Therefore {@code Bill_Summary} is immutable <em>with respect to
 * its own state</em> (the {@code payment} field). Callers must not invoke inherited mutators on a
 * {@code Bill_Summary} instance if full immutability is required.
 */
public final class Bill_Summary extends Bill {

    /**
     * The payment associated with this bill summary.
     * Assigned exactly once at construction and
     * never modified afterwards, which makes the summary
     * immutable and thread-safe.
     */
    private final Payment payment;

    /**
     * Constructs an immutable bill summary without an attached payment.
     *
     * @param payment optional payment attached to this summary;
     * {@code null} if not yet paid
     */
    public Bill_Summary(
            Date appointment_date,
            AppointmentStatus status,
            AppointmentType type,
            double appointmentFees, Doctor doctor,
            Patient patient,
            Constants constants,
            double doctor_fees,
            double totalAmount
    ) {
        this(
                appointment_date,
                status,
                type,
                appointmentFees,
                doctor,
                patient,
                constants,
                doctor_fees,
                totalAmount,
                null
        );
    }

    /**
     * Constructs an immutable bill summary with an attached payment.
     *
     * @param payment optional payment attached to this summary; {@code null} if not yet paid
     */
    public Bill_Summary(
            Date appointment_date,
            AppointmentStatus status,
            AppointmentType type,
            double appointmentFees,
            Doctor doctor,
            Patient patient,
            Constants constants,
            double doctor_fees,
            double totalAmount,
            Payment payment
    ) {
        super(
                appointment_date,
                status,
                type,
                appointmentFees,
                doctor,
                patient,
                constants,
                doctor_fees,
                totalAmount
        );
        this.payment = payment;
    }

    /**
     * Initiates and processes the payment for this bill.
     * <p>
     * It first generates and displays the bill to the user, then allows the user to select a
     * payment method and creates and processes a {@link Payment}. Because this summary is
     * immutable, the payment is <strong>not</strong> stored back into {@code this}; instead the
     * processed payment is returned to the caller, who can attach it to a new summary if needed.
     *
     * @return the processed {@link Payment}, or {@code null} if an invalid payment method was
     *         selected
     */
    public Payment processBillPayment() {
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

        Payment processedPayment = null;

        if (choice > 0 && choice <= PaymentMethods.values().length) {
            PaymentMethods selectedMethod = PaymentMethods.values()[choice - 1];

            // Create a new payment object with the total amount from the bill.
            // Use a local variable instead of mutating this.payment to preserve immutability.
            Payment newPayment = new Payment(this.getTotalAmount(), selectedMethod);

            // Process the payment
            newPayment.processPayment();

            if (newPayment.getStatus() == PaymentStatus.COMPLETED) {
                System.out.println("Bill successfully paid and closed.");
            } else {
                System.out.println("Further action required for this bill.");
            }

            processedPayment = newPayment;
        } else {
            System.out.println("Invalid payment method selected. Please try again.");
        }

        return processedPayment;
    }

    /**
     * Returns the payment attached to this summary, or {@code null} if it has not been paid.
     *
     * @return the payment, possibly {@code null}
     */
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
        private Payment payment;

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

        /**
         * Optional step to attach a payment to the summary at build time.
         */
        public BillSummaryBuilder payment(Payment payment) {
            this.payment = payment;
            return this;
        }

        public Bill_Summary build() {
            return new Bill_Summary(appointment_date, status, type, appointmentFees, doctor, patient, constants, doctor_fees, totalAmount, payment);
        }
    }
}
