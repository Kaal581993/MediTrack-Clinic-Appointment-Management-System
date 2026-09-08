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

    // Extends PersonBuilder so PatientBuilder inherits the fluent age/f_name/l_name/gender
    // setters and satisfies the abstract build() contract with a concrete Patient.
    public static class PatientBuilder extends PersonBuilder {
        private String medical_history;

        public PatientBuilder medical_history(String medical_history) {
            this.medical_history = medical_history;
            return this;
        }

        @Override
        public Patient build() {
            Patient patient = new Patient(age, f_name, l_name, gender);
            patient.medical_history = medical_history;
            return patient;
        }
    }
}
