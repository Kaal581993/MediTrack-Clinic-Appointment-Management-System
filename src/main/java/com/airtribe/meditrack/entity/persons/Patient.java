package com.airtribe.meditrack.entity.persons;

import com.airtribe.meditrack.entity.id_generators.IdGenerators;

public class Patient extends Person{

    int pat_id;
    private String medical_history;

    IdGenerators id_gen = IdGenerators.getInstance();

    public Patient(int age, String f_name, String l_name, Gender gender) {
        super(age, f_name, l_name, gender);
        this.pat_id  =id_gen.PatientIDGenerator();
    }

    public Patient() {
        super();
    }


    public int getPat_id() {
        return pat_id;
    }

    public String getMedical_history() {
        return medical_history;
    }

    public void setMedical_history(String medical_history) {
        this.medical_history = medical_history;
    }
// We need to implement object cloning for this

    // Builder pattern (does not alter existing constructors/logic)
    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    public static class PatientBuilder {
        private int age;
        private String f_name;
        private String l_name;
        private Gender gender;
        private String medical_history;

        public PatientBuilder age(int age) {
            this.age = age;
            return this;
        }

        public PatientBuilder f_name(String f_name) {
            this.f_name = f_name;
            return this;
        }

        public PatientBuilder l_name(String l_name) {
            this.l_name = l_name;
            return this;
        }

        public PatientBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public PatientBuilder medical_history(String medical_history) {
            this.medical_history = medical_history;
            return this;
        }

        public Patient build() {
            Patient patient = new Patient(age, f_name, l_name, gender);
            patient.medical_history = medical_history;
            return patient;
        }
    }
}
