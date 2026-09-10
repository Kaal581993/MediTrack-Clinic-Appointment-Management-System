package com.airtribe.meditrack.interfaces;

public interface Payable {

    default double processPayment() {
        return 0.0;
    }

    default String getPaymentStatus() {
        return "UNKNOWN";
    }

    default boolean validatePayment() {
        return true;
    }
}
