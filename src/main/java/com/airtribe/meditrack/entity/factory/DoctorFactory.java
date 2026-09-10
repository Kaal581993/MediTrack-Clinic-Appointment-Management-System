package com.airtribe.meditrack.entity.factory;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Specialization;

public interface DoctorFactory {
    Doctor createDoctor(String firstName, String lastName, int age, Gender gender, Specialization specialization, double fees);
}
