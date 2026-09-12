package com.airtribe.meditrack.strategey.billiing.impl;

import com.airtribe.meditrack.entity.Constants;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;

public class StandardBillingStrategy implements BillingStrategey {

    @Override
    public double calculateSubTotal(Bill bill) {
        return bill.getDoctor_fees() + bill.getAppointmentFees();
    }

    @Override
    public double calculateTaxAmount(Bill bill) {
        return calculateSubTotal(bill) * Constants.getTAX_RATE();
    }

    @Override
    public double calculateTotal(Bill bill) {
        return calculateSubTotal(bill) + calculateTaxAmount(bill);
    }
}
