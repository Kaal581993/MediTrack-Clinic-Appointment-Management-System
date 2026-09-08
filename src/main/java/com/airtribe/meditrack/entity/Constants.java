package com.airtribe.meditrack.entity;

public class Constants {
    final private double TAX_RATE=0.8;
    final private String BILL_FILE_PATH="/bill/bill.csv";
    final private String PATIENT_MEDICAL_FILE_PATH="/medical_file/medical_file.csv";
    final private double INSURANCE_COVERAGE=0.8;
    final private double FIRST_APPOINTMENT_FEE=500.0;
    final private double FOLLOW_UP_APPOINTMENT_FEE=100.0;
    final private double MEDICINE_COST=100.0;
    final private double MEDICINE_DISCOUNT=0.12;
    final private double MEDICINE_TAX=0.1;
    final private double MEDICINE_INSURANCE_COVERAGE=0.8;


    public double getTAX_RATE() {
        return TAX_RATE;
    }

    public String getBILL_FILE_PATH() {
        return BILL_FILE_PATH;
    }

    public String getPATIENT_MEDICAL_FILE_PATH() {
        return PATIENT_MEDICAL_FILE_PATH;
    }

    public double getINSURANCE_COVERAGE(){
        return INSURANCE_COVERAGE;
    }

    public double getFIRST_APPOINTMENT_FEE() {
        return FIRST_APPOINTMENT_FEE;
    }

    public double getFOLLOW_UP_APPOINTMENT_FEE() {
        return FOLLOW_UP_APPOINTMENT_FEE;
    }

    public double getMEDICINE_COST() {
        return MEDICINE_COST;
    }

    public double getMEDICINE_DISCOUNT() {
        return MEDICINE_DISCOUNT;
    }

    public double getMEDICINE_TAX() {
        return MEDICINE_TAX;
    }

    public double getMEDICINE_INSURANCE_COVERAGE() {
        return MEDICINE_INSURANCE_COVERAGE;
    }
}
