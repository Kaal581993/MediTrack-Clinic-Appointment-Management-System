package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientService {
    private final DataStore<Patient> patientStore = new DataStore<>();

    public Patient registerPatient(Patient patient) throws InvalidDataException {
        if (!Validator.validatePatient(patient)) {
            throw new InvalidDataException("Invalid patient details. Please verify name, age, and gender.");
        }
        patientStore.put(patient.getPat_id(), patient);
        return patient;
    }

    // READ ALL
    public List<Patient> getAllPatients() {
        return patientStore.getAll();
    }

    // POLYMORPHIC OVERLOADING 1: By ID
    public Optional<Patient> searchPatient(int id) {
        return patientStore.get(id);
    }

    // POLYMORPHIC OVERLOADING 2: By Name (Streams + Lambdas)
    public List<Patient> searchPatient(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return patientStore.getAll().stream()
                .filter(p -> p.matchesName(name))
                .collect(Collectors.toList());
    }

    // POLYMORPHIC OVERLOADING 3: By Age (Streams + Lambdas)
    public List<Patient> searchPatientByAge(int age) {
        return patientStore.getAll().stream()
                .filter(p -> p.getAge() == age)
                .collect(Collectors.toList());
    }

    // UPDATE
    public Patient updateMedicalHistory(int patientId, String newHistory) throws InvalidDataException {
        Patient patient = patientStore.get(patientId)
                .orElseThrow(() -> new InvalidDataException("Patient with ID " + patientId + " not found."));
        patient.setMedical_history(newHistory);
        return patient;
    }

    // DELETE
    public boolean deletePatient(int patientId) {
        return patientStore.delete(patientId);
    }

    // Deep copy demonstration helper
    public Patient clonePatient(int patientId) throws InvalidDataException {
        Patient original = patientStore.get(patientId)
                .orElseThrow(() -> new InvalidDataException("Patient not found for cloning."));
        return original.clone();
    }
}