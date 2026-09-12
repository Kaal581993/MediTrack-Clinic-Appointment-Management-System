package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.entity.billing.Payment;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Specialization;

public class Validator {
    public static boolean validatePatient(Patient patient) {
        if (patient == null) {
            return false;
        }
        if (patient.getF_name() == null || patient.getF_name().trim().isEmpty()) {
            return false;
        }
        if (patient.getL_name() == null || patient.getL_name().trim().isEmpty()) {
            return false;
        }
        if (patient.getAge() <= 0 || patient.getAge() > 130) {
            return false;
        }
        return patient.getGender() != null;
    }
    public boolean validateBill(Bill bill){
        if(bill.getBill_id() == 0){
            return false;
        }
        if(bill.calculateSubTotal() <0){
            return false;
        }

        if(bill.calculateTotalAmount()<0){
            return false;
        }

        if(bill.getDoctor()==null){
            return false;
        }

        if(bill.getPatient()==null){
            return false;
        }

        if(bill.getMedicineList().isEmpty()){
            return false;
        }

        return true;
    }

    public boolean validatePayment(Payment payment){
        if(payment.getPayment_id() == 0){
            return false;
        }
        return true;
    }

    public boolean validateDoctor(Doctor doctor) {
        if (doctor == null) {
            return false;
        }
        if (doctor.getF_name() == null || doctor.getF_name().isEmpty()) {
            return false;
        }
        if (doctor.getL_name() == null || doctor.getL_name().isEmpty()) {
            return false;
        }
        if (doctor.getAge() <= 0 || doctor.getAge() > 120) {
            return false;
        }
        if (doctor.getSpecialization() == null) {
            return false;
        }
        if (doctor.getFees() < 0) {
            return false;
        }
        return true;
    }
}
