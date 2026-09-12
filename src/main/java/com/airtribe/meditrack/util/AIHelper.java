package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Specialization;

import java.util.List;
import java.util.stream.Collectors;

public class AIHelper {

    public List<Doctor> recommendDoctorsBySpecialization(List<Doctor> doctors, Specialization specialization) {
        return doctors.stream()
                .filter(doctor -> doctor.getSpecialization() == specialization)
                .collect(Collectors.toList());
    }

    public List<Doctor> recommendDoctorsByMaxFee(List<Doctor> doctors, double maxFee) {
        return doctors.stream()
                .filter(doctor -> doctor.getFees() <= maxFee)
                .collect(Collectors.toList());
    }

    public List<Doctor> recommendDoctorsByName(List<Doctor> doctors, String nameFragment) {
        if (nameFragment == null || nameFragment.isEmpty()) {
            return doctors;
        }
        String lowerFragment = nameFragment.toLowerCase();
        return doctors.stream()
                .filter(doctor -> doctor.getF_name().toLowerCase().contains(lowerFragment) ||
                        doctor.getL_name().toLowerCase().contains(lowerFragment))
                .collect(Collectors.toList());
    }

    public List<Doctor> recommendTopRatedDoctors(List<Doctor> doctors, int limit) {
        return doctors.stream()
                .sorted((d1, d2) -> Double.compare(d2.getFees(), d1.getFees()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double computeAverageFee(List<Doctor> doctors) {
        return doctors.stream()
                .mapToDouble(Doctor::getFees)
                .average()
                .orElse(0.0);
    }

    public List<Doctor> filterDoctors(List<Doctor> doctors, Specialization specialization, double maxFee) {
        return doctors.stream()
                .filter(doctor -> doctor.getSpecialization() == specialization)
                .filter(doctor -> doctor.getFees() <= maxFee)
                .collect(Collectors.toList());
    }
}
