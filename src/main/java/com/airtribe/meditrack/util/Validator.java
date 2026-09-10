package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.billing.Bill;
import com.airtribe.meditrack.entity.billing.Payment;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Specialization;

import java.util.Date;

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

    // ---------------------------------------------------------------------
    // Appointment module additions
    // ---------------------------------------------------------------------

    /**
     * Centralised validation for the inputs of a new appointment.
     * <p>
     * Throws rather than returning a boolean so the caller cannot ignore a failure,
     * and so the message explains exactly which field was wrong.
     *
     * @param date    requested slot; must be non-null and in the future
     * @param type    appointment type; must be non-null
     * @param fees    consultation fee; must not be negative
     * @param doctor  the doctor; must be non-null
     * @param patient the patient; must be non-null
     * @throws IllegalArgumentException when any field is missing or out of range
     */
    public static void validateAppointment(Date date,
                                           AppointmentType type,
                                           double fees,
                                           Doctor doctor,
                                           Patient patient) {
        if (doctor == null) {
            throw new IllegalArgumentException("Appointment must have a doctor.");
        }
        if (patient == null) {
            throw new IllegalArgumentException("Appointment must have a patient.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Appointment must have a type (INITIAL or FOLLOWUP).");
        }
        if (date == null) {
            throw new IllegalArgumentException("Appointment must have a date.");
        }
        if (!DateUtil.isFuture(date)) {
            throw new IllegalArgumentException(
                    "Appointment date must be in the future, but was: " + DateUtil.format(date));
        }
        if (fees < 0) {
            throw new IllegalArgumentException("Appointment fees must not be negative, but was: " + fees);
        }
    }

    /**
     * @param name the name to check
     * @return {@code true} when the name is non-null and not blank
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * @param id the id to check
     * @return {@code true} when the id is positive (generated ids start at 1)
     */
    public static boolean isValidId(int id) {
        return id > 0;
    }
}
