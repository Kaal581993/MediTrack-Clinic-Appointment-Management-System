package com.airtribe.meditrack.entity.factory;

import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;

public class BillFactoryImpl implements BillFactory{


    @Override
    public Bill createBill(Appointment appointment, BillingStrategey billingStrategey) {
        return null;
    }

    @Override
    public Bill createStandardBill(Appointment appointment) {
        return null;
    }

    @Override
    public Bill createEmergencyBill(Appointment appointment) {
        return null;
    }
}
