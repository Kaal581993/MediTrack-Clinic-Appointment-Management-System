package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.id_generators.IdGenerators;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;

import java.util.Date;

// May need to re-visit once with Bill Summar

public class Bill extends Appointment {
    private int bill_id;
    Constants constants = new Constants();
    private double doctor_fees;

   final private double tax = constants.getTAX_RATE();
    private double totalAmount;
    // double appointmentFees;


    IdGenerators id_gen = new IdGenerators();

    public Bill(Date appointment_date,
                AppointmentStatus status,
                AppointmentType type,
                double appointmentFees,
                Doctor doctor,
                Patient patient,
                Constants constants,
                double doctor_fees,
                double totalAmount) { // Corrected typo
        super(appointment_date, status, type, appointmentFees, doctor, patient);
        this.bill_id = id_gen.BillIdGenerator();
        this.constants = constants;
        this.doctor_fees = doctor_fees;
        this.totalAmount = totalAmount; // Corrected typo

    }

    public int getBill_id() {
        return bill_id;
    }

    public Constants getConstants() {
        return constants;
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

    // Renamed from calculateTotal for clarity
    public double calculateSubTotal(){
        return doctor_fees + getAppointmentFees(); // Using getter for appointmentFees from parent
    }

    // This method now calculates and returns the tax amount without side effects.
    public double calculateTaxAmount(){
        return calculateSubTotal() * tax;
    }

    // A new method to calculate the final total amount.
    public double calculateTotalAmount() {
        return calculateSubTotal() + calculateTaxAmount();
    }

    /**
     * Generates and prints a formatted bill to the console.
     * It calculates the subtotal, tax, and final total amount and displays them
     * along with other relevant appointment details.
     */
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


}
