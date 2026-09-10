package com.airtribe.meditrack.interfaces;

public interface Payable {

    public double processPayment();
    public double getPayemtStatus();
    public boolean validatePayment();
}
