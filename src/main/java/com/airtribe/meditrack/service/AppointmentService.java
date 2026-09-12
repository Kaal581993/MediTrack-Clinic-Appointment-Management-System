package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentObserver;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.interfaces.Searchable;
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

public class AppointmentService implements Searchable {

    private final Map<Integer, Appointment> appointments = new LinkedHashMap<>();

    private final List<AppointmentObserver> observers = new ArrayList<>();

    private final String storagePath;

    private static final Map<AppointmentType, Double> DEFAULT_FEES;

    private static int instanceCount;

    static {
        Map<AppointmentType, Double> fees = new LinkedHashMap<>();
        fees.put(AppointmentType.INITIAL, Constants.INITIAL_APPOINTMENT_FEE);
        fees.put(AppointmentType.FOLLOWUP, Constants.FOLLOWUP_APPOINTMENT_FEE);
        DEFAULT_FEES = java.util.Collections.unmodifiableMap(fees);
        instanceCount = 0;
    }

    public AppointmentService() {
        this(Constants.APPOINTMENT_FILE_PATH);
    }

    public AppointmentService(String storagePath) {
        this.storagePath = storagePath;
        instanceCount++;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public static double defaultFeeFor(AppointmentType type) {
        return DEFAULT_FEES.getOrDefault(type, 0.0);
    }


    public void register(AppointmentObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public boolean unregister(AppointmentObserver observer) {
        return observers.remove(observer);
    }

    public int observerCount() {
        return observers.size();
    }


    public Appointment bookAppointment(Date date, AppointmentType type, Doctor doctor, Patient patient) {
        return bookAppointment(date, type, defaultFeeFor(type), doctor, patient);
    }

    public Appointment bookAppointment(Date date, AppointmentType type, double fees,
                                       Doctor doctor, Patient patient) {
        Validator.validateAppointment(date, type, fees, doctor, patient);
        if (isDoctorBusy(doctor, date)) {
            throw new IllegalArgumentException("Dr. " + doctor.getF_name() + " " + doctor.getL_name()
                    + " already has an appointment at " + DateUtil.formatDate(date) + ".");
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

    public void addExisting(Appointment appointment) {
        if (appointment != null) {
            appointments.put(appointment.getAppointment_id(), appointment);
        }
    }

    public Appointment viewAppointment(int appointmentId) {
        Appointment appointment = appointments.get(appointmentId);
        if (appointment == null) {
            throw AppointmentNotFoundException.forId(appointmentId);
        }
        return appointment;
    }

    public List<Appointment> listAll() {
        return new ArrayList<>(appointments.values());
    }

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

    public Appointment rescheduleAppointment(int appointmentId, Date newDate) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Appointment #" + appointmentId + " is "
                    + appointment.getStatus() + " and cannot be rescheduled.");
        }
        if (!DateUtil.isFuture(newDate)) {
            throw new IllegalArgumentException(
                    "New appointment date must be in the future, but was: " + DateUtil.formatDate(newDate));
        }
        if (isDoctorBusyExcluding(appointment.getDoctor(), newDate, appointmentId)) {
            throw new IllegalArgumentException("That doctor already has an appointment at "
                    + DateUtil.formatDate(newDate) + ".");
        }

        appointment.setAppointment_date(newDate);
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        observers.forEach(observer -> observer.onRescheduled(appointment));
        return appointment;
    }

    public Appointment confirmAppointment(int appointmentId) {
        Appointment appointment = viewAppointment(appointmentId);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Appointment #" + appointmentId + " is cancelled and cannot be confirmed.");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        return appointment;
    }

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

    public Appointment deleteAppointment(int appointmentId) {
        Appointment removed = appointments.remove(appointmentId);
        if (removed == null) {
            throw AppointmentNotFoundException.forId(appointmentId);
        }
        return removed;
    }


    public Optional<Appointment> searchAppointment(int appointmentId) {
        return Optional.ofNullable(appointments.get(appointmentId));
    }

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

    public List<Appointment> searchAppointment(AppointmentStatus status) {
        return appointments.values().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

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

    public List<Appointment> searchByDoctorId(int doctorId) {
        return appointments.values().stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getP_id() == doctorId)
                .collect(Collectors.toList());
    }

    private boolean matchesName(String candidate, String needle) {
        return candidate != null && candidate.toLowerCase().contains(needle);
    }



    public List<Appointment> searchById(int id) {
        Appointment appointment = appointments.get(id);
        List<Appointment> result = new ArrayList<>();
        if (appointment != null) {
            result.add(appointment);
        }
        return result;
    }

    public List<Appointment> searchAll() {
        return listAll();
    }

    @Override
    public boolean matches(String searchTerm) {
        return !searchAppointment(searchTerm).isEmpty();
    }

    @Override
    public String getSearchKey() {
        return "appointment";
    }

    @Override
    public boolean matchesId(int id) {
        return appointments.containsKey(id);
    }

    @Override
    public boolean matchesName(String name) {
        return !searchAppointment(name).isEmpty();
    }


    public Map<Integer, Long> appointmentsPerDoctor() {
        return appointments.values().stream()
                .filter(a -> a.getDoctor() != null)
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .collect(Collectors.groupingBy(a -> a.getDoctor().getP_id(), Collectors.counting()));
    }

    public double averageFee() {
        return appointments.values().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .mapToDouble(Appointment::getAppointmentFees)
                .average()
                .orElse(0.0);
    }

    public double totalRevenue() {
        return appointments.values().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .mapToDouble(Appointment::getAppointmentFees)
                .sum();
    }

    public Map<AppointmentStatus, Long> countByStatus() {
        return appointments.values().stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
    }

    public List<Appointment> filterByStatus(AppointmentStatus status) {
        return searchAppointment(status);
    }

    public List<Appointment> sortedByDate() {
        return appointments.values().stream()
                .sorted(Comparator.comparing(Appointment::getAppointment_date,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<Appointment> upcomingAppointments() {
        return appointments.values().stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .filter(a -> DateUtil.isFuture(a.getAppointment_date()))
                .sorted(Comparator.comparing(Appointment::getAppointment_date))
                .collect(Collectors.toList());
    }


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


    public int saveToFile() {
        AppointmentCSVUtil.save(storagePath, listAll());
        return appointments.size();
    }

    public int loadFromFile() {
        List<Appointment> loaded = AppointmentCSVUtil.load(storagePath);
        appointments.clear();
        loaded.forEach(this::addExisting);
        return loaded.size();
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void clear() {
        appointments.clear();
    }

    public boolean exists(int appointmentId) {
        return appointments.containsKey(appointmentId);
    }

    public int count() {
        return appointments.size();
    }
}
