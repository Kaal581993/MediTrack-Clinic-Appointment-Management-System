package com.airtribe.meditrack.entity.factory;

import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;
import com.airtribe.meditrack.entity.billing.Bill;

public interface BillFactory {

    public Bill createBill(Appointment appointment, BillingStrategey billingStrategey);
    public Bill createStandardBill(Appointment appointment);
    public Bill createEmergencyBill(Appointment appointment);
}