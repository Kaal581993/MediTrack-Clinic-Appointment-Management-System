package com.airtribe.meditrack.entity;

public class Constants {
    static final private double TAX_RATE=0.8;
    static final private String BILL_FILE_PATH="/bill/bill.csv";
    static final private String PATIENT_MEDICAL_FILE_PATH="/medical_file/medical_file.csv";
    static final private double INSURANCE_COVERAGE=0.8;
    static final private double FIRST_APPOINTMENT_FEE=500.0;
    static final private double FOLLOW_UP_APPOINTMENT_FEE=100.0;
    static final private double MEDICINE_COST=100.0;
    static final private double MEDICINE_DISCOUNT=0.12;
    static final private double MEDICINE_TAX=0.1;
    static final private double MEDICINE_INSURANCE_COVERAGE=0.8;

    private static Constants instance = new Constants();


    public static double getTAX_RATE() {
        return TAX_RATE;
    }

    public  static String getBILL_FILE_PATH() {
        return BILL_FILE_PATH;
    }

    public static String getPATIENT_MEDICAL_FILE_PATH() {
        return PATIENT_MEDICAL_FILE_PATH;
    }

    public static double getINSURANCE_COVERAGE(){
        return INSURANCE_COVERAGE;
    }

    public static double getFIRST_APPOINTMENT_FEE() {
        return FIRST_APPOINTMENT_FEE;
    }

    public static double getFOLLOW_UP_APPOINTMENT_FEE() {
        return FOLLOW_UP_APPOINTMENT_FEE;
    }

    public static double getMEDICINE_COST() {
        return MEDICINE_COST;
    }

    public static double getMEDICINE_DISCOUNT() {
        return MEDICINE_DISCOUNT;
    }

    public static double getMEDICINE_TAX() {
        return MEDICINE_TAX;
    }

    public static double getMEDICINE_INSURANCE_COVERAGE() {
        return MEDICINE_INSURANCE_COVERAGE;
    }

    public static Constants getInstance() {
        return instance;
    }
}
