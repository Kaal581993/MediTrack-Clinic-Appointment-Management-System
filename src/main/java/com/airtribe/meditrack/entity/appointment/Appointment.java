package com.airtribe.meditrack.entity.appointment;

import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.entity.id_generators.IdGenerators;

import java.util.Date;
import java.util.Objects;

/**
 * A scheduled consultation between a {@link Doctor} and a {@link Patient}.
 * <p>
 * Instances are created either through the public constructor or through
 * {@link AppointmentBuilder}. The appointment id is assigned automatically by the
 * {@link IdGenerators} singleton and is never reassigned, which makes it the
 * natural identity for {@link #equals(Object)} and {@link #hashCode()}.
 */
public class Appointment implements Cloneable {

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

    /**
     * Restores a previously generated id.
     * <p>
     * The constructor always mints a fresh id, which is right for a new booking but wrong
     * when rebuilding an appointment from persisted data — the stored id must survive a
     * save/load round trip. Intended for deserialisation only; do not use it to reassign
     * the id of a live appointment.
     *
     * @param appointment_id the persisted id
     */
    public void setAppointment_id(int appointment_id) {
        this.appointment_id = appointment_id;
    }

    /**
     * Updates the lifecycle state of this appointment. Required so the service layer
     * can cancel, reschedule or complete an appointment without recreating it (which
     * would churn the generated id).
     *
     * @param status the new status; must not be {@code null}
     */
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    /**
     * Moves this appointment to a new date/time. A defensive copy is stored so the
     * caller cannot mutate the appointment's internal state afterwards.
     *
     * @param appointment_date the new date; may be {@code null} only if the caller
     *                         intends to clear the slot
     */
    public void setAppointment_date(Date appointment_date) {
        this.appointment_date = (appointment_date == null) ? null : new Date(appointment_date.getTime());
    }

    /**
     * Creates a copy of this appointment with its mutable nested state deep-copied.
     * <p>
     * The nested {@link Date} is cloned, so mutating the copy's date (or calling
     * {@link #setAppointment_date(Date)} on it) leaves the original untouched — this is
     * the deep-vs-shallow distinction the copy demonstrates.
     * <p>
     * Note: {@link Doctor} and {@link Patient} are shared by reference, because neither
     * implements {@link Cloneable} yet (they are owned by the doctor/patient modules).
     * That is intentional for now — a doctor and a patient are shared identities rather
     * than values, so copying an appointment should not fork the person records. If
     * Patient later becomes Cloneable, clone it here to make the copy fully deep.
     *
     * @return a copy of this appointment
     */
    @Override
    public Appointment clone() {
        try {
            Appointment copy = (Appointment) super.clone();
            if (this.appointment_date != null) {
                copy.appointment_date = new Date(this.appointment_date.getTime());
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            // Unreachable: this class implements Cloneable.
            throw new AssertionError("Appointment must be cloneable", e);
        }
    }

    /**
     * Two appointments are the same appointment when they carry the same generated id.
     */
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
