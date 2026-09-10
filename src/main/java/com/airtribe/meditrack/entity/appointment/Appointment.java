package com.airtribe.meditrack.entity.appointment;

import com.airtribe.meditrack.entity.Constants;
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

    IdGenerators idgen = IdGenerators.getInstance();
    public Appointment(Date appointment_date, AppointmentStatus status, AppointmentType type, double appointmentFees,Doctor doctor, Patient patient) {
        this.appointment_id = idgen.NewAppointmentIdGenerator();
        this.appointment_date = appointment_date;
        this.appointmentFees = appointmentFees;
        this.status = status;
        this.type = type;
        this.doctor = doctor;
        this.patient = patient;
    }

    protected Appointment(Appointment other) {
        this.appointment_id = idgen.NewAppointmentIdGenerator(); // New ID
        this.appointment_date = new Date(other.appointment_date.getTime()); // Deep copy Date
        this.status = other.status; // Enum - safe to copy
        this.type = other.type; // Enum - safe to copy
        this.appointmentFees = other.appointmentFees; // Primitive
        this.doctor = other.doctor; // Share reference (add deep copy if needed)
        this.patient = other.patient; // Share reference (add deep copy if needed)
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

    public Constants getConstants() {
        return Constants.getInstance();
    }

    // Builder pattern (does not alter existing constructors/logic)
  public static class AppointmentBuilder {
        private Date appointment_date;
        private AppointmentStatus status;
        private AppointmentType type;
        private double appointmentFees;
        private Doctor doctor;
        private Patient patient;

        public AppointmentBuilder appointment_date(Date appointment_date) {
            this.appointment_date = appointment_date;
            return this;
        }

        public AppointmentBuilder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public AppointmentBuilder type(AppointmentType type) {
            this.type = type;
            return this;
        }

        public AppointmentBuilder appointmentFees(double appointmentFees) {
            this.appointmentFees = appointmentFees;
            return this;
        }

        public AppointmentBuilder doctor(Doctor doctor) {
            this.doctor = doctor;
            return this;
        }

        public AppointmentBuilder patient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public Appointment build() {
            return new Appointment(appointment_date, status, type, appointmentFees, doctor, patient);
        }
    }
}
