package com.airtribe.meditrack.entity.persons;

import com.airtribe.meditrack.entity.id_generators.IdGenerators;

public class Patient extends Person{

    int pat_id;
    private String medical_history;

    IdGenerators id_gen = new IdGenerators();

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
}
