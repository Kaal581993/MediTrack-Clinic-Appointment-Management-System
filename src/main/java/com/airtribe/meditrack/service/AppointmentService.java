package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentObserver;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.inter_face.Searchable;
import com.airtribe.meditrack.util.AppointmentCSVUtil;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages the appointment lifecycle: booking, viewing, searching, cancelling,
 * rescheduling and completing, plus CSV persistence and simple analytics.
 * <p>
 * Records live in a {@link LinkedHashMap} keyed by appointment id, which gives O(1)
 * lookup by id while preserving insertion order for listings. Observers registered via
 * {@link #register(AppointmentObserver)} are notified on every state change.
 */
public class AppointmentService implements Searchable<Appointment> {

    /** Storage keyed by appointment id — O(1) lookup, insertion-ordered iteration. */
    private final Map<Integer, Appointment> appointments = new LinkedHashMap<>();

    /** Registered notification listeners. */
    private final List<AppointmentObserver> observers = new ArrayList<>();

    /** Where {@link #saveToFile()} and {@link #loadFromFile()} read and write. */
    private final String storagePath;

    /** Default fees per appointment type, populated once in the static block below. */
    private static final Map<AppointmentType, Double> DEFAULT_FEES;

    /** Number of services created in this JVM — demonstrates static initialisation. */
    private static int instanceCount;

    // Static initialisation block: runs once when the class is first loaded, before any
    // instance exists. Used here to build the immutable default-fee table.
    static {
        Map<AppointmentType, Double> fees = new LinkedHashMap<>();
        fees.put(AppointmentType.INITIAL, Constants.INITIAL_APPOINTMENT_FEE);
        fees.put(AppointmentType.FOLLOWUP, Constants.FOLLOWUP_APPOINTMENT_FEE);
        DEFAULT_FEES = java.util.Collections.unmodifiableMap(fees);
        instanceCount = 0;
    }

    /** Creates a service persisting to the default appointment file. */
    public AppointmentService() {
        this(Constants.APPOINTMENT_FILE_PATH);
    }

    /**
     * @param storagePath file this service reads from and writes to
     */
    public AppointmentService(String storagePath) {
        this.storagePath = storagePath;
        instanceCount++;
    }

    /** @return how many services have been created in this JVM */
    public static int getInstanceCount() {
        return instanceCount;
    }

    /**
     * @param type the appointment type
     * @return the standard fee for that type, or {@code 0.0} when unknown
     */
    public static double defaultFeeFor(AppointmentType type) {
        return DEFAULT_FEES.getOrDefault(type, 0.0);
    }

    // ------------------------------------------------------------------
    // Observer registration
    // ------------------------------------------------------------------

    /**
     * @param observer listener to notify on lifecycle changes; {@code null} is ignored
     */
    public void register(AppointmentObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    /**
     * @param observer listener to stop notifying
     * @return {@code true} when the observer was registered
     */
    public boolean unregister(AppointmentObserver observer) {
        return observers.remove(observer);
    }

    /** @return how many observers are currently registered */
    public int observerCount() {
        return observers.size();
    }

    // ------------------------------------------------------------------
    // CRUD
    // ------------------------------------------------------------------

    /**
     * Books an appointment at the standard fee for its type.
     *
     * @param date    requested slot, must be in the future
     * @param type    INITIAL or FOLLOWUP
     * @param doctor  the doctor
     * @param patient the patient
     * @return the stored appointment
     * @throws IllegalArgumentException when validation fails or the doctor is double-booked
     */
    public Appointment bookAppointment(Date date, AppointmentType type, Doctor doctor, Patient patient) {
        return bookAppointment(date, type, defaultFeeFor(type), doctor, patient);
    }

    /**
     * Books an appointment with an explicit fee. The new appointment starts as
     * {@link AppointmentStatus#PENDING} and registered observers are notified.
     *
     * @param date    requested slot, must be in the future
     * @param type    INITIAL or FOLLOWUP
     * @param fees    consultation fee, must not be negative
     * @param doctor  the doctor
     * @param patient the patient
     * @return the stored appointment
     * @throws IllegalArgumentException when validation fails or the doctor is double-booked
     */
    public Appointment bookAppointment(Date date, AppointmentType type, double fees,
                                       Doctor doctor, Patient patient) {
        Validator.validateAppointment(date, type, fees, doctor, patient);
        if (isDoctorBusy(doctor, date)) {
            throw new IllegalArgumentException("Dr. " + doctor.getF_name() + " " + doctor.getL_name()
                    + " already has an appointment at " + DateUtil.format(date) + ".");
        }

        Appointment appointment = new Appointment.AppointmentBuilder()
                .appointment_date(date)
                .status(AppointmentStatus.PENDING)
                .type(type)
                .appointmentFees(fees)
                .doctor(doctor)
                .patient(patient)
                .build();

        appointments.put(appointment.getAppointment_id(), appointment);
        observers.forEach(observer -> observer.onBooked(appointment));
        return appointment;
    }

    /**
     * Adds an already-constructed appointment, e.g. one rebuilt from CSV. No observer
     * notification is fired, since this is a restore rather than a new booking.
     *
     * @param appointment the appointment to store
     */
    public void addExisting(Appointment appointment) {
        if (appointment != null) {
            appointments.put(appointment.getAppointment_id(), appointment);
        }
    }

    /**
     * @param appointmentId the id to look up
     * @return the appointment
     * @throws AppointmentNotFoundException when no appointment carries that id
     */
    public Appointment viewAppointment(int appointmentId) {
        Appointment appointment = appointments.get(appointmentId);
        if (appointment == null) {
            throw AppointmentNotFoundException.forId(appointmentId);
        }
        return appointment;
    }

    /** @return every appointment, in booking order */
    public List<Appointment> listAll() {
        return new ArrayList<>(appointments.values());
    }

    /**
     * Cancels an appointment and notifies observers.
     *
     * @param appointmentId the appointment to cancel
     * @return the cancelled appointment
     * @throws AppointmentNotFoundException when no appointment carries that id
     * @throws IllegalStateException        when the appointment is already completed
     */
    public Appointment cancelAppointment(int appointmentId) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Appointment #" + appointmentId + " is already completed and cannot be cancelled.");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        observers.forEach(observer -> observer.onCancelled(appointment));
        return appointment;
    }

    /**
     * Moves an appointment to a new slot and notifies observers.
     *
     * @param appointmentId the appointment to move
     * @param newDate       the new slot, must be in the future
     * @return the rescheduled appointment
     * @throws AppointmentNotFoundException when no appointment carries that id
     * @throws IllegalStateException        when the appointment is cancelled or completed
     * @throws IllegalArgumentException     when the date is invalid or the doctor is busy
     */
    public Appointment rescheduleAppointment(int appointmentId, Date newDate) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Appointment #" + appointmentId + " is "
                    + appointment.getStatus() + " and cannot be rescheduled.");
        }
        if (!DateUtil.isFuture(newDate)) {
            throw new IllegalArgumentException(
                    "New appointment date must be in the future, but was: " + DateUtil.format(newDate));
        }
        if (isDoctorBusyExcluding(appointment.getDoctor(), newDate, appointmentId)) {
            throw new IllegalArgumentException("That doctor already has an appointment at "
                    + DateUtil.format(newDate) + ".");
        }

        appointment.setAppointment_date(newDate);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        observers.forEach(observer -> observer.onRescheduled(appointment));
        return appointment;
    }

    /**
     * Confirms a pending appointment.
     *
     * @param appointmentId the appointment to confirm
     * @return the confirmed appointment
     * @throws AppointmentNotFoundException when no appointment carries that id
     */
    public Appointment confirmAppointment(int appointmentId) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Appointment #" + appointmentId + " is cancelled and cannot be confirmed.");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointment;
    }

    /**
     * Marks an appointment as completed and notifies observers.
     *
     * @param appointmentId the appointment to complete
     * @return the completed appointment
     * @throws AppointmentNotFoundException when no appointment carries that id
     * @throws IllegalStateException        when the appointment was cancelled
     */
    public Appointment completeAppointment(int appointmentId) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Appointment #" + appointmentId + " is cancelled and cannot be completed.");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        observers.forEach(observer -> observer.onCompleted(appointment));
        return appointment;
    }

    /**
     * Permanently removes an appointment from storage.
     *
     * @param appointmentId the appointment to delete
     * @return the removed appointment
     * @throws AppointmentNotFoundException when no appointment carries that id
     */
    public Appointment deleteAppointment(int appointmentId) {
        Appointment removed = appointments.remove(appointmentId);
        if (removed == null) {
            throw AppointmentNotFoundException.forId(appointmentId);
        }
        return removed;
    }

    // ------------------------------------------------------------------
    // Search — method overloading (compile-time polymorphism)
    // ------------------------------------------------------------------

    /**
     * Overload 1 — search by appointment id.
     *
     * @param appointmentId the id to match
     * @return the matching appointment, or empty when absent
     */
    public Optional<Appointment> searchAppointment(int appointmentId) {
        return Optional.ofNullable(appointments.get(appointmentId));
    }

    /**
     * Overload 2 — search by patient name (case-insensitive, matches first or last name).
     *
     * @param patientName the name fragment to match
     * @return every matching appointment
     */
    public List<Appointment> searchAppointment(String patientName) {
        if (!Validator.isValidName(patientName)) {
            return new ArrayList<>();
        }
        String needle = patientName.trim().toLowerCase();
        return appointments.values().stream()
                .filter(a -> a.getPatient() != null)
                .filter(a -> matchesName(a.getPatient().getF_name(), needle)
                        || matchesName(a.getPatient().getL_name(), needle))
                .collect(Collectors.toList());
    }

    /**
     * Overload 3 — search by lifecycle status.
     *
     * @param status the status to match
     * @return every matching appointment
     */
    public List<Appointment> searchAppointment(AppointmentStatus status) {
        return appointments.values().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Overload 4 — search by date range, inclusive at both ends.
     *
     * @param from start of the range
     * @param to   end of the range
     * @return every appointment falling inside the range, earliest first
     */
    public List<Appointment> searchAppointment(Date from, Date to) {
        if (from == null || to == null) {
            return new ArrayList<>();
        }
        return appointments.values().stream()
                .filter(a -> a.getAppointment_date() != null)
                .filter(a -> !a.getAppointment_date().before(from) && !a.getAppointment_date().after(to))
                .sorted(Comparator.comparing(Appointment::getAppointment_date))
                .collect(Collectors.toList());
    }

    /**
     * Finds every appointment belonging to one doctor.
     *
     * @param doctorId the doctor's person id
     * @return every matching appointment
     */
    public List<Appointment> searchByDoctorId(int doctorId) {
        return appointments.values().stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getP_id() == doctorId)
                .collect(Collectors.toList());
    }

    private boolean matchesName(String candidate, String needle) {
        return candidate != null && candidate.toLowerCase().contains(needle);
    }

    // ------------------------------------------------------------------
    // Searchable contract
    // ------------------------------------------------------------------

    @Override
    public List<Appointment> searchById(int id) {
        Appointment appointment = appointments.get(id);
        List<Appointment> result = new ArrayList<>();
        if (appointment != null) {
            result.add(appointment);
        }
        return result;
    }

    @Override
    public List<Appointment> searchAll() {
        return listAll();
    }

    // ------------------------------------------------------------------
    // Analytics — streams and lambdas
    // ------------------------------------------------------------------

    /**
     * @return appointment count per doctor id, cancelled appointments excluded
     */
    public Map<Integer, Long> appointmentsPerDoctor() {
        return appointments.values().stream()
                .filter(a -> a.getDoctor() != null)
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .collect(Collectors.groupingBy(a -> a.getDoctor().getP_id(), Collectors.counting()));
    }

    /**
     * @return mean fee across non-cancelled appointments, or {@code 0.0} when there are none
     */
    public double averageFee() {
        return appointments.values().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .mapToDouble(Appointment::getAppointmentFees)
                .average()
                .orElse(0.0);
    }

    /**
     * @return total fees billed across non-cancelled appointments
     */
    public double totalRevenue() {
        return appointments.values().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .mapToDouble(Appointment::getAppointmentFees)
                .sum();
    }

    /**
     * @return how many appointments sit in each status
     */
    public Map<AppointmentStatus, Long> countByStatus() {
        return appointments.values().stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
    }

    /**
     * @param status the status to keep
     * @return matching appointments
     */
    public List<Appointment> filterByStatus(AppointmentStatus status) {
        return searchAppointment(status);
    }

    /**
     * @return every appointment ordered by date, earliest first; undated ones last
     */
    public List<Appointment> sortedByDate() {
        return appointments.values().stream()
                .sorted(Comparator.comparing(Appointment::getAppointment_date,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * @return appointments still ahead of now and not cancelled, earliest first
     */
    public List<Appointment> upcomingAppointments() {
        return appointments.values().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> DateUtil.isFuture(a.getAppointment_date()))
                .sorted(Comparator.comparing(Appointment::getAppointment_date))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Slot availability
    // ------------------------------------------------------------------

    /**
     * @param doctor the doctor to check
     * @param date   the slot to check
     * @return {@code true} when the doctor already has a live appointment in that slot
     */
    public boolean isDoctorBusy(Doctor doctor, Date date) {
        return isDoctorBusyExcluding(doctor, date, -1);
    }

    private boolean isDoctorBusyExcluding(Doctor doctor, Date date, int excludedId) {
        if (doctor == null || date == null) {
            return false;
        }
        return appointments.values().stream()
                .filter(a -> a.getAppointment_id() != excludedId)
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> a.getDoctor() != null && a.getDoctor().getP_id() == doctor.getP_id())
                .anyMatch(a -> DateUtil.isSameSlot(a.getAppointment_date(), date));
    }

    // ------------------------------------------------------------------
    // Persistence
    // ------------------------------------------------------------------

    /**
     * Writes every appointment to the CSV file this service was configured with.
     *
     * @return how many appointments were written
     */
    public int saveToFile() {
        AppointmentCSVUtil.save(storagePath, listAll());
        return appointments.size();
    }

    /**
     * Replaces in-memory state with whatever is in the CSV file.
     * <p>
     * Doctor and patient records are rebuilt from the names stored in the row, since the
     * doctor and patient modules do not yet expose a shared registry to resolve ids
     * against. Once they do, pass their lookups to
     * {@link AppointmentCSVUtil#load(String, java.util.function.IntFunction, java.util.function.IntFunction)}.
     *
     * @return how many appointments were loaded
     */
    public int loadFromFile() {
        List<Appointment> loaded = AppointmentCSVUtil.load(storagePath);
        appointments.clear();
        loaded.forEach(this::addExisting);
        return loaded.size();
    }

    /** @return the CSV path this service reads from and writes to */
    public String getStoragePath() {
        return storagePath;
    }

    /** Discards every in-memory appointment; the file on disk is untouched. */
    public void clear() {
        appointments.clear();
    }
}
