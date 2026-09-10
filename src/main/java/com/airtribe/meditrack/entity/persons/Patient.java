package com.airtribe.meditrack.entity.persons;

import com.airtribe.meditrack.entity.idgenerators.IdGenerators;
import com.airtribe.meditrack.interfaces.Searchable;

import java.util.Objects;

public class Patient extends Person implements Cloneable, Searchable {
    private int pat_id;
    private String medical_history;

    public Patient() {
        super();
    }

    public Patient(int age, String f_name, String l_name, Gender gender) {
        this(age, f_name, l_name, gender, "None");
    }

    // Constructor chaining with super and this
    public Patient(int age, String f_name, String l_name, Gender gender, String medical_history) {
        super(age, f_name, l_name, gender);
        this.pat_id = IdGenerators.getInstance().PatientIDGenerator();
        this.medical_history = medical_history;
    }

    public int getPat_id() {
        return pat_id;
    }

    public void setPat_id(int pat_id) {
        this.pat_id = pat_id;
    }

    public String getMedical_history() {
        return medical_history;
    }

    public void setMedical_history(String medical_history) {
        this.medical_history = medical_history;
    }

    // Deep copy implementation
    @Override
    public Patient clone() {
        Patient cloned = (Patient) super.clone();
        // New String reference for deep copy isolation
        if (this.medical_history != null) {
            cloned.medical_history = this.medical_history;
        }
        return cloned;
    }

    @Override
    public boolean matches(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return false;
        }
        String term = searchTerm.toLowerCase();
        return getF_name().toLowerCase().contains(term) ||
                getL_name().toLowerCase().contains(term) ||
                medical_history.toLowerCase().contains(term);
    }

    @Override
    public String getSearchKey() {
        return getF_name() + " " + getL_name() + " " + medical_history;
    }

    @Override
    public boolean matchesId(int id) {
        return this.pat_id == id || getP_id() == id;
    }

    @Override
    public boolean matchesName(String name) {
        if (name == null) return false;
        String fullName = (getF_name() + " " + getL_name()).trim().toLowerCase();
        return fullName.contains(name.toLowerCase().trim());
    }

    @Override
    public String toString() {
        return "Patient [ID=" + pat_id + ", Name=" + getF_name() + " " + getL_name() +
                ", Age=" + getAge() + ", Gender=" + getGender() +
                ", History=" + medical_history + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return pat_id == patient.pat_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pat_id);
    }

    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    public static class PatientBuilder extends PersonBuilder {
        private String medical_history = "None";

        public PatientBuilder medical_history(String medical_history) {
            this.medical_history = medical_history;
            return this;
        }

        @Override
        public Patient build() {
            return new Patient(age, f_name, l_name, gender, medical_history);
        }
    }
}