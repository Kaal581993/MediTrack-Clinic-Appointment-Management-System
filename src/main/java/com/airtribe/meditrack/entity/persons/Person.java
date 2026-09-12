package com.airtribe.meditrack.entity.persons;

import com.airtribe.meditrack.entity.id_generators.IdGenerators;

public abstract class Person {

    private int p_id;
    private int age;
    private String F_name;
    private String L_name;
    private Gender gender;

    IdGenerators id_gen = IdGenerators.getInstance();

    public Person() {
    }

    public Person(int age, String f_name, String l_name, Gender gender) {
        this.p_id = id_gen.PersonIDGenerator() ;
        this.age = age;
        F_name = f_name;
        L_name = l_name;
        this.gender = gender;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getF_name() {
        return F_name;
    }

    public void setF_name(String f_name) {
        F_name = f_name;
    }

    public String getL_name() {
        return L_name;
    }

    public void setL_name(String l_name) {
        L_name = l_name;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    // Builder pattern (does not alter existing constructors/logic).
    //
    // PersonBuilder is abstract because Person is abstract: it cannot instantiate a
    // Person directly. Concrete subclasses (DoctorBuilder, PatientBuilder) extend this
    // builder, inherit the fluent setters, and override build() to return a concrete
    // instance of their own type.
    public static abstract class PersonBuilder {
        protected int age;
        protected String f_name;
        protected String l_name;
        protected Gender gender;

        public PersonBuilder age(int age) {
            this.age = age;
            return this;
        }

        public PersonBuilder f_name(String f_name) {
            this.f_name = f_name;
            return this;
        }

        public PersonBuilder l_name(String l_name) {
            this.l_name = l_name;
            return this;
        }

        public PersonBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        /**
         * Builds a concrete Person subclass. Abstract here so subclasses must provide
         * their own implementation that returns a Doctor or Patient.
         */
        public abstract Person build();
    }
}
