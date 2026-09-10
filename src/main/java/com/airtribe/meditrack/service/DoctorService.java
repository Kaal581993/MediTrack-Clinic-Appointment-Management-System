package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Specialization;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.util.DataStore;

import java.util.List;
import java.util.Optional;

public class DoctorService {

    private final DataStore<Doctor> doctorStore = new DataStore<>();

    public void addDoctor(Doctor doctor) {
        doctorStore.add(String.valueOf(doctor.getDoc_id()), doctor);
    }

    public Doctor getDoctorById(int doctorId) {
        Doctor doctor = doctorStore.get(String.valueOf(doctorId));
        if (doctor == null) {
            throw new DoctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        return doctor;
    }

    public List<Doctor> getAllDoctors() {
        return doctorStore.getAll();
    }

    public Doctor updateDoctor(int doctorId, Doctor updatedDoctor) {
        Doctor existingDoctor = getDoctorById(doctorId);
        if (existingDoctor == null) {
            throw new DoctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }

        existingDoctor.setF_name(updatedDoctor.getF_name());
        existingDoctor.setL_name(updatedDoctor.getL_name());
        existingDoctor.setAge(updatedDoctor.getAge());
        existingDoctor.setGender(updatedDoctor.getGender());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setFees(updatedDoctor.getFees());

        return existingDoctor;
    }

    public void deleteDoctor(int doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor == null) {
            throw new DoctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        doctorStore.remove(String.valueOf(doctorId));
    }

    public List<Doctor> getDoctorsBySpecialization(Specialization specialization) {
        return doctorStore.filter(doctor -> doctor.getSpecialization() == specialization);
    }

    public List<Doctor> searchDoctor(String name) {
        if (name == null || name.isEmpty()) {
            return doctorStore.getAll();
        }
        String lowerName = name.toLowerCase();
        return doctorStore.filter(doctor ->
                doctor.getF_name().toLowerCase().contains(lowerName) ||
                doctor.getL_name().toLowerCase().contains(lowerName)
        );
    }

    public List<Doctor> searchDoctor(int age) {
        return doctorStore.filter(doctor -> doctor.getAge() == age);
    }

    public double getDoctorFees(int doctorId) {
        Doctor doctor = getDoctorById(doctorId);
        return doctor.getFees();
    }

    public Doctor updateDoctorFees(int doctorId, double newFees) {
        Doctor doctor = getDoctorById(doctorId);
        if (doctor == null) {
            throw new DoctorNotFoundException("Doctor with ID " + doctorId + " not found");
        }
        doctor.setFees(newFees);
        return doctor;
    }
}
