package com.airtribe.meditrack.entity.appointment;

import com.airtribe.meditrack.entity.Constants;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.entity.idgenerators.IdGenerators;

import java.util.Date;
import java.util.Objects;

public class Appointment implements Cloneable {

    private int appointment_id;
    private Date appointment_date;
    private AppointmentStatus status;
    private AppointmentType type;
    private double appointmentFees;
    private Doctor doctor;
    private Patient patient;

    IdGenerators idgen = IdGenerators.getInstance();
    private Constants constants;

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

    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setAppointment_date(Date appointment_date) {
        this.appointment_date = (appointment_date == null) ? null : new Date(appointment_date.getTime());
    }

    @Override
    public Appointment clone() {
        try {
            Appointment copy = (Appointment) super.clone();
            if (this.appointment_date != null) {
                copy.appointment_date = new Date(this.appointment_date.getTime());
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Appointment must be cloneable", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        Appointment that = (Appointment) o;
        return this.appointment_id == that.appointment_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointment_id);
    }

    @Override
    public String toString() {
        String doctorName = (doctor == null) ? "unassigned" : doctor.getF_name() + " " + doctor.getL_name();
        String patientName = (patient == null) ? "unassigned" : patient.getF_name() + " " + patient.getL_name();
        return "Appointment{" +
                "id=" + appointment_id +
                ", date=" + appointment_date +
                ", status=" + status +
                ", type=" + type +
                ", fees=" + appointmentFees +
                ", doctor=" + doctorName +
                ", patient=" + patientName +
                '}';
    }

    public Constants getConstants() {
        return this.constants;
    }

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
