package com.airtribe.meditrack.entity.billing;

import com.airtribe.meditrack.entity.id_generators.IdGenerators;
import java.util.Scanner;

public class Payment {
    private final int paymentId;
    private int transactionId;
    private final double amount;
    private PaymentStatus status;
    private final PaymentMethods paymentMethod;
    IdGenerators idGenerators = IdGenerators.getInstance();

    public Payment(double amount, PaymentMethods paymentMethod) {
        this.paymentId = idGenerators.generatePaymentID();
        this.transactionId = idGenerators.generateTransactionID();
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
    }

    /**
     * Processes the payment by simulating the selected payment method.
     * For manual payments like cash, it prompts for confirmation.
     * For others, it simulates a validation and processing step.
     */
    public void processPayment() {
        System.out.println("Initializing payment for amount: " + this.amount + " using " + this.paymentMethod);
        boolean success = false;
        switch (this.paymentMethod) {
            case CASH:
                success = processCashPayment();
                break;
            case UPI:
            case CREDIT_CARD:
            case DEBIT_CARD:
            case ONLINE_BANKING:
                success = processDigitalPayment();
                break;
        }

        if (success) {
            this.status = PaymentStatus.COMPLETED;
            System.out.println("Payment successful. Receipt generated for payment ID: " + paymentId);
        } else {
            this.status = PaymentStatus.FAILED;
            System.out.println("Payment failed for payment ID: " + paymentId);
        }
    }

    private boolean processCashPayment() {
        System.out.print("Confirm cash received from patient (yes/no): ");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("yes");
    }

    private boolean processDigitalPayment() {
        // Simulate a digital payment validation (e.g., checking card details, UPI ID)
        System.out.println("Validating payment details for " + this.paymentMethod + "...");
        // In a real system, this would involve an external API call. Here we just simulate success.
        System.out.println("Validation successful.");
        System.out.println("Processing transaction...");
        this.transactionId = idGenerators.generateTransactionID(); // Generate a new transaction ID on success
        return true;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    // Builder pattern (does not alter existing constructors/logic)
    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public static class PaymentBuilder {
        private double amount;
        private PaymentMethods paymentMethod;

        public PaymentBuilder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public PaymentBuilder paymentMethod(PaymentMethods paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Payment build() {
            return new Payment(amount, paymentMethod);
        }
    }
}
