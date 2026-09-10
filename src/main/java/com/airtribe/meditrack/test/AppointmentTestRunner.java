package com.airtribe.meditrack.test;

import com.airtribe.meditrack.entity.appointment.Appointment;
import com.airtribe.meditrack.entity.appointment.AppointmentObserver;
import com.airtribe.meditrack.entity.appointment.AppointmentStatus;
import com.airtribe.meditrack.entity.appointment.AppointmentType;
import com.airtribe.meditrack.entity.persons.Doctor;
import com.airtribe.meditrack.entity.persons.Gender;
import com.airtribe.meditrack.entity.persons.Patient;
import com.airtribe.meditrack.entity.persons.Specialization;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.util.DateUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AppointmentTestRunner {

    private static int passed;
    private static int failed;

    private static final long ONE_DAY = 24L * 60 * 60 * 1000;

    public static void main(String[] args) {
        System.out.println("===== MediTrack Appointment Module — Manual Tests =====\n");

        testBookAndRetrieve();
        testValidationRejectsBadInput();
        testNotFoundThrows();
        testCancel();
        testReschedule();
        testCompleteAndGuards();
        testOverloadedSearches();
        testDoubleBookingRejected();
        testDeepClone();
        testObserverFires();
        testStreamsAnalytics();
        testSearchableContract();
        testCsvRoundTrip();

        System.out.println("\n===== Summary =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");

        if (failed > 0) {
            System.exit(1);
        }
    }


    private static void testBookAndRetrieve() {
        section("Booking and retrieval");
        AppointmentService service = new AppointmentService();
        Appointment booked = service.bookAppointment(
                future(1), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient("Neha", "Sharma"));

        check("booking returns an appointment", booked != null);
        check("appointment gets a generated id", booked.getAppointment_id() > 0);
        check("new appointment starts PENDING", booked.getStatus() == AppointmentStatus.PENDING);
        check("default INITIAL fee applied", booked.getAppointmentFees() == 500.0);
        check("appointment is retrievable by id",
                service.viewAppointment(booked.getAppointment_id()) == booked);
        check("service holds exactly one appointment", service.listAll().size() == 1);
    }

    private static void testValidationRejectsBadInput() {
        section("Validation");
        AppointmentService service = new AppointmentService();
        Doctor doctor = doctor("Asha", "Rao");
        Patient patient = patient("Neha", "Sharma");

        check("past date rejected", throwsIllegalArgument(() ->
                service.bookAppointment(past(1), AppointmentType.INITIAL, doctor, patient)));
        check("null doctor rejected", throwsIllegalArgument(() ->
                service.bookAppointment(future(1), AppointmentType.INITIAL, null, patient)));
        check("null patient rejected", throwsIllegalArgument(() ->
                service.bookAppointment(future(1), AppointmentType.INITIAL, doctor, null)));
        check("negative fee rejected", throwsIllegalArgument(() ->
                service.bookAppointment(future(1), AppointmentType.INITIAL, -50.0, doctor, patient)));
        check("nothing was stored after failed bookings", service.listAll().isEmpty());
    }

    private static void testNotFoundThrows() {
        section("Missing appointment");
        AppointmentService service = new AppointmentService();
        boolean thrown = false;
        String message = "";
        try {
            service.viewAppointment(9999);
        } catch (AppointmentNotFoundException e) {
            thrown = true;
            message = e.getMessage();
        }
        check("AppointmentNotFoundException is thrown", thrown);
        check("exception carries a useful message", message.contains("9999"));
        check("exception type survives (constructor calls super, does not throw)",
                new AppointmentNotFoundException("x") instanceof RuntimeException);
    }

    private static void testCancel() {
        section("Cancellation");
        AppointmentService service = new AppointmentService();
        Appointment booked = service.bookAppointment(
                future(2), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient("Neha", "Sharma"));

        service.cancelAppointment(booked.getAppointment_id());
        check("status flips to CANCELLED", booked.getStatus() == AppointmentStatus.CANCELLED);
        check("cancelled appointment is still stored", service.listAll().size() == 1);
        check("cancelled appointment is excluded from upcoming",
                service.upcomingAppointments().isEmpty());
    }

    private static void testReschedule() {
        section("Rescheduling");
        AppointmentService service = new AppointmentService();
        Appointment booked = service.bookAppointment(
                future(2), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient("Neha", "Sharma"));

        Date newDate = future(5);
        service.rescheduleAppointment(booked.getAppointment_id(), newDate);

        check("status flips to RESCHEDULED", booked.getStatus() == AppointmentStatus.RESCHEDULED);
        check("date is updated", DateUtil.isSameSlot(booked.getAppointment_date(), newDate));
        check("stored date is a defensive copy", booked.getAppointment_date() != newDate);
        check("rescheduling into the past is rejected", throwsIllegalArgument(() ->
                service.rescheduleAppointment(booked.getAppointment_id(), past(1))));
    }

    private static void testCompleteAndGuards() {
        section("Completion and state guards");
        AppointmentService service = new AppointmentService();
        Appointment booked = service.bookAppointment(
                future(2), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient("Neha", "Sharma"));

        service.completeAppointment(booked.getAppointment_id());
        check("status flips to COMPLETED", booked.getStatus() == AppointmentStatus.COMPLETED);

        boolean guarded = false;
        try {
            service.cancelAppointment(booked.getAppointment_id());
        } catch (IllegalStateException e) {
            guarded = true;
        }
        check("completed appointment cannot be cancelled", guarded);

        Appointment other = service.bookAppointment(
                future(3), AppointmentType.FOLLOWUP, doctor("Vikram", "Singh"), patient("Imran", "Khan"));
        service.cancelAppointment(other.getAppointment_id());
        boolean guardedComplete = false;
        try {
            service.completeAppointment(other.getAppointment_id());
        } catch (IllegalStateException e) {
            guardedComplete = true;
        }
        check("cancelled appointment cannot be completed", guardedComplete);
    }

    private static void testOverloadedSearches() {
        section("Overloaded search (compile-time polymorphism)");
        AppointmentService service = new AppointmentService();
        Doctor asha = doctor("Asha", "Rao");
        Appointment first = service.bookAppointment(
                future(2), AppointmentType.INITIAL, asha, patient("Neha", "Sharma"));
        Appointment second = service.bookAppointment(
                future(4), AppointmentType.FOLLOWUP, doctor("Vikram", "Singh"), patient("Imran", "Khan"));
        service.cancelAppointment(second.getAppointment_id());

        check("search by id finds the appointment",
                service.searchAppointment(first.getAppointment_id()).isPresent());
        check("search by unknown id is empty", service.searchAppointment(4242).isEmpty());
        check("search by patient name (case-insensitive)",
                service.searchAppointment("neha").size() == 1);
        check("search by partial last name", service.searchAppointment("Khan").size() == 1);
        check("search by unknown name is empty", service.searchAppointment("Nobody").isEmpty());
        check("search by status CANCELLED",
                service.searchAppointment(AppointmentStatus.CANCELLED).size() == 1);
        check("search by status PENDING",
                service.searchAppointment(AppointmentStatus.PENDING).size() == 1);
        check("search by date range covers both",
                service.searchAppointment(future(1), future(5)).size() == 2);
        check("search by narrow date range covers one",
                service.searchAppointment(future(1), future(3)).size() == 1);
        check("search by doctor id", service.searchByDoctorId(asha.getP_id()).size() == 1);
    }

    private static void testDoubleBookingRejected() {
        section("Double-booking guard");
        AppointmentService service = new AppointmentService();
        Doctor asha = doctor("Asha", "Rao");
        Date slot = future(2);
        service.bookAppointment(slot, AppointmentType.INITIAL, asha, patient("Neha", "Sharma"));

        check("same doctor, same slot is rejected", throwsIllegalArgument(() ->
                service.bookAppointment(slot, AppointmentType.INITIAL, asha, patient("Imran", "Khan"))));
        check("different doctor, same slot is allowed",
                service.bookAppointment(slot, AppointmentType.INITIAL,
                        doctor("Vikram", "Singh"), patient("Imran", "Khan")) != null);
        check("same doctor, different slot is allowed",
                service.bookAppointment(future(6), AppointmentType.FOLLOWUP,
                        asha, patient("Imran", "Khan")) != null);
        check("only the rejected booking was dropped", service.listAll().size() == 3);
    }

    private static void testDeepClone() {
        section("Deep vs shallow copy");
        AppointmentService service = new AppointmentService();
        Patient patient = patient("Neha", "Sharma");
        Appointment original = service.bookAppointment(
                future(2), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient);

        Appointment copy = original.clone();
        check("clone is a different object", copy != original);
        check("clone is equal by id (same appointment)", copy.equals(original));
        check("clone carries the same date value",
                DateUtil.isSameSlot(copy.getAppointment_date(), original.getAppointment_date()));
        check("nested Date is deep-copied, not shared",
                copy.getAppointment_date() != original.getAppointment_date());

        Date originalDate = new Date(original.getAppointment_date().getTime());
        copy.setAppointment_date(future(9));
        check("mutating the copy leaves the original date unchanged",
                DateUtil.isSameSlot(original.getAppointment_date(), originalDate));

        check("person records are shared by reference (documented shallow)",
                copy.getPatient() == original.getPatient());
    }

    private static void testObserverFires() {
        section("Observer pattern");
        AppointmentService service = new AppointmentService();
        final int[] counts = new int[4];

        service.register(new AppointmentObserver() {
            @Override public void onBooked(Appointment a) { counts[0]++; }
            @Override public void onCancelled(Appointment a) { counts[1]++; }
            @Override public void onRescheduled(Appointment a) { counts[2]++; }
            @Override public void onCompleted(Appointment a) { counts[3]++; }
        });
        check("observer is registered", service.observerCount() == 1);

        Appointment booked = service.bookAppointment(
                future(2), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient("Neha", "Sharma"));
        check("onBooked fired", counts[0] == 1);

        service.rescheduleAppointment(booked.getAppointment_id(), future(5));
        check("onRescheduled fired", counts[2] == 1);

        service.completeAppointment(booked.getAppointment_id());
        check("onCompleted fired", counts[3] == 1);

        Appointment second = service.bookAppointment(
                future(7), AppointmentType.FOLLOWUP, doctor("Vikram", "Singh"), patient("Imran", "Khan"));
        service.cancelAppointment(second.getAppointment_id());
        check("onCancelled fired", counts[1] == 1);
        check("onBooked fired twice in total", counts[0] == 2);
    }

    private static void testStreamsAnalytics() {
        section("Streams and lambdas");
        AppointmentService service = new AppointmentService();
        Doctor asha = doctor("Asha", "Rao");
        Doctor vikram = doctor("Vikram", "Singh");

        service.bookAppointment(future(2), AppointmentType.INITIAL, asha, patient("Neha", "Sharma"));
        service.bookAppointment(future(3), AppointmentType.INITIAL, asha, patient("Imran", "Khan"));
        Appointment cancelled = service.bookAppointment(
                future(4), AppointmentType.FOLLOWUP, vikram, patient("Ravi", "Menon"));
        service.bookAppointment(future(5), AppointmentType.FOLLOWUP, vikram, patient("Sara", "Iyer"));
        service.cancelAppointment(cancelled.getAppointment_id());

        Map<Integer, Long> perDoctor = service.appointmentsPerDoctor();
        check("appointments per doctor counts Asha's two", perDoctor.get(asha.getP_id()) == 2L);
        check("cancelled excluded from per-doctor count", perDoctor.get(vikram.getP_id()) == 1L);

        check("total revenue excludes cancelled", service.totalRevenue() == 1100.0);
        check("average fee excludes cancelled",
                Math.abs(service.averageFee() - (1100.0 / 3)) < 0.0001);

        Map<AppointmentStatus, Long> byStatus = service.countByStatus();
        check("count by status: 3 pending", byStatus.get(AppointmentStatus.PENDING) == 3L);
        check("count by status: 1 cancelled", byStatus.get(AppointmentStatus.CANCELLED) == 1L);
        check("filterByStatus agrees with countByStatus",
                service.filterByStatus(AppointmentStatus.PENDING).size() == 3);

        List<Appointment> sorted = service.sortedByDate();
        check("sortedByDate returns all four", sorted.size() == 4);
        check("sortedByDate is ascending",
                !sorted.get(0).getAppointment_date().after(sorted.get(3).getAppointment_date()));
        check("upcoming excludes the cancelled one", service.upcomingAppointments().size() == 3);
    }

    private static void testSearchableContract() {
        section("Searchable interface (default methods)");
        AppointmentService service = new AppointmentService();
        Appointment booked = service.bookAppointment(
                future(2), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient("Neha", "Sharma"));

        check("searchById returns the record", service.searchById(booked.getAppointment_id()).size() == 1);
        check("searchById on unknown id is empty", service.searchById(8888).isEmpty());
        check("default exists() is true for a known id", service.exists(booked.getAppointment_id()));
        check("default exists() is false for an unknown id", !service.exists(8888));
        check("default count() matches searchAll()", service.count() == service.searchAll().size());
    }

    private static void testCsvRoundTrip() {
        section("CSV persistence round trip");
        String path = new File(System.getProperty("java.io.tmpdir"),
                "meditrack-appointments-test.csv").getAbsolutePath();
        new File(path).delete();

        AppointmentService writer = new AppointmentService(path);
        Appointment first = writer.bookAppointment(
                future(2), AppointmentType.INITIAL, doctor("Asha", "Rao"), patient("Neha", "Sharma"));
        Appointment second = writer.bookAppointment(
                future(3), AppointmentType.FOLLOWUP, doctor("Vikram", "Singh"), patient("Imran", "Khan"));
        writer.cancelAppointment(second.getAppointment_id());

        int saved = writer.saveToFile();
        check("saveToFile reports two rows", saved == 2);
        check("CSV file exists on disk", new File(path).exists());

        AppointmentService reader = new AppointmentService(path);
        int loaded = reader.loadFromFile();
        check("loadFromFile reports two rows", loaded == 2);
        check("loaded ids are preserved", reader.exists(first.getAppointment_id()));

        Appointment restored = reader.viewAppointment(first.getAppointment_id());
        check("restored status survives", restored.getStatus() == AppointmentStatus.PENDING);
        check("restored type survives", restored.getType() == AppointmentType.INITIAL);
        check("restored fee survives", restored.getAppointmentFees() == 500.0);
        check("restored date survives (to the minute)",
                DateUtil.isSameSlot(restored.getAppointment_date(), first.getAppointment_date()));
        check("restored patient name survives",
                "Neha".equals(restored.getPatient().getF_name()));
        check("cancelled status survives the round trip",
                reader.viewAppointment(second.getAppointment_id()).getStatus()
                        == AppointmentStatus.CANCELLED);

        reader.loadFromFile();
        check("reloading does not duplicate rows", reader.listAll().size() == 2);

        new File(path).delete();
    }


    private static void section(String title) {
        System.out.println("-- " + title + " --");
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description);
        }
    }

    private static boolean throwsIllegalArgument(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static Date future(int days) {
        return new Date(System.currentTimeMillis() + days * ONE_DAY);
    }

    private static Date past(int days) {
        return new Date(System.currentTimeMillis() - days * ONE_DAY);
    }

    private static Doctor doctor(String firstName, String lastName) {
        Doctor.DoctorBuilder builder = Doctor.builder();
        builder.f_name(firstName).l_name(lastName).age(45).gender(Gender.OTHER);
        return builder.specialization(Specialization.MBBS).fees(500.0).build();
    }

    private static Patient patient(String firstName, String lastName) {
        Patient.PatientBuilder builder = Patient.builder();
        builder.f_name(firstName).l_name(lastName).age(30).gender(Gender.OTHER);
        return builder.medical_history("n/a").build();
    }
}
