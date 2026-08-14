package com.airtribe.meditrack.entity;

public class Doctor extends Person{
    public Doctor(int p_id, int age, String f_name, String l_name, Gender gender) {
        super(p_id, age, f_name, l_name, gender);
    }
    private int doc_id;
    private Specialization specialization;
    double fees;

}
