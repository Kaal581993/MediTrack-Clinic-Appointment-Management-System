package com.airtribe.meditrack.entity.factory;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Specialization;
import com.airtribe.meditrack.util.Validator;

public class DoctorFactoryImpl implements DoctorFactory {

    private final Validator validator = new Validator();

    @Override
    public Doctor createDoctor(String firstName, String lastName, int age, Gender gender, Specialization specialization, double fees) {
        Doctor doctor = new Doctor(age, firstName, lastName, gender);
        doctor.setSpecialization(specialization);
        doctor.setFees(fees);

        if (!validator.validateDoctor(doctor)) {
            throw new IllegalArgumentException("Invalid doctor data provided");
        }

        return doctor;
    }
}
