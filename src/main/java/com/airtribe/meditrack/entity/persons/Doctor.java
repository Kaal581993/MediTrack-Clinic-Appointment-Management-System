package com.airtribe.meditrack.entity.persons;

import com.airtribe.meditrack.entity.id_generators.IdGenerators;

public class Doctor extends Person{

    IdGenerators id_gen = IdGenerators.getInstance();
    public Doctor(int age, String f_name, String l_name, Gender gender) {
        super(age, f_name, l_name, gender);

        id_gen.DocIdGenerator();
    }

    public Doctor() {
    }

    private int doc_id;
    private Specialization specialization;
    private double fees;

    // Builder pattern (does not alter existing constructors/logic)
    public static DoctorBuilder builder() {
        return new DoctorBuilder();
    }

    // Extends PersonBuilder so DoctorBuilder inherits the fluent age/f_name/l_name/gender
    // setters and satisfies the abstract build() contract with a concrete Doctor.
    public static class DoctorBuilder extends PersonBuilder {
        private Specialization specialization;
        private double fees;

        public DoctorBuilder specialization(Specialization specialization) {
            this.specialization = specialization;
            return this;
        }

        public DoctorBuilder fees(double fees) {
            this.fees = fees;
            return this;
        }

        @Override
        public Doctor build() {
            Doctor doctor = new Doctor(age, f_name, l_name, gender);
            doctor.specialization = specialization;
            doctor.fees = fees;
            return doctor;
        }
    }

}
