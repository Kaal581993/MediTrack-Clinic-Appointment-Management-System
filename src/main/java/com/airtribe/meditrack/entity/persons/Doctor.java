package com.airtribe.meditrack.entity.persons;

import com.airtribe.meditrack.entity.id_generators.IdGenerators;

public class Doctor extends Person{

    IdGenerators id_gen=new IdGenerators();
    public Doctor(int age, String f_name, String l_name, Gender gender) {
        super(age, f_name, l_name, gender);

        id_gen.DocIdGenerator();
    }

    public Doctor() {
    }

    private int doc_id;
    private Specialization specialization;
    private double fees;

}
