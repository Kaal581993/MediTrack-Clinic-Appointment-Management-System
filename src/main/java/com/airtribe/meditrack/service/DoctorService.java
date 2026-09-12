package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Specialization;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.util.AIHelper;
import com.airtribe.meditrack.util.DataStore;

import java.util.List;
import java.util.Optional;

public class DoctorService {

    private final DataStore<Doctor> doctorStore = new DataStore<>();
    private final AIHelper aiHelper = new AIHelper();

    public void addDoctor(Doctor doctor) {
        doctorStore.add(String.valueOf(doctor.getDoc_id()), doctor);
    }

    public Doctor getDoctorById(int docId) {
        return doctorStore.get(String.valueOf(docId));
    }

    public List<Doctor> getAllDoctors() {
        return doctorStore.getAll();
    }

    public List<Doctor> searchDoctors(String searchTerm) {
        return doctorStore.filter(doctor -> doctor.matches(searchTerm));
    }

    public List<Doctor> getDoctorsBySpecialization(Specialization specialization) {
        return aiHelper.recommendDoctorsBySpecialization(doctorStore.getAll(), specialization);
    }

    public List<Doctor> getDoctorsByMaxFee(double maxFee) {
        return aiHelper.recommendDoctorsByMaxFee(doctorStore.getAll(), maxFee);
    }

    public List<Doctor> getDoctorsByName(String nameFragment) {
        return aiHelper.recommendDoctorsByName(doctorStore.getAll(), nameFragment);
    }

    public List<Doctor> getTopRatedDoctors(int limit) {
        return aiHelper.recommendTopRatedDoctors(doctorStore.getAll(), limit);
    }

    public double getAverageFee() {
        return aiHelper.computeAverageFee(doctorStore.getAll());
    }

    public List<Doctor> filterDoctors(Specialization specialization, double maxFee) {
        return aiHelper.filterDoctors(doctorStore.getAll(), specialization, maxFee);
    }

    public Doctor findDoctorById(int docId) {
        Doctor doctor = getDoctorById(docId);
        if (doctor == null) {
            throw new DoctorNotFoundException("Doctor with ID " + docId + " not found");
        }
        return doctor;
    }

    public Optional<Doctor> findDoctorByName(String firstName, String lastName) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.getF_name().equalsIgnoreCase(firstName) &&
                        doctor.getL_name().equalsIgnoreCase(lastName))
                .findFirst();
    }

    public long countDoctorsBySpecialization(Specialization specialization) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.getSpecialization() == specialization)
                .count();
    }

    public List<Doctor> getDoctorsSortedByFeeAscending() {
        return doctorStore.getAll().stream()
                .sorted((d1, d2) -> Double.compare(d1.getFees(), d2.getFees()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Doctor> getDoctorsSortedByFeeDescending() {
        return doctorStore.getAll().stream()
                .sorted((d1, d2) -> Double.compare(d2.getFees(), d1.getFees()))
                .collect(java.util.stream.Collectors.toList());
    }
}
