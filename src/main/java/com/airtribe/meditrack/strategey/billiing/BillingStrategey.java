package com.airtribe.meditrack.strategey.billiing;

import com.airtribe.meditrack.entity.billing.Bill;

public interface BillingStrategey {

    public double calculateSubTotal(Bill bill);
    public double calculateTaxAmount(Bill bill);
    public double calculateTotal(Bill bill);
//    public double applyDiscount(Bill bill, double discount);
//    public double applyTax(Bill bill, double tax);
}
