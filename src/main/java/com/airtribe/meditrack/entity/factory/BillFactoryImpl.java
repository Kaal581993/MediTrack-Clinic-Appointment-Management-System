package com.airtribe.meditrack.entity.factory;

import com.airtribe.meditrack.entity.Constants;
import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;
import com.airtribe.meditrack.strategey.billiing.impl.EmergencyBillingStrategy;
import com.airtribe.meditrack.strategey.billiing.impl.StandardBillingStrategy;

public class BillFactoryImpl implements BillFactory {

    @Override
    public Bill createBill(Appointment appointment, BillingStrategey billingStrategey) {
        return new Bill.BillBuilder()
                .appointment_date(appointment.getAppointment_date())
                .status(appointment.getStatus())
                .type(appointment.getType())
                .appointmentFees(appointment.getAppointmentFees())
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .constants(new Constants())
                .doctor_fees(appointment.getDoctor().getFees())
                .totalAmount(0.0)
                .billingStrategey(billingStrategey)
                .build();
    }

    @Override
    public Bill createStandardBill(Appointment appointment) {
        return createBill(appointment, new StandardBillingStrategy());
    }

    @Override
    public Bill createEmergencyBill(Appointment appointment) {
        return createBill(appointment, new EmergencyBillingStrategy());
    }
}
