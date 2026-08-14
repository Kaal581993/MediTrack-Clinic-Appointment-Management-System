package com.airtribe.meditrack.entity;

public class Patient extends Person{

    int p_id;
    String medical_history;

    public Patient(int p_id, int age, String f_name, String l_name, Gender gender) {
        super(p_id, age, f_name, l_name, gender);
    }

    @Override
    public int getP_id() {
        return p_id;
    }

    @Override
    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getMedical_history() {
        return medical_history;
    }

    public void setMedical_history(String medical_history) {
        this.medical_history = medical_history;
    }
// We need to implement object cloning for this
}
