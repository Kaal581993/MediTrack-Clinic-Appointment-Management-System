package com.airtribe.meditrack.entity.billing;



import com.airtribe.meditrack.entity.Constants;
import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.idgenerators.IdGenerators;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.strategey.billiing.BillingStrategey;

import java.util.Date;

// May need to re-visit once with Bill Summari

public class Bill extends Appointment {
    private int bill_id;

    private double doctor_fees;

    final private double tax = Constants.getTAX_RATE();
    private double totalAmount;
    private BillingStrategey billingStrategey;
    // double appointmentFees;


    IdGenerators id_gen = IdGenerators.getInstance();



    Bill(
        Date appointment_date,
        AppointmentStatus status,
        AppointmentType type,
        double appointmentFees,
        Doctor doctor,
        Patient patient,
//        Constants constants,
        double doctor_fees,
        double totalAmount,
        BillingStrategey billingStrategey
    ) {
        super(appointment_date, status, type, appointmentFees, doctor, patient);
        if(billingStrategey == null){
            throw new InvalidDataException("Billing strategy is required");
        }
        this.bill_id = id_gen.BillIdGenerator();
//        this.constants = constants;
        this.doctor_fees = doctor_fees;
        this.totalAmount = totalAmount;
        this.billingStrategey = billingStrategey;
    }


    public Bill(Bill other) {
        super(other); // Call parent copy constructor first
        // Copy primitive fields
        this.doctor_fees = other.doctor_fees;
        this.totalAmount = other.totalAmount;
        // Generate new bill_id (don't copy - should be unique)
        this.bill_id = id_gen.BillIdGenerator();
        // Share BillingStrategy reference (stateless strategy)
        this.billingStrategey = other.billingStrategey;
    }


    private Bill(
        BillBuilder billBuilder
    ) {
        super(billBuilder.appointment_date, billBuilder.status, billBuilder.type, billBuilder.appointmentFees, billBuilder.doctor, billBuilder.patient);
        if(billBuilder.billingStrategy == null){
            throw new InvalidDataException("Billing strategy is required");
        }
        this.bill_id = id_gen.BillIdGenerator();
//        this.constants = billBuilder.constants;
        this.doctor_fees = billBuilder.doctor_fees;
        this.totalAmount = billBuilder.totalAmount;
        this.billingStrategey = billBuilder.billingStrategy;
    }

    public int getBill_id() {
        return bill_id;
    }


    public double getDoctor_fees() {
        return doctor_fees;
    }

    // Removed getAmount() as 'amount' is not defined and its purpose is unclear.

    public double getTax() {
        return tax;
    }

    public double getTotalAmount() { // Corrected typo
        return totalAmount;
    }

    public double calculateSubTotal(){
        return billingStrategey.calculateSubTotal(this);
    }

    public double calculateTaxAmount(){
        return billingStrategey.calculateTaxAmount(this);
    }

    public double calculateTotalAmount() {
        return billingStrategey.calculateTotal(this);
    }


    public CharSequence getMedicineList() {
        return null;
    }

    public BillingStrategey getBillingStrategey() {
        return billingStrategey;
    }

    public void generateBill(){
        this.totalAmount = calculateTotalAmount();
        System.out.println("*****************************************");
        System.out.println("              INVOICE                    ");
        System.out.println("*****************************************");
        System.out.println("Bill ID: " + getBill_id());
        System.out.println("Patient Name: " + getPatient().getF_name()+""+getPatient().getL_name());
        System.out.println("Attending Doctor: " + getDoctor().getF_name()+" "+getDoctor().getL_name());
        System.out.println("Date: " + new Date());
        System.out.println("-----------------------------------------");
        System.out.println("Description\t\t\tAmount");
        System.out.println("-----------------------------------------");
        System.out.printf("Doctor Fees:\t\t\t%.2f\n", getDoctor_fees());
        System.out.printf("Appointment Fees:\t\t%.2f\n", getAppointmentFees());
        System.out.println("-----------------------------------------");
        System.out.printf("Subtotal:\t\t\t%.2f\n", calculateSubTotal());
        System.out.printf("Tax (%.1f%%):\t\t\t%.2f\n", tax * 100, calculateTaxAmount());
        System.out.println("-----------------------------------------");
        System.out.printf("Total Amount Payable:\t\t%.2f\n", this.totalAmount);
        System.out.println("*****************************************");
    }


    // Builder pattern (does not alter existing constructors/logic)


    public static class BillBuilder {
        private BillingStrategey billingStrategy;
        private Date appointment_date;
        private AppointmentStatus status;
        private AppointmentType type;
        private double appointmentFees;
        private Doctor doctor;
        private Patient patient;
        private Constants constants;
        private double doctor_fees;
        private double totalAmount;


        public BillBuilder appointment_date(Date appointment_date) {
            this.appointment_date = appointment_date;
            return this;
        }

        public BillBuilder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public BillBuilder type(AppointmentType type) {
            this.type = type;
            return this;
        }

        public BillBuilder appointmentFees(double appointmentFees) {
            this.appointmentFees = appointmentFees;
            return this;
        }

        public BillBuilder doctor(Doctor doctor) {
            this.doctor = doctor;
            return this;
        }

        public BillBuilder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public BillBuilder constants(Constants constants) {
            this.constants = constants;
            return this;
        }

        public BillBuilder doctor_fees(double doctor_fees) {
            this.doctor_fees = doctor_fees;
            return this;
        }

        public BillBuilder totalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public  BillBuilder billingStrategey(BillingStrategey billingStrategey) {
            this.billingStrategy = billingStrategey;
            return this;
        }

        public Bill build() {
            if(billingStrategy == null){
                throw new InvalidDataException("Billing strategy is required");
            }
            return new Bill(this);
        }
    }
}
