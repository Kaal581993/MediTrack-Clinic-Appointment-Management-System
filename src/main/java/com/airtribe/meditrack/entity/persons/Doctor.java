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

    public static class DoctorBuilder {
        private int age;
        private String f_name;
        private String l_name;
        private Gender gender;
        private Specialization specialization;
        private double fees;

        public DoctorBuilder age(int age) {
            this.age = age;
            return this;
        }

        public DoctorBuilder f_name(String f_name) {
            this.f_name = f_name;
            return this;
        }

        public DoctorBuilder l_name(String l_name) {
            this.l_name = l_name;
            return this;
        }

        public DoctorBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public DoctorBuilder specialization(Specialization specialization) {
            this.specialization = specialization;
            return this;
        }

        public DoctorBuilder fees(double fees) {
            this.fees = fees;
            return this;
        }

        public Doctor build() {
            Doctor doctor = new Doctor(age, f_name, l_name, gender);
            doctor.specialization = specialization;
            doctor.fees = fees;
            return doctor;
        }
    }

}
