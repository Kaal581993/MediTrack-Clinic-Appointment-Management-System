package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.entity.billing.Payment;

public class Validator {

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
