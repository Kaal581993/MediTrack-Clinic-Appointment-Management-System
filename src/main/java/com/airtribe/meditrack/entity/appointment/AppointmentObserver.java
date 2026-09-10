package com.airtribe.meditrack.entity.appointment;

public interface AppointmentObserver {

    default void onBooked(Appointment appointment) {
    }

    default void onCancelled(Appointment appointment) {
    }

    default void onRescheduled(Appointment appointment) {
    }

    default void onCompleted(Appointment appointment) {
    }
}
