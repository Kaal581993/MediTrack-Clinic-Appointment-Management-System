package com.airtribe.meditrack.entity.appointment;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.entity.id_generators.IdGenerators;

import java.util.Date;

public class Appointment {

    private int appointment_id;
    private Date appointment_date;
    private AppointmentStatus status;
    private AppointmentType type;
    private double appointmentFees;
    private Doctor doctor;
    private Patient patient;

    IdGenerators idgen = new IdGenerators();
    public Appointment(Date appointment_date, AppointmentStatus status, AppointmentType type, double appointmentFees,Doctor doctor, Patient patient) {
        this.appointment_id = idgen.NewAppointmentIdGenerator();
        this.appointment_date = appointment_date;
        this.appointmentFees = appointmentFees;
        this.status = status;
        this.type = type;
        this.doctor = doctor;
        this.patient = patient;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public Date getAppointment_date() {
        return appointment_date;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public AppointmentType getType() {
        return type;
    }

    public double getAppointmentFees() {
        return appointmentFees;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public IdGenerators getIdgen() {
        return idgen;
    }
}
