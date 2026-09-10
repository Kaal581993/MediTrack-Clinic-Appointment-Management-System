package com.airtribe.meditrack.entity.persons;

import com.airtribe.meditrack.entity.MedicalEntity;
import com.airtribe.meditrack.entity.id_generators.IdGenerators;
import com.airtribe.meditrack.inter_face.Searchable;

public class Doctor extends Person implements Searchable, MedicalEntity {

    IdGenerators id_gen = IdGenerators.getInstance();
    private String entityId;
    private String name;

    public Doctor(int age, String f_name, String l_name, Gender gender) {
        super(age, f_name, l_name, gender);
        this.doc_id = id_gen.DocIdGenerator();
    }

    public Doctor() {
    }

    private int doc_id;
    private Specialization specialization;
    private double fees;

    public IdGenerators getId_gen() {
        return id_gen;
    }

    public int getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(int doc_id) {
        this.doc_id = doc_id;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    @Override
    public Doctor clone() {
        try {
            Doctor cloned = (Doctor) super.clone();
            cloned.specialization = this.specialization;
            cloned.fees = this.fees;
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported for Doctor", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Doctor doctor = (Doctor) o;
        return doc_id == doctor.doc_id &&
                Double.compare(doctor.fees, fees) == 0 &&
                specialization == doctor.specialization;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), doc_id, specialization, fees);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "doc_id=" + doc_id +
                ", specialization=" + specialization +
                ", fees=" + fees +
                ", p_id=" + getP_id() +
                ", age=" + getAge() +
                ", F_name='" + getF_name() + '\'' +
                ", L_name='" + getL_name() + '\'' +
                ", gender=" + getGender() +
                '}';
    }

    @Override
    public boolean matches(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return false;
        }
        String term = searchTerm.toLowerCase();
        return getF_name().toLowerCase().contains(term) ||
                getL_name().toLowerCase().contains(term) ||
                specialization.name().toLowerCase().contains(term);
    }

    @Override
    public String getSearchKey() {
        return getF_name() + " " + getL_name() + " " + specialization.name();
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

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
