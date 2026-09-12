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

    public Doctor getDoctorById(int docId) {
        return doctorStore.get(String.valueOf(docId));
    }

    public List<Doctor> getAllDoctors() {
        return doctorStore.getAll();
    }

    public List<Doctor> searchDoctors(String searchTerm) {
        return doctorStore.filter(doctor -> doctor.matches(searchTerm));
    }

    public Doctor searchDoctor(String searchTerm) {
        List<Doctor> results = searchDoctors(searchTerm);
        return results.isEmpty() ? null : results.get(0);
    }

    public Doctor searchDoctor(int id) {
        List<Doctor> results = doctorStore.getAll().stream()
                .filter(doctor -> doctor.matchesId(id))
                .toList();
        return results.isEmpty() ? null : results.get(0);
    }

    public boolean updateDoctor(Doctor doctor) {
        if (doctor == null || !doctorStore.contains(String.valueOf(doctor.getDoc_id()))) {
            return false;
        }
        doctorStore.add(String.valueOf(doctor.getDoc_id()), doctor);
        return true;
    }

    public boolean deleteDoctor(int docId) {
        if (!doctorStore.contains(String.valueOf(docId))) {
            return false;
        }
        doctorStore.remove(String.valueOf(docId));
        return true;
    }

    public double getDoctorFees(int docId) {
        Doctor doctor = getDoctorById(docId);
        if (doctor == null) {
            throw new DoctorNotFoundException("Doctor with ID " + docId + " not found");
        }
        return doctor.getFees();
    }

    public boolean updateDoctorFees(int docId, double newFees) {
        Doctor doctor = getDoctorById(docId);
        if (doctor == null) {
            return false;
        }
        doctor.setFees(newFees);
        return true;
    }

    public List<Doctor> getDoctorsBySpecialization(Specialization specialization) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.getSpecialization() == specialization)
                .toList();
    }

    public List<Doctor> getDoctorsByMaxFee(double maxFee) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.getFees() <= maxFee)
                .toList();
    }

    public List<Doctor> getDoctorsByName(String nameFragment) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.matchesName(nameFragment))
                .toList();
    }

    public List<Doctor> getTopRatedDoctors(int limit) {
        return doctorStore.getAll().stream()
                .limit(limit)
                .toList();
    }

    public double getAverageFee() {
        return doctorStore.getAll().stream()
                .mapToDouble(Doctor::getFees)
                .average()
                .orElse(0.0);
    }

    public List<Doctor> filterDoctors(Specialization specialization, double maxFee) {
        return doctorStore.getAll().stream()
                .filter(doctor -> doctor.getSpecialization() == specialization)
                .filter(doctor -> doctor.getFees() <= maxFee)
                .toList();
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
                .toList();
    }

    public List<Doctor> getDoctorsSortedByFeeDescending() {
        return doctorStore.getAll().stream()
                .sorted((d1, d2) -> Double.compare(d2.getFees(), d1.getFees()))
                .toList();
    }
}
