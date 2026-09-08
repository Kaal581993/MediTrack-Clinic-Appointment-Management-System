package com.airtribe.meditrack.strategey.billiing.impl;

import com.airtribe.meditrack.entity.Constants;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;

public class EmergencyBillingStrategy implements BillingStrategey {

    @Override
    public double calculateSubTotal(Bill bill){
        return bill.getDoctor_fees() + bill.getAppointmentFees(); // Using getter for appointmentFees from parent
    }

    @Override
    public double calculateTaxAmount(Bill bill) {
        return calculateSubTotal(bill) * Constants.getTAX_RATE();
    }

    @Override
    public double calculateTotal(Bill bill) {
        return calculateSubTotal(bill) + calculateTaxAmount(bill);
    }

//    @Override
//    public double applyDiscount(Bill bill, double discount) {
//        return 0;
//    }
//
//    @Override
//    public double applyTax(Bill bill, double tax) {
//        return 0;
//    }
}
