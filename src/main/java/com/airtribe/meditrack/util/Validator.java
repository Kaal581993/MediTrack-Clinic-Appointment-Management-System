package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.entity.billing.Payment;
import com.airtribe.meditrack.entity.persons.Patient;

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



}
